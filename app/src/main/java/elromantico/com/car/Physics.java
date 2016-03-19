package elromantico.com.car;

public class Physics {

    private final static double PI = 3.14159265359;

    public static double degToRad(double deg) {
        return deg * PI / 180;
    }

    public static double calculateSpeed(Location start, Location end) {
        double dLat = degToRad(end.latitude - start.latitude);
        double dLon = degToRad(end.longitude - start.longitude);
        double lat1 = degToRad(start.latitude);
        double lat2 = degToRad(end.latitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371 * c / 1000;
        double duration = (end.timestamp - start.timestamp) / 3600000;
        return distance / duration;
    }
}
