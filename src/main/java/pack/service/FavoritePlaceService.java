package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.entity.UberCredential;
import pack.entity.User;
import pack.model.Place;
import pack.model.custom.PlaceItem;
import pack.service.api.GeocodingService;
import pack.service.api.UberApiService;
import pack.service.dao.UberCredentialService;

import java.util.*;

@Service
public class FavoritePlaceService {
    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private UberCredentialService uberCredentialService;

    // Get Optional of favorite places list
    public Optional<List<PlaceItem>> getPlacesList(User user) {
        Optional<List<PlaceItem>> result = Optional.empty();        // To be returned
        Map<String, Place> map = new HashMap<>();                   // Map for found places
        // If user has places, put them to map
        uberApiService.getFavoritePlace(user, "work").ifPresent(p -> map.put("work", p));
        uberApiService.getFavoritePlace(user, "home").ifPresent(p -> map.put("home", p));
        // If there's at least one place
        if (!map.isEmpty()) {
            List<PlaceItem> resultList = new ArrayList<>();                 // List for PlaceItems
            map.forEach((name, place) -> {                                  // For each Place, create PlaceItem
                // Create new PlaceItem with set Coordinates and add to resultList
                resultList.add(getPlaceItemFromPlace(place, name));
            });
            result = Optional.of(resultList);                   // Set to Optional
        }
        return result;
    }

    public Optional<Place> updatePlace(User user, String id, String address) {
        return uberApiService.putPlace(user, id, address);
    }

    public void updateUserHasPlaces(User user) {
        getPlacesList(user).ifPresent(l -> {
            UberCredential credential = uberCredentialService.getCredentialByChatId(user.getChatId());
            credential.setHas_fav_places(true);
            uberCredentialService.save(credential);
        });
    }

    public PlaceItem getPlaceItemById(User user, String placeId) {
        placeId = placeId.toLowerCase();
        Place place = uberApiService.getFavoritePlace(user, placeId).get();
        return getPlaceItemFromPlace(place, placeId);
    }

    private PlaceItem getPlaceItemFromPlace(Place place, String name) {
        // Get coordinates from GeocodingService
        Coordinates coords = geocodingService.getCoordinatesFromAddress(place.getAddress()).get();
        return new PlaceItem(place, name, coords);
    }
}
