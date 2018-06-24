package pack.model.custom;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.apache.commons.lang3.StringUtils;
import pack.model.Place;

public class PlaceItem {
    private String address;
    private String name;
    private Coordinates coordinates;

    public PlaceItem() {
    }

    public PlaceItem(Place place, String name, Coordinates coords) {
        this.address = place.getAddress();
        this.name = StringUtils.capitalize(name);   // Capitalize, e.g. "work" -> "Work"
        this.coordinates = coords;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
