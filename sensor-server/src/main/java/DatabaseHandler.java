import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Properties;

public class DatabaseHandler implements AutoCloseable {
    private Connection connection;

    public DatabaseHandler() throws SQLException, IOException {
        this.connection = initialise();
    }

    private Connection initialise() throws SQLException, IOException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        Properties properties = loadPropertyFile("database.properties");
        String host = properties.getProperty("db.url");
        String username = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        connection = DriverManager.getConnection(host, username, password);
        System.out.println("Database connection established.");
        return connection;
    }

    private Properties loadPropertyFile(String fileName) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Property file '" + fileName + "' not found in the classpath");
            }
            properties.load(inputStream);
        }
        return properties;
    }

    public HashMap<String, UnitInfo> getUnitMap() {
        HashMap<String, UnitInfo> unitMap = new HashMap<>();
        String query = "SELECT unit_id, unit_name, min_val, max_val FROM unit";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                unitMap.put(rs.getString("unit_name"),
                        new UnitInfo(rs.getInt("unit_id"), rs.getDouble("min_val"), rs.getDouble("max_val")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unitMap;
    }

    private int getOrInsertSensorType(String sensorType) throws SQLException {
        String checkQuery = "SELECT sensor_type_id FROM sensor_type WHERE sensor_type_name = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, sensorType);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) return rs.getInt("sensor_type_id");
        }

        String insertQuery = "INSERT INTO sensor_type(sensor_type_name) VALUES (?) RETURNING sensor_type_id";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setString(1, sensorType);
            ResultSet rs = insertStmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        throw new SQLException("Failed to insert sensor type: " + sensorType);
    }

    private void getOrInsertSensor(String sensorId, String sensorType) throws SQLException {
        String checkQuery = "SELECT sensor_id FROM sensor WHERE sensor_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, sensorId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) return;
        }

        int sensorTypeId = getOrInsertSensorType(sensorType);
        String insertQuery = "INSERT INTO sensor(sensor_id, sensor_type_id) VALUES (?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setString(1, sensorId);
            insertStmt.setInt(2, sensorTypeId);
            insertStmt.executeUpdate();
        }
    }

    public boolean insertReading(Reading reading) {
        try {
            HashMap<String, UnitInfo> unitMap = getUnitMap();
            UnitInfo unitInfo = unitMap.get(reading.units);

            if (unitInfo == null) {
                System.err.println("Invalid unit: " + reading.units);
                return false;
            }

            if (reading.value < unitInfo.minValue || reading.value > unitInfo.maxValue) {
                System.err.println("Value out of range: " + reading.value);
                return false;
            }

            getOrInsertSensor(reading.sensorId, reading.sensorType);

            String insertReadingQuery = """
                    INSERT INTO reading(sensor_id, unit_id, at, value)
                    VALUES (?, ?, ?, ?)
                    """;
            try (PreparedStatement stmt = connection.prepareStatement(insertReadingQuery)) {
                stmt.setString(1, reading.sensorId);
                stmt.setInt(2, unitInfo.unitId);
                stmt.setTimestamp(3, reading.at);
                stmt.setDouble(4, reading.value);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class UnitInfo {
        int unitId;
        double minValue;
        double maxValue;

        public UnitInfo(int unitId, double minValue, double maxValue) {
            this.unitId = unitId;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
    }
}
