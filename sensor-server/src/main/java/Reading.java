import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;

public class Reading {
    @JsonProperty("sensor_id")
    public String sensorId;
    @JsonProperty("sensor_type")
    public String sensorType;
    public String units;
    public double value;
    @JsonProperty("at")
    public Timestamp at;

    @Override
    public String toString() {
        return "SensorReading{" +
                "sensorId='" + sensorId + '\'' +
                ", sensorType='" + sensorType + '\'' +
                ", value=" + value +
                ", units='" + units + '\'' +
                ", at='" + at + '\'' +
                '}';
    }
}
