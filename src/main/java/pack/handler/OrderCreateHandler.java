package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Location;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.element.button.PostbackButton;
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
import pack.service.UserService;
import pack.service.api.GeocodingService;
import pack.service.api.MapboxService;
import pack.service.api.UberApiService;

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
        // Check if Google can find such address
        Optional<Coordinates> startPoint = geocodingService.getCoordinatesFromAddress(text);
        if (startPoint.isPresent()) {
            Coordinates coords = startPoint.get();
            orderService.setStartPoint(user, coords);            // Save order start point
            userService.save(user, State.START_TEXT_ASKED);     // Save user
            Request request = getLocationConfirmRequest(user, MessageText.TEXT_ASKED_TITLE,
                    MessageText.START_TEXT_ASKED_SUBTITLE, Payload.CONFIRM_START_POINT,
                    Payload.DISCARD_START_POINT, coords);
            sender.send(request);
        } else {
            Request request = QuickReplies.builder()            // Remain as START_INPUT
                    .user(user)
                    .text(MessageText.PLACE_NOT_FOUND)
                    .location()
                    .build();
            sender.send(request);
        }
    }

    private Request getLocationConfirmRequest(User user, String title, String subtitle,
                    String payloadConfirm, String payloadDiscard, Coordinates coords) {
        return GenericTemplate.builder()
                .user(user)
                .addElement(TemplateElement.builder()
                        .title(title)
                        .subtitle(subtitle)
                        .imageUrl(mapboxService.getConfirmAddressMapUrl(coords))
                        .button(new PostbackButton("Yes", payloadConfirm))
                        .button(new PostbackButton("No", payloadDiscard))
                        .build())
                .build();
    }

    @Postback(value = Payload.CONFIRM_START_POINT)
    public void handleConfirmStartPoint(User user) {
        Coordinates coord = orderService.getStartPointCoordinates(user);
        handleStartInputLocation(user, coord);              // delegate to @Location method
    }

    @Postback(value = Payload.DISCARD_START_POINT)
    public void handleDiscardStartPoint(User user) {
        userService.save(user, State.START_INPUT);          // Return user to START_INPUT
        handleMakeOrder(user);
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
            userService.save(user, State.LOGGED);               // user -> LOGGED
            request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.UBER_NO_PRODUCTS)
                    .location()
                    .build();
        }
        sender.send(request);
    }

    @Text(states = {State.END_INPUT, State.END_TEXT_ASKED})
    public void handleEndInput(User user, @Text String text) {
        Request request;                // to be returned
        // Get Coordinates
        Optional<Coordinates> endPoint = geocodingService.getCoordinatesFromAddress(text);
        if (endPoint.isPresent()) {
            Coordinates coord = endPoint.get();
            orderService.setEndPoint(user, coord);                  // Set them to the order in DB
            userService.save(user, State.END_TEXT_ASKED);        // Update user's state

            request = GenericTemplate.builder()
                    .user(user)
                    .addElement(TemplateElement.builder()
                            .title(MessageText.TEXT_ASKED_TITLE)
                            .subtitle(MessageText.END_TEXT_ASKED_SUBTITLE)
                            .imageUrl(mapboxService.getConfirmAddressMapUrl(coord))
                            .button(new PostbackButton("Yes", Payload.CONFIRM_END_POINT))
                            .button(new PostbackButton("No", Payload.DISCARD_END_POINT))
                            .build())
                    .build();
        } else {
            request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.END_INPUT_FALSE)
                    .location()
                    .build();
        }

        sender.send(request);
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
                .build();
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
                    .postback("Confirm", Payload.CONFIRM_RIDE)
                    .postback("Discard", Payload.DISCARD_RIDE)
                    .build();
        } else {
            request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.END_ERROR)
                    .location()
                    .build();
        }
        sender.send(request);
    }

    @Postback(value = Payload.DISCARD_RIDE)
    public void handleDiscardRide(User user) {
        orderService.removeByUser(user);
        userService.save(user, State.LOGGED);
        startHandler.handleLoggedState(user);
    }

    @Postback(value = Payload.CONFIRM_RIDE)
    public void handleConfirmRide(User user) {
        if (uberRideService.confirmRide(user)) {
            userService.save(user, State.FARE_CONFIRMED);
        } else {
            sender.send(user, "Errooor");
        }
    }
}