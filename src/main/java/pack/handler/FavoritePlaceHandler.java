package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Location;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.PostbackParameters;
import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.element.quickreply.PostbackQuickReply;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.model.custom.PlaceItem;
import pack.service.FavoritePlaceService;
import pack.service.MessageService;
import pack.service.api.GeocodingService;
import pack.service.dao.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ChatEventsProcessor
public class FavoritePlaceHandler {

    @Autowired
    private FavoritePlaceService favoritePlaceService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private Sender sender;

    @Postback(value = Payload.SHOW_FAV_PLACES)
    public void handleShowFavoritePlaces(User user) {
        Optional<List<PlaceItem>> places = favoritePlaceService.getList(user);
        if (places.isPresent()) {
            List<TemplateElement> elements = messageService.getPlaceTemplates(places.get());
            Request request = GenericTemplate.builder()
                    .user(user)
                    .elements(elements)
                    .addQuickReply(new PostbackQuickReply("Add place", Payload.CHOOSE_PLACE_TO_ADD))
                    .addQuickReply(new PostbackQuickReply("Back", Payload.START))
                    .build();
            sender.send(request);
        } else {
            sender.send(user, "You don't have favorite places");
        }
    }

    @Postback(value = Payload.CHOOSE_PLACE_TO_ADD)
    public void handleChoosePlaceToAdd(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.CHOOSE_PLACE_TO_ADD)
                .postback("Home", Payload.ADD_PLACE + "?id=home")
                .postback("Work", Payload.ADD_PLACE + "?id=work")
                .build();
        sender.send(request);
    }

    @Postback(value = Payload.ADD_PLACE)
    public void handleAddPlace(User user, @PostbackParameters Map<String, String> params) {
        String id = params.get("id");
        userService.save(user, State.INPUT_FAV_PLACE + "?id=" + id);
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.INPUT_FAV_PLACE)
                .location()
                .build();
        sender.send(request);
    }

    @Location(states = State.INPUT_FAV_PLACE)
    public void handleInputFavPlaceLocation(User user, @Location Coordinates coords) {
        Optional<String> address = geocodingService.getAddressFromCoordinates(coords);
        sender.send(user, address.get());
    }

}
