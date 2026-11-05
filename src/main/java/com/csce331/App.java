package com.csce331;

import io.javalin.Javalin;


/**
 * Application entry point.
 */
public class App {
    private App() {
    }

    /**
     * Starts the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        }).start(8080);
    }
}
