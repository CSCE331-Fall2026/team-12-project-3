package com.csce331.controllers;

import java.sql.SQLException;

import java.util.List;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;

import com.csce331.managers.InventoryManager;
import com.csce331.models.Ingredient;
import com.csce331.models.Utensil;
import com.csce331.models.InventoryLocation;
import com.csce331.models.IngredientRestockData;
import com.csce331.requests.LocationRequest;
import com.csce331.requests.RestockRequest;

/**
 * Handles the inventory HTTP endpoints: ingredients and utensils by storage
 * location, restocking, restock thresholds, end of day, and restock reports.
 *
 * <p>Each method takes the Javalin request context, delegates the work to an
 * {@link InventoryManager}, and writes the status code and body of the
 * response itself. Query failures are reported to the client as a 500 with
 * the error message rather than being thrown.
 */
public class InventoryController {
    private static final InventoryManager inventoryManager = new InventoryManager();

    /**
     * Prevents instantiation; the handlers are all static.
     */
    private InventoryController() {
    }

    /**
     * Responds with every ingredient in the inventory as JSON.
     *
     * <p>Responds 500 with an error message if the database query fails.
     *
     * @param ctx the request context
     */
    @OpenApi(
        path = "/manager/ingredients",
        methods = HttpMethod.GET,
        operationId = "getAllIngredients",
        summary = "List every ingredient",
        description = "Returns every ingredient in the inventory as JSON. "
            + "Responds with 500 and an error message if the database query fails.",
        tags = {"Inventory"},
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "Every ingredient, including the sum of its quantities "
                    + "across all inventory records.",
                content = @OpenApiContent(from = Ingredient[].class)
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void allIngredients(Context ctx) {
        try {
            List<Ingredient> ingredients = inventoryManager.getIngredients();
            ctx.status(200).json(ingredients);
        } catch (SQLException e) {
            ctx.status(500).result("Error retrieving ingredients: " + e.getMessage());
        }
    }

    /**
     * Responds with the ingredients stored in the location named in the path.
     * A path of "utensils" returns every utensil instead.
     *
     * <p>Responds 400 for an unknown location and 500 if the database query
     * fails.
     *
     * @param ctx the request context
     */
    @OpenApi(
        path = "/manager/ingredients",
        methods = HttpMethod.POST,
        operationId = "getIngredientsByLocation",
        summary = "List the ingredients stored at a location",
        description = "Returns the ingredients stored in the requested location. "
            + "Requesting the UTENSILS location returns every utensil instead. "
            + "Responds with 400 for an unknown location and 500 if the database "
            + "query fails. The handler is also registered for HTTP QUERY; POST is "
            + "documented here because OpenAPI 3.1 cannot describe QUERY.",
        tags = {"Inventory"},
        requestBody = @OpenApiRequestBody(
            description = "The storage location to list. Use UTENSILS to list "
                + "utensils instead of ingredients.",
            required = true,
            content = @OpenApiContent(from = LocationRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "Ingredients at the location, including the sum of "
                    + "their quantities across all inventory records, or every "
                    + "utensil when the location is UTENSILS.",
                content = @OpenApiContent(from = Ingredient[].class)
            ),
            @OpenApiResponse(
                status = "400",
                description = "The location is missing or unknown.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void ingredientsByLocation(Context ctx) {
        try {
            LocationRequest request = ctx.bodyAsClass(LocationRequest.class);

            if (request.location() == InventoryLocation.UTENSILS) {
                List<Utensil> utensils = inventoryManager.getUtensils();
                ctx.status(200).json(utensils);
                return;
            }

            List<Ingredient> ingredients = inventoryManager.getIngredients(request.location());
            ctx.status(200).json(ingredients);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Error retrieving ingredients: " + e.getMessage());
        }
    }
    
    /**
     * Adds stock to the item named in the path. The amount comes from the
     * {@code quantity} query parameter, and zero is used when it is missing.
     * A {@code isUtensil} query parameter of true restocks a utensil;
     * otherwise an ingredient is restocked.
     *
     * <p>Responds 201 when the ingredient's today-dated stock row is created
     * and 200 when existing stock is increased or a utensil is updated.
     * Responds 400 for a non-numeric quantity and 500 if the database update
     * fails.
     *
     * @param ctx the request context
     */
    @OpenApi(
        path = "/manager/restock/",
        methods = HttpMethod.PUT,
        operationId = "restockItem",
        summary = "Restock an ingredient or utensil",
        description = "Adds the requested quantity to the named ingredient or "
            + "utensil. Set isUtensil to true to restock a utensil. Responds with "
            + "201 when the ingredient's today-dated stock row is created, 200 when "
            + "existing stock is increased or a utensil is updated, 400 for an "
            + "invalid quantity, and 500 if the database update fails.",
        tags = {"Inventory"},
        requestBody = @OpenApiRequestBody(
            description = "The item to restock and the quantity to add.",
            required = true,
            content = @OpenApiContent(from = RestockRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "Existing stock was increased, or a utensil was restocked."
            ),
            @OpenApiResponse(
                status = "201",
                description = "A new today-dated stock row was created for the ingredient."
            ),
            @OpenApiResponse(
                status = "400",
                description = "The request body is invalid or the quantity is not a number.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database update failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void restock(Context ctx) {
        try {
            RestockRequest request = ctx.bodyAsClass(RestockRequest.class);
            if (request.isUtensil()) {
                inventoryManager.restockUtensils(request.name(), request.quantity());
                ctx.status(200);
                return;
            }
            ctx.status(inventoryManager.restockIngredients(request.name(), request.quantity()) ? 201 : 200);
        } catch (SQLException e) {
            ctx.status(500).result("Error restocking ingredients: " + e.getMessage());
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid quantity format: " + e.getMessage());
        }
    }

    /**
     * Sets the restock threshold of the item named in the path to the
     * {@code threshold} query parameter. A {@code isUtensil} query parameter
     * of true updates a utensil; otherwise an ingredient is updated.
     *
     * <p>Responds 200 on success, 400 for a non-numeric threshold and 500 if
     * the database update fails.
     *
     * @param ctx the request context
     */
    @OpenApi(
        path = "/manager/threshold",
        methods = HttpMethod.PATCH,
        operationId = "setRestockThreshold",
        summary = "Set an item's restock threshold",
        description = "Sets the restock threshold of the named item to the "
            + "quantity in the request body. Set isUtensil to true to update a "
            + "utensil; otherwise an ingredient is updated. Responds with 200 on "
            + "success, 400 for an invalid threshold, and 500 if the database "
            + "update fails.",
        tags = {"Inventory"},
        requestBody = @OpenApiRequestBody(
            description = "The item whose threshold is changing, the new "
                + "threshold in the quantity field, and whether the item is a "
                + "utensil.",
            required = true,
            content = @OpenApiContent(from = RestockRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The restock threshold was updated."
            ),
            @OpenApiResponse(
                status = "400",
                description = "The request body is invalid or the threshold is not a number.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database update failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void setRestockThreshold(Context ctx) {
        try {
            RestockRequest request = ctx.bodyAsClass(RestockRequest.class);
            if (request.isUtensil()) {
                inventoryManager.setUtensilRestockThreshold(request.name(), request.quantity());
            } else {
                inventoryManager.setIngredientRestockThreshold(request.name(), request.quantity());
            }
            ctx.status(200);
        } catch (SQLException e) {
            ctx.status(500).result("Error restocking ingredients: " + e.getMessage());
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid threshold format: " + e.getMessage());
        }
    }

    /**
     * Ends the current day by deleting stock that has outlived its shelf life.
     *
     * <p>Responds 204 when nothing was deleted and 200 with the number of
     * deleted records otherwise, or 500 if the delete fails.
     *
     * @param ctx the request context
     */
    @OpenApi(
        path = "/manager/endday",
        methods = HttpMethod.DELETE,
        operationId = "endDay",
        summary = "End the day and remove expired stock",
        description = "Ends the current day by deleting stock that has outlived "
            + "its shelf life. Responds with 204 when nothing was deleted, 200 "
            + "with the number of deleted records as plain text otherwise, and "
            + "500 if the delete fails.",
        tags = {"Inventory"},
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The number of stock records that were deleted.",
                content = @OpenApiContent(
                    from = String.class,
                    mimeType = "text/plain",
                    example = "12"
                )
            ),
            @OpenApiResponse(
                status = "204",
                description = "No expired stock was found, so nothing was deleted."
            ),
            @OpenApiResponse(
                status = "500",
                description = "The delete failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void endDay(Context ctx) {
        try {
            int deletionCount = inventoryManager.endDay();
            if (deletionCount == 0) {
                ctx.status(204);
            } else {
                ctx.status(200).result(Integer.toString(deletionCount));
            }
        } catch (SQLException e) {
            ctx.status(500).result("Error deleting expired ingredients: " + e.getMessage());
        }
    }

    /**
     * Responds with the restock report for the location named in the path, or
     * for every utensil when the path is "utensils". The report lists the items
     * at or below their restock threshold.
     *
     * <p>Responds 400 for an unknown location and 500 if the database query
     * fails.
     *
     * @param ctx the request context
     */
    @OpenApi(
        path = "/manager/restockreport",
        methods = HttpMethod.POST,
        operationId = "getRestockReport",
        summary = "Get the restock report for a location",
        description = "Returns the items at or below their restock threshold for "
            + "the requested location, or every utensil when the location is "
            + "UTENSILS. Responds with 400 for an unknown location and 500 if the "
            + "database query fails. The handler is also registered for HTTP "
            + "QUERY; POST is documented here because OpenAPI 3.1 cannot describe "
            + "QUERY.",
        tags = {"Inventory"},
        requestBody = @OpenApiRequestBody(
            description = "The storage location to report on. Use UTENSILS for "
                + "the utensil report.",
            required = true,
            content = @OpenApiContent(from = LocationRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "Ingredients at or below their restock threshold, "
                    + "or every utensil when the location is UTENSILS.",
                content = @OpenApiContent(from = IngredientRestockData[].class)
            ),
            @OpenApiResponse(
                status = "400",
                description = "The location is missing or unknown.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void restockReport(Context ctx) {
        try {
            LocationRequest request = ctx.bodyAsClass(LocationRequest.class);
            if (request.location() == InventoryLocation.UTENSILS) {
                List<Utensil> report = inventoryManager.utensilRestockReport();
                ctx.status(200).json(report);
                return;
            }
            List<IngredientRestockData> report = inventoryManager.ingredientRestockReport(request.location());
            ctx.status(200).json(report);
        } catch (SQLException e) {
            ctx.status(500).result("Error finding restock report: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        }
    }
}
