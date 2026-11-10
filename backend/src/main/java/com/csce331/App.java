package com.csce331;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiResponse;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;

import com.csce331.controllers.InventoryController;
import com.csce331.controllers.MenuItemController;
import com.csce331.controllers.CustomerMenuController;
import com.csce331.controllers.AuthenticationController;

/**
 * Application entry point.
 */
public class App {
    /**
     * Prevents instantiation; the application is started through
     * {@link #main(String[])}.
     */
    private App() {
    }

    /**
     * Responds with a message confirming that the API is running.
     *
     * @param ctx the request context
     */
    @OpenApi(
        path = "/",
        methods = HttpMethod.GET,
        operationId = "getStatus",
        summary = "Check that the API is running",
        description = "Returns a plain-text message confirming that the API is "
            + "up and running.",
        tags = {"Status"},
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The API is running.",
                content = @OpenApiContent(
                    from = String.class,
                    mimeType = "text/plain",
                    example = "Up and running!"
                )
            )
        }
    )
    private static void root(Context ctx) {
        ctx.result("Up and running!");
    }

    /**
     * Starts the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(rule -> rule.anyHost()));

            config.registerPlugin(new OpenApiPlugin(openapi -> {
                openapi.withDefinitionConfiguration((version, builder) -> {
                    builder.info(info -> info
                        .title("CSCE 331 Project 3 API")
                        .version("1.0.0")
                    );
                });
            }));

            config.registerPlugin(new SwaggerPlugin());
            
            config.routes.get("/", App::root);

            config.routes.get("/manager/ingredients", InventoryController::allIngredients);
            config.routes.query("/manager/ingredients", InventoryController::ingredientsByLocation);
            config.routes.put("/manager/restock/", InventoryController::restock);
            config.routes.patch("/manager/threshold", InventoryController::setRestockThreshold);
            config.routes.delete("/manager/endday", InventoryController::endDay);
            config.routes.query("/manager/restockreport", InventoryController::restockReport);
            config.routes.get("/manager/menuitems", MenuItemController::allMenuItems);
            config.routes.query("/manager/menuitems", MenuItemController::menuItemByCategory);
            config.routes.patch("/manager/menuitems/price", MenuItemController::updatePrice);
            config.routes.patch("/manager/menuitems/discontinue", MenuItemController::discontinueMenuItem);
            config.routes.get("/manager/salesreport", MenuItemController::salesReport);

            config.routes.get("/customer/menuitems", CustomerMenuController::popularItems);
            config.routes.query("/customer/menuitems", CustomerMenuController::menuItemByCategory);
            config.routes.post("/customer/order", CustomerMenuController::order);

            config.routes.query("/auth/role/email", AuthenticationController::getUserRoleByEmail);
            config.routes.query("/auth/role/id", AuthenticationController::getUserRoleById);
            config.routes.post("/auth/newuser", AuthenticationController::createUser);

            // OpenAPI 3.1 has no operation for the QUERY method, so the same
            // handlers are also exposed over POST for the Swagger UI.
            config.routes.post("/manager/ingredients", InventoryController::ingredientsByLocation);
            config.routes.post("/manager/restockreport", InventoryController::restockReport);
            config.routes.post("/manager/menuitems", MenuItemController::menuItemByCategory);
            config.routes.post("/customer/menuitems", CustomerMenuController::menuItemByCategory);
            config.routes.post("/auth/role/email", AuthenticationController::getUserRoleByEmail);
            config.routes.post("/auth/role/id", AuthenticationController::getUserRoleById);
        }).start(8080);
    }
}
