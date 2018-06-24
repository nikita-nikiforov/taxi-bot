package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.Payload;
import pack.entity.User;
import pack.model.Place;
import pack.service.FavoritePlaceService;

import java.util.List;
import java.util.Optional;

@ChatEventsProcessor
public class FavoritePlaceHandler {

    @Autowired
    private FavoritePlaceService favoritePlaceService;

    @Postback(value = Payload.SHOW_FAV_PLACES)
    public void handleShowFavoritePlaces(User user) {
        Optional<List<Place>> places = favoritePlaceService.getList(user);
    }
}
