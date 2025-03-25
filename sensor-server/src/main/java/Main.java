import io.javalin.Javalin;

public class Main {

    private static DatabaseHandler dbHandler;

    private static void initialiseDatabase() {
        try {
            dbHandler = new DatabaseHandler();
            System.out.println("Database connection established.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialise the database connection.");
            System.exit(1);
        }
    }
    private static void closeDatabase() {
        try {
            if (dbHandler != null) {
                dbHandler.close();
                System.out.println("Database connection closed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static boolean validateReading(Reading reading) {
        return !(reading.sensorId == null || reading.sensorType == null
                || reading.at == null || reading.units == null);
    }

    public static void main(String[] args) {
        initialiseDatabase();
        Javalin sensorServer = Javalin.create().start("0.0.0.0",8080);
        Runtime.getRuntime().addShutdownHook(new Thread(Main::closeDatabase));

        sensorServer.post("/api/reading", ctx -> {
            Reading reading = ctx.bodyAsClass(Reading.class);
            System.out.println("Received: " + reading);

            if (!validateReading(reading)) {
                ctx.status(400).result("Invalid payload.");
                return;
            }

            try {
                if (dbHandler.insertReading(reading)) {
                    ctx.status(200).result("Reading recorded.");
                } else {
                    ctx.status(400).result("Failed to record reading (value/unit error).");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal server error.");
            }
        });
    }
}
