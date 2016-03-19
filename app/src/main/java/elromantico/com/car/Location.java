package elromantico.com.car;

public class Location {

    public double latitude;
    public double longtitude;
    public long timestamp;

    public Location(double latitude, double longtitude, long timestamp) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.timestamp = timestamp;
    }

    public String toString() {
        return latitude + ", " + longtitude;
    }
}
