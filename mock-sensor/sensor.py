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
                 unit: str, min_val: float, max_val: float, volatility: float = 20.0):
        self.sensor_id = sensor_id
        self.sensor_type = sensor_type
        self.unit = unit
        self.min_val = min_val
        self.max_val = max_val
        self.previous_reading = (self.min_val + self.max_val) / 2
        self.last_read = datetime.now().isoformat()
        self.volatility = volatility

    def __str__(self) -> str:
        return f"""
        SENSOR
        ID: {self.sensor_id}
        TYPE: {self.sensor_type}
        UNITS: {self.unit}
        LAST_READING: {self.previous_reading}
        LAST_READ_AT: {self.last_read}
        VOLATILITY: {self.volatility}%
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
        variation = self.previous_reading * (self.volatility / 100)
        new_value = uniform(self.previous_reading - variation, self.previous_reading + variation)
        new_value = max(self.min_val, min(self.max_val, new_value))
        self.previous_reading = new_value
        return new_value


class TemperatureSensor(Sensor):
    def __init__(self, sensor_id: str, volatility: float):
        super().__init__(sensor_id, "temperature", "celsius", -10, 50, volatility=volatility)


class HumiditySensor(Sensor):
    def __init__(self, sensor_id: str, volatility: float):
        super().__init__(sensor_id, "humidity", "percent", 0, 100, volatility=volatility)


class LightSensor(Sensor):
    def __init__(self, sensor_id: str, volatility: float):
        super().__init__(sensor_id, "light", "lux", 0, 10000, volatility=volatility)


SENSOR_CLASSES = {
    "temperature": TemperatureSensor,
    "humidity": HumiditySensor,
    "light": LightSensor
}
