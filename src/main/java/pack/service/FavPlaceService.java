package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.FavPlaceRepository;
import pack.dao.FavPlaceTempRepository;
import pack.entity.FavoritePlace;
import pack.entity.FavoritePlaceTemp;
import pack.entity.User;

import java.util.List;

@Service
public class FavPlaceService {
    @Autowired
    FavPlaceTempRepository favPlaceTempRepository;

    @Autowired
    FavPlaceRepository favPlaceRepository;

    public void savePlaceLocationTemp(User user, Coordinates coord) {
        FavoritePlaceTemp placeTemp = new FavoritePlaceTemp();
        placeTemp.setLat(coord.getLatitude());
        placeTemp.setLng(coord.getLongitude());
        placeTemp.setUser(user);

        favPlaceTempRepository.save(placeTemp);
    }

    public void savePlaceName(User user, String name) {
        FavoritePlaceTemp tempPlace= favPlaceTempRepository.findByUserChatIdAndNameIsNull(user.getChatId());
        FavoritePlace result = new FavoritePlace(tempPlace, name);
        favPlaceRepository.save(result);
        favPlaceTempRepository.delete(tempPlace);
    }

    public List<FavoritePlace> getAllUserPlaces(User user) {
        List<FavoritePlace> userPlaces = favPlaceRepository.findAllByUserChatId(user.getChatId());
        return userPlaces;
    }

}
