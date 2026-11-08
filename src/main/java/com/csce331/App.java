package com.csce331;

import io.javalin.Javalin;

import com.csce331.controllers.InventoryController;
import com.csce331.controllers.MenuItemController;

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
            config.routes.get("/", ctx -> ctx.result("Up and running!"));

            config.routes.get("/manager/ingredients", InventoryController::allIngredients);
            config.routes.query("/manager/ingredients", InventoryController::ingredientsByLocation);
            config.routes.put("/manager/restock/", InventoryController::restock);
            config.routes.patch("/manager/threshold", InventoryController::setRestockThreshold);
            config.routes.delete("/manager/endday", InventoryController::endDay);
            config.routes.query("/manager/restockreport", InventoryController::restockReport);
            config.routes.get("/manager/menuitems", MenuItemController::allMenuItems);
            config.routes.query("/manager/menuitems", MenuItemController::menuItemByCategory);
            config.routes.patch("/manager/menuitems", MenuItemController::updatePrice);
            config.routes.patch("/manager/menuitems", MenuItemController::discontinueMenuItem);
            config.routes.get("/manager/salesreport", MenuItemController::salesReport);
        }).start(8080);
    }
}
