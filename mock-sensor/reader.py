from sensor import Sensor
import requests as req


class Reader:
    @staticmethod
    def _form_base_url(host: str, port: int) -> str:
        return f"http://{host}:{port}/api/"

    def __init__(self, sensor_arr: list[Sensor],
                 host: str, port: int):
        self.sensors = sensor_arr
        self.base_url = Reader._form_base_url(host, port)

    def submit_readings(self):
        reading_url = f"{self.base_url}reading"
        for sensor in self.sensors:
            data = sensor.get_reading()
            req.post(reading_url, json=data, timeout=10)
