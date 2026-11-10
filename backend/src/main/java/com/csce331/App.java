package com.csce331;

import io.javalin.Javalin;
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
     * Starts the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(openapi -> {
                openapi.withDefinitionConfiguration((version, builder) -> {
                    builder.info(info -> info
                        .title("CSCE 331 Project 3 API")
                        .version("1.0.0")
                    );
                });
            }));

            config.registerPlugin(new SwaggerPlugin());

            config.routes.get("/", ctx -> ctx.result("Up and running!"));

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
        }).start(8080);
    }
}
