package pack.factory;

import com.botscrew.messengercdk.model.incomming.Coordinates;

public class CoordinatesFactory {

    public static Coordinates create(double lat, double lng) {
        Coordinates coords = new Coordinates();
        coords.setLatitude(lat);
        coords.setLongitude(lng);
        return coords;
    }

    public static Coordinates fromStrings(String lat, String lng) {
        Coordinates coords = new Coordinates();
        coords.setLatitude(Double.valueOf(lat));
        coords.setLongitude(Double.valueOf(lng));
        return coords;
    }
}
