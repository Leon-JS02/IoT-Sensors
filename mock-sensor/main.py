from argparse import ArgumentParser, Namespace
from time import sleep

from sensor import Sensor, SENSOR_CLASSES
from reader import Reader


def collect_args() -> Namespace:
    parser = ArgumentParser(
        description="A quick mockup of the IoT sensor software.")
    parser.add_argument("-s", "--server", type=str,
                        help="The address of the IoT server host.", default="localhost")
    parser.add_argument("-p", "--port", type=int,
                        help="The port to access on the IoT server.", default=8080)
    parser.add_argument("-ns", "--number_sensors", type=int,
                        help="The number of sensors to simulate.", default=3)
    parser.add_argument("-pe", "--periodicity", type=int,
                        help="The number of seconds between readings", default=5)
    parser.add_argument("-vt", "--volatility", type=float,
                        help="The percentage of the maximum change between sensor readings", default=20.0)

    return parser.parse_args()


def describe_sensor_arr(sensors: list[Sensor]) -> None:
    """Calls __str__ for each sensor."""
    for sensor in sensors:
        print(sensor)


def collect_readings(sensors: list[Sensor]):
    """Reads each sensor."""
    for sensor in sensors:
        reading = sensor.get_reading()
        print(reading)


if __name__ == "__main__":
    args = collect_args()
    sensor_ids = Sensor.generate_sensor_ids(args.number_sensors)
    sensor_types = Sensor.generate_sensor_types(args.number_sensors)
    sensor_arr = [
        SENSOR_CLASSES[sensor_types[idx]](sensor_ids[idx], volatility=args.volatility)
        for idx in range(args.number_sensors)
    ]
    describe_sensor_arr(sensor_arr)
    reader = Reader(sensor_arr, args.server, args.port)

    running = True
    while running:
        sleep(args.periodicity)
        reader.submit_readings()
