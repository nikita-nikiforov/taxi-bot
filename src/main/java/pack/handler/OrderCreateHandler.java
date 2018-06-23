package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Location;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.builder.TextMessage;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.element.WebAction;
import com.botscrew.messengercdk.model.outgoing.element.quickreply.PostbackQuickReply;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.model.FareResponse;
import pack.model.ProductItem;
import pack.service.MessageService;
import pack.service.OrderService;
import pack.service.UberRideService;
import pack.service.api.GeocodingService;
import pack.service.api.MapboxService;
import pack.service.api.UberApiService;
import pack.service.dao.OrderDaoService;
import pack.service.dao.UserService;

import java.util.List;
import java.util.Optional;

@ChatEventsProcessor
public class OrderCreateHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private Sender sender;

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private UberRideService uberRideService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDaoService orderDaoService;

    @Autowired
    private MapboxService mapboxService;

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private MessageService messageService;

    @Autowired
    private GeocodingService geocodingService;

    @Postback(value = Payload.MAKE_ORDER, states = State.LOGGED)
    public void handleMakeOrder(User user) {
        userService.save(user.getChatId(), State.START_INPUT);      // Save user
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.START_INPUT)
//                .postback("My places", "SELECT_FROM_PLACES")
                .location()
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
                .addElement(TemplateElement.builder()
                        .title("Pinned Location")
                        .imageUrl(mapboxService.getConfirmAddressMapUrl(coords))
                        .defaultAction(WebAction.builder()
                                .url(mapboxService.getMarkeredMapUrl(coords))
                                .makeCompactWebView()
                                .build())
                        .build())
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
            request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.START_INPUT_TRUE)
                    .location()
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
        // Sends request to start UberTrip to Uber. If has started, then true
        if (uberRideService.confirmRide(user)) {
            userService.save(user, State.FARE_CONFIRMED);
            Request request = TextMessage.builder()
                    .user(user)
                    .text(MessageText.FARE_CONFIRMED)
                    .build();
            sender.send(request);
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

    @Location(states = State.FARE_CONFIRMATION)
    @Text(states = State.FARE_CONFIRMATION)
    public void handleTextWhileConfirmation(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.RETRY)
                .postback("Confirm", Payload.CONFIRM_ORDER)
                .postback("Discard", Payload.DISCARD_ORDER)
                .build();
        sender.send(request);
    }
}