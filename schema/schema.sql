DROP DATABASE IF EXISTS iot_sensors;
CREATE DATABASE iot_sensors;
\c iot_sensors;

CREATE TABLE sensor_type(
    sensor_type_id SMALLINT GENERATED ALWAYS AS IDENTITY,
    sensor_type_name VARCHAR(20) NOT NULL UNIQUE,
    PRIMARY KEY(sensor_type_id)
);

CREATE TABLE sensor(
    sensor_id VARCHAR(4) NOT NULL UNIQUE,
    sensor_type_id SMALLINT NOT NULL,
    PRIMARY KEY(sensor_id),
    FOREIGN KEY(sensor_type_id) REFERENCES sensor_type(sensor_type_id)
);

CREATE TABLE unit(
    unit_id SMALLINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    unit_name VARCHAR(20) NOT NULL UNIQUE,
    min_val FLOAT NOT NULL,
    max_val FLOAT NOT NULL,
    PRIMARY KEY(unit_id),
    CHECK (min_val < max_val)
);

CREATE TABLE reading(
    reading_id SERIAL NOT NULL,
    sensor_id VARCHAR(4) NOT NULL,
    unit_id SMALLINT NOT NULL,
    at TIMESTAMPTZ NOT NULL,
    value FLOAT NOT NULL,
    PRIMARY KEY(reading_id),
    FOREIGN KEY(sensor_id) REFERENCES sensor(sensor_id),
    FOREIGN KEY(unit_id) REFERENCES unit(unit_id)
);

INSERT INTO sensor_type(sensor_type_name)
VALUES ('temperature'), ('humidity'), ('light');

INSERT INTO unit(unit_name, min_val, max_val)
VALUES ('celsius', -10, 50), ('lux', 0, 10000), ('percent', 0, 100)