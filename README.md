# IoT Sensor Project

## Overview
This project sets up a complete IoT pipeline to produce, collect, store, and visualise sensor data from connected IoT devices.

## Project Structure
The repository is organised as follows:

- **`IoT-Sensors/sensor-server`**: Java-based HTTP server that collects sensor readings and stores them in a PostgreSQL database.
- **`IoT-Sensors/schema`**: Database schema definitions for PostgreSQL, used to store sensor data.
- **`IoT-Sensors/mock-sensor`**: A CLI tool to generate and send simulated sensor readings, useful for testing.
- **`IoT-Sensors/iot-sensor`**: Firmware for Arduino/ESP32-based sensors, responsible for collecting and transmitting readings.
- **`dashboard`**: A Streamlit-based web dashboard to visualise sensor data.

---

## Running the Sensor Server

### Prerequisites
Ensure the following are installed:
- Java 17+
- Maven
- PostgreSQL

### Setup & Execution
1. Set up the database by following the instructions in [`schema/README.md`](schema/README.md).
2. Create a configuration file at `sensor-server/src/main/resources/database.properties` with the following details:
    ```properties
    db.url=jdbc:postgresql://<YOUR_DB_HOST>/iot_sensors
    db.user=<YOUR_DB_USERNAME>
    db.password=<YOUR_DB_PASSWORD>
    ```
3. From the repository's root directory, execute:
    ```sh
    cd sensor-server
    mvn clean package
    java -jar SensorServer.jar
    ```

---

## Running the IoT Sensor

1. Follow the setup guide in [`iot-sensor/README.md`](iot-sensor/README.md) to configure and deploy the IoT sensor.

---

## Running the Dashboard

### Prerequisites
- Python 3.9+

### Setup & Execution
1. Create a `.env` file in the `dashboard` directory with the following credentials:
    ```ini
    DB_NAME=iot_sensors
    DB_PORT=5432
    DB_HOST=<YOUR_DB_HOST>
    DB_USER=<YOUR_DB_USERNAME>
    DB_PASSWORD=<YOUR_DB_PASSWORD>
    ```
2. From the repository's root directory, run:
    ```sh
    cd dashboard
    python3 -m venv venv
    source venv/bin/activate  # On Windows, use `venv\Scripts\activate`
    pip install -r requirements.txt
    streamlit run dashboard.py
    ```
3. Open [`http://localhost:8501`](http://localhost:8501) in your browser to view the dashboard.

---

## Notes
- The mock sensor can be used in place of a physical IoT sensor for testing purposes.
- Ensure the database is running before starting the server or dashboard.
- Ensure the server is running before starting the sensors, or mock sensors.
- Modify configuration files as needed to match your environment.
