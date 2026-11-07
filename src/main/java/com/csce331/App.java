package com.csce331;

import io.javalin.Javalin;

import com.csce331.controllers.InventoryController;

/**
 * Application entry point.
 */
public class App {
    /**
     * Starts the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.routes.get("/", ctx -> ctx.result("Up and running!"));
            config.routes.get("/manager/ingredients", InventoryController::allIngredients);
            config.routes.get("/manager/ingredients/<location>", InventoryController::ingredientsByLocation);
            config.routes.put("/manager/restock/<name>", InventoryController::restock);
            config.routes.patch("/manager/threshold/<name>", InventoryController::setRestockThreshold);
            config.routes.delete("/manager/endday", InventoryController::endDay);
            config.routes.get("/restockreport/<location>", InventoryController::restockReport);
        }).start(8080);
    }
}
