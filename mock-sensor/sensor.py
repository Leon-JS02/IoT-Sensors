from datetime import datetime
from random import sample, choice, uniform


class Sensor:
    """Base sensor class."""
    @staticmethod
    def generate_sensor_ids(n: int) -> list[str]:
        """Randomly generates a list of n sensor_id strings."""
        sensor_suffixes = sample(range(1, 101), n)
        return [f"S{suf}" for suf in sensor_suffixes]

    @staticmethod
    def generate_sensor_types(n: int) -> list[str]:
        """Randomly generates a list of n sensor_type strings."""
        return [choice(list(SENSOR_CLASSES.keys())) for _ in range(n)]

    def __init__(self, sensor_id: str, sensor_type: str,
                 unit: str, min_val: float, max_val: float):
        self.sensor_id = sensor_id
        self.sensor_type = sensor_type
        self.unit = unit
        self.min_val = min_val
        self.max_val = max_val
        self.last_read = datetime.now().isoformat()

    def __str__(self) -> str:
        return f"""
        SENSOR
        ID: {self.sensor_id}
        TYPE: {self.sensor_type}
        UNITS: {self.unit}
        LAST_READ: {self.last_read}
        """

    def get_reading(self) -> dict:
        """Generates a sensor reading within its defined range."""
        self.last_read = datetime.now().isoformat()
        return {
            "sensor_id": self.sensor_id,
            "sensor_type": self.sensor_type,
            "value": round(self.generate_reading_value(), 2),
            "units": self.unit,
            "at": self.last_read
        }

    def generate_reading_value(self) -> float:
        """Placeholder for generating sensor-specific readings."""
        return uniform(self.min_val, self.max_val)


class TemperatureSensor(Sensor):
    def __init__(self, sensor_id: str):
        super().__init__(sensor_id, "temperature", "celsius", -10, 50)


class HumiditySensor(Sensor):
    def __init__(self, sensor_id: str):
        super().__init__(sensor_id, "humidity", "percent", 0, 100)


class LightSensor(Sensor):
    def __init__(self, sensor_id: str):
        super().__init__(sensor_id, "light", "lux", 0, 10000)


SENSOR_CLASSES = {
    "temperature": TemperatureSensor,
    "humidity": HumiditySensor,
    "light": LightSensor
}
