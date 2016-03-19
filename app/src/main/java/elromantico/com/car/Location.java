package elromantico.com.car;

public class Location {

    public double latitude;
    public double longitude;
    public long timestamp;

    public Location(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String toString() {
        return latitude + ", " + longitude;
    }
}
