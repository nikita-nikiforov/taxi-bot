package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.entity.User;
import pack.model.Place;
import pack.model.custom.PlaceItem;
import pack.service.api.GeocodingService;
import pack.service.api.UberApiService;

import java.util.*;

@Service
public class FavoritePlaceService {

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private GeocodingService geocodingService;

    // Get Optional of favorite places list
    public Optional<List<PlaceItem>> getList(User user) {
        Optional<List<PlaceItem>> result = Optional.empty();        // To be returned
        Map<String, Place> map = new HashMap<>();                   // Map for found places
        // If user has places, put them to map
        uberApiService.getFavoritePlace(user, "work").ifPresent(p -> map.put("work", p));
        uberApiService.getFavoritePlace(user, "home").ifPresent(p -> map.put("home", p));
        // If there's at least one place
        if (!map.isEmpty()) {
            List<PlaceItem> resultList = new ArrayList<>();                 // List for PlaceItems
            map.forEach((name, place) -> {                                  // For each Place, create PlaceItem
                // Get coordinates from GeocodingService
                Coordinates coords = geocodingService.getCoordinatesFromAddress(place.getAddress()).get();
                // Create new PlaceItem of Place, String name and Coordinates and add to resultList
                resultList.add(new PlaceItem(place, name, coords));
            });
            result = Optional.of(resultList);                   // Set to Optional
        }
        return result;
    }
}
