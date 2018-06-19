package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Location;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.model.outgoing.builder.ListTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.builder.TextMessage;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.element.button.PostbackButton;
import com.botscrew.messengercdk.model.outgoing.element.quickreply.QuickReply;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.constant.State;
import pack.entity.FavoritePlace;
import pack.entity.User;
import pack.service.FavPlaceService;
import pack.service.UserService;

import java.util.ArrayList;
import java.util.List;

@ChatEventsProcessor
public class FavoritePlaceHandler {

    @Autowired
    private Sender sender;

    @Autowired
    private UserService userService;

    @Autowired
    private FavPlaceService favPlaceService;

    @Postback(value = "ADD_PLACE")
    public void handleAddNewPlace(User user) {
        userService.save(user, "FAV_PLACE_INPUT_MAP");
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.FAV_PLACE_INPUT_MAP)
                .location()
                .build();
        sender.send(request);
    }

    @Location(states = "FAV_PLACE_INPUT_MAP")
    public void handleFavPlaceInputMap(User user, @Location Coordinates coord) {
        favPlaceService.savePlaceLocationTemp(user, coord);
        userService.save(user, "FAV_PLACE_INPUT_NAME");
        Request request = TextMessage.builder()
                .user(user)
                .text(MessageText.FAV_PLACE_INPUT_NAME)
                .build();
        sender.send(request);
    }

    @Text(states = "FAV_PLACE_INPUT_NAME")
    public void handleFavPlaceInputName(User user, @Text String text) {
        favPlaceService.savePlaceName(user, text);
        userService.save(user, State.LOGGED);
        Request request = TextMessage.builder()
                .user(user)
                .text(MessageText.FAV_PLACE_ADDED)
                .build();
        sender.send(request);
    }

    @Postback(value = "SELECT_FROM_PLACES", states = "START_INPUT")
    public void handleStartInputFromPlaces(User user) {
        List<TemplateElement> places = new ArrayList<>();
        List<FavoritePlace> userPlaces = favPlaceService.getAllUserPlaces(user);
        userPlaces.forEach(p -> {
            places.add(TemplateElement.builder()
                .title(p.getName())
                .button(new PostbackButton(p.getName(), "postback"))
                .build());
        });
        Request request = ListTemplate.builder().elements(places)
                .user(user).build();
        List<QuickReply> quickReplies = new ArrayList<>();
//        userPlaces.forEach(p -> {
//            quickReplies.add(QuickReply)
//        });
//        Request request1 = QuickReplies.builder()
//                .

        sender.send(request);

    }
}
