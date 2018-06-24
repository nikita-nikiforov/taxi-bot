package pack.handler;

import com.botscrew.botframework.annotation.*;
import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.element.quickreply.LocationQuickReply;
import com.botscrew.messengercdk.model.outgoing.element.quickreply.PostbackQuickReply;
import com.botscrew.messengercdk.model.outgoing.element.quickreply.QuickReply;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.model.FareResponse;
import pack.model.ProductItem;
import pack.model.custom.PlaceItem;
import pack.service.FavoritePlaceService;
import pack.service.MessageService;
import pack.service.OrderService;
import pack.service.UberRideService;
import pack.service.api.GeocodingService;
import pack.service.dao.OrderDaoService;
import pack.service.dao.UberCredentialService;
import pack.service.dao.UserService;

import java.util.*;

@ChatEventsProcessor
public class OrderCreateHandler {
    @Autowired
    private UserService userService;

    @Autowired
    private Sender sender;

    @Autowired
    private UberRideService uberRideService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDaoService orderDaoService;

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private MessageService messageService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private UberCredentialService uberCredentialService;

    @Autowired
    private FavoritePlaceService favoritePlaceService;

    @Postback(value = Payload.MAKE_ORDER, states = State.LOGGED)
    public void handleMakeOrder(User user) {
        userService.save(user.getChatId(), State.START_INPUT);      // Save user
        // Create quickReplies (with choosing from "My places", if user has them)
        List<QuickReply> quickReplies = new ArrayList<>(Arrays.asList(new LocationQuickReply()));
        boolean userHasFavPlaces = uberCredentialService.getCredentialByChatId(user.getChatId()).isHas_fav_places();
        if (userHasFavPlaces) {
            quickReplies.add(new PostbackQuickReply("My places",
                    Payload.INPUT_MY_PLACES + "?input=start"));
        }
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.START_INPUT)
                .quickReplies(quickReplies)
                .build();
        sender.send(request);
    }

    @Text(states = {State.START_INPUT, State.START_TEXT_ASKED})
    public void handleStartInputText(User user, @Text String text) {
        // Receive coordinates by text address using Google Geocoding
        Optional<Coordinates> startPoint = geocodingService.getCoordinatesFromAddress(text);
        if (startPoint.isPresent()) {
            Coordinates coords = startPoint.get();
            orderService.setStartPoint(user, coords);            // Save order start point
            userService.save(user, State.START_TEXT_ASKED);     // Save user
            // Show map and ask confirmation
            askLocationConfirm(user, Payload.CONFIRM_START_POINT,
                    Payload.DISCARD_START_POINT, coords);
        } else {                                                // Remain as START_INPUT
            Request request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.PLACE_NOT_FOUND)
                    .location()
                    .build();
            sender.send(request);
        }
    }

    // Send the map of geocoded address and ask user to confirm. The method is universal
    // for confirmation of start and end points
    private void askLocationConfirm(User user, String payloadConfirm, String payloadDiscard,
                                    Coordinates coords) {
        Request mapView = GenericTemplate.builder()
                .user(user)
                .addElement(messageService.getConfirmMapTemplate(coords))
                .build();
        Request quickReplies = QuickReplies.builder()
                .user(user)
                .text(MessageText.DID_YOU_MEAN_THIS)
                .addQuickReply(new PostbackQuickReply("Yes", payloadConfirm))
                .addQuickReply(new PostbackQuickReply("No", payloadDiscard))
                .build();
        sender.send(mapView);
        sender.send(quickReplies);
    }

    @Postback(value = Payload.CONFIRM_START_POINT)
    public void handleConfirmStartPoint(User user) {
        Coordinates coord = orderService.getStartPointCoordinates(user);
        handleStartInputLocation(user, coord);              // delegate to @Location method
    }

    @Postback(value = Payload.DISCARD_START_POINT)
    public void handleDiscardStartPoint(User user) {
        userService.save(user, State.START_INPUT);          // Return user to START_INPUT
        handleMakeOrder(user);                              // And ask of start again
    }

    @Location(states = {State.START_INPUT, State.START_TEXT_ASKED})
    public void handleStartInputLocation(User user, @Location Coordinates coord) {
        Request request;
        // Check if any product is available in this location
        List<ProductItem> productsNearBy = uberRideService.getProductsNearBy(user, coord);
        if (!productsNearBy.isEmpty()) {
            orderService.setStartPoint(user, coord);            // save start point
            userService.save(user, State.END_INPUT);            // user -> END_INPUT

            // Create quickReplies (with choosing from "My places", if user has them)
            List<QuickReply> quickReplies = new ArrayList<>(Arrays.asList(new LocationQuickReply()));
            boolean userHasFavPlaces = uberCredentialService.getCredentialByChatId(user.getChatId()).isHas_fav_places();
            if (userHasFavPlaces) quickReplies.add(new PostbackQuickReply("My places",
                    Payload.INPUT_MY_PLACES + "?input=end"));

            request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.START_INPUT_TRUE)
                    .quickReplies(quickReplies)
                    .build();
        } else {                                    // If Uber has no products in the region
//            userService.save(user, State.LOGGED);               // user -> LOGGED
            request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.UBER_NO_PRODUCTS)
                    .location()
                    .postback("Cancel", Payload.DISCARD_ORDER)
                    .build();
        }
        sender.send(request);
    }

    @Text(states = {State.END_INPUT, State.END_TEXT_ASKED})
    public void handleEndInput(User user, @Text String text) {
        // Get Coordinates
        Optional<Coordinates> endPoint = geocodingService.getCoordinatesFromAddress(text);
        if (endPoint.isPresent()) {
            Coordinates coords = endPoint.get();
            orderService.setEndPoint(user, coords);                  // Set them to the order in DB
            userService.save(user, State.END_TEXT_ASKED);            // Update user's state
            // Show map and ask confirmation
            askLocationConfirm(user, Payload.CONFIRM_END_POINT,
                    Payload.DISCARD_END_POINT, coords);
        } else {
            Request request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.PLACE_NOT_FOUND)
                    .location()
                    .postback("Cancel", Payload.DISCARD_ORDER)
                    .build();
            sender.send(request);
        }
    }

    @Postback(value = Payload.CONFIRM_END_POINT)
    public void handleConfirmEndPoint(User user) {
        Coordinates coord = orderService.getEndPointCoordinates(user);
        handleEndLocation(user, coord);
    }

    @Postback(value = Payload.DISCARD_END_POINT)
    public void handleDiscardEndPoint(User user) {
        userService.save(user, State.END_INPUT);
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.END_INPUT_RETRY)
                .location()
                .postback("Cancel", Payload.DISCARD_ORDER)
                .build();
        sender.send(request);
    }

    @Location(states = {State.END_INPUT, State.END_TEXT_ASKED})
    public void handleEndLocation(User user, @Location Coordinates coord) {
        orderService.setEndPoint(user, coord);
        sendEstimateFare(user);
    }

    @Text(states = State.FARE_CONFIRMATION)
    @Location(states = State.FARE_CONFIRMATION)
    public void sendEstimateFare(User user) {
        Optional<FareResponse> estimateFare = orderService.getEstimateFare(user);
        Request request;
        if (estimateFare.isPresent()) {
            userService.save(user, State.FARE_CONFIRMATION);
            request = QuickReplies.builder()
                    .user(user)
                    .text(messageService.getTripEstimate(estimateFare.get()))
                    .postback("Confirm", Payload.CONFIRM_ORDER)
                    .postback("Discard", Payload.DISCARD_ORDER)
                    .build();
        } else {
            request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.END_ERROR)
                    .location()
                    .postback("Cancel", Payload.DISCARD_ORDER)
                    .build();
        }
        sender.send(request);
    }

    @Postback(value = Payload.DISCARD_ORDER)
    public void handleDiscardRide(User user) {
        orderDaoService.removeByUser(user);
        userService.save(user, State.LOGGED);
        startHandler.handleLoggedState(user);
    }

    @Postback(value = Payload.CONFIRM_ORDER)
    public void handleConfirmRide(User user) {
        // Sends request to Uber to start UberTrip. If has started, then true
        if (uberRideService.confirmRide(user)) {
            userService.save(user, State.FARE_CONFIRMED);
        } else {
            // If ride failed to be started in Uber, then send a new estimate fare to user
            Optional<FareResponse> estimateFare = orderService.getEstimateFare(user);
            Request request = QuickReplies.builder()
                    .user(user)
                    .text(messageService.getTripEstimate(estimateFare.get()))
                    .postback("Confirm", Payload.CONFIRM_ORDER)
                    .postback("Discard", Payload.DISCARD_ORDER)
                    .build();
            sender.send(user, MessageText.FARE_ERROR);
            sender.send(request);
        }
    }

    @Postback(value = Payload.INPUT_MY_PLACES)
    public void handleInputMyPlaces(User user, @PostbackParameters Map<String, String> params) {
        String inputType = params.get("input");
        List<QuickReply> quickReplies = messageService.getInputMyPlacesQuickReplies(user, inputType);
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.CHOOSE_FROM_PLACES)
                .quickReplies(quickReplies)
                .build();
        sender.send(request);
    }

    @Postback(value = Payload.CHOOSE_FROM_PLACES)
    public void handleChooseFromPlaces(User user, @PostbackParameters Map<String, String> params) {
        String input = params.get("input");
        String place = params.get("place");
        PlaceItem placeItem = favoritePlaceService.getPlaceItemById(user, place);
        Coordinates coords = placeItem.getCoordinates();
        switch (input) {
            case "start":
                handleStartInputLocation(user, coords);
                break;
            case "end":
                handleEndLocation(user, coords);
        }
    }
}