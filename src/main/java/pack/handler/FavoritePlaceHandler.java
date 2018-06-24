package pack.handler;

import com.botscrew.botframework.annotation.*;
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
import pack.model.Place;
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
    private StartHandler startHandler;

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
        // When users open his places, check for update in user's Uber favorite places
        // and update has_fav_place in DB
        favoritePlaceService.updateUserHasPlaces(user);
        Optional<List<PlaceItem>> places = favoritePlaceService.getPlacesList(user);
        if (places.isPresent()) {

            List<TemplateElement> elements = messageService.getPlaceTemplates(places.get());
            Request maps = GenericTemplate.builder()
                    .user(user)
                    .elements(elements)
                    .build();
            Request quickReplies = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.FAV_PLACE_SHOWED + MessageText.WHATS_NEXT)
                    .addQuickReply(new PostbackQuickReply("Add place", Payload.CHOOSE_PLACE_TO_ADD))
                    .addQuickReply(new PostbackQuickReply("Back", Payload.START))
                    .build();
            sender.send(maps);
            sender.send(quickReplies);
        } else {
            Request request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.DONT_HAVE_PLACES)
                    .addQuickReply(new PostbackQuickReply("Add place", Payload.CHOOSE_PLACE_TO_ADD))
                    .addQuickReply(new PostbackQuickReply("Back", Payload.START))
                    .build();
            sender.send(request);
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
    public void handleInputFavPlaceLocation(User user, @Location Coordinates coords,
                                            @StateParameters Map<String, String> stateParams) {
        Optional<String> address = geocodingService.getAddressFromCoordinates(coords);
        if (address.isPresent()) {
            handleInputFavPlaceText(user, address.get(), stateParams);
        } else {
            Request request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.CANT_FIND_ADDRESS_BY_LOCATION)
                    .location()
                    .addQuickReply(new PostbackQuickReply("Cancel", Payload.CANCEL_FAV_PLACE))
                    .build();
            sender.send(request);
        }

    }

    @Text(states = State.INPUT_FAV_PLACE)
    public void handleInputFavPlaceText(User user, @Text String address,
                                        @StateParameters Map<String, String> stateParams) {
        String id = stateParams.get("id");
        Optional<Place> place = favoritePlaceService.updatePlace(user, id, address);
        if (place.isPresent()) {
            userService.save(user, State.LOGGED);
            sender.send(user, MessageText.FAV_PLACE_ADDED);
            handleShowFavoritePlaces(user);
        } else {
            Request request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.UBER_CANT_FIND)
                    .location()
                    .build();
            sender.send(request);
        }
    }

    @Postback(value = Payload.CANCEL_FAV_PLACE)
    public void habdleLoggedState(User user) {
        userService.save(user, State.LOGGED);
        startHandler.handleLoggedState(user);
    }
}
