package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Location;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.builder.TextMessage;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.model.ProductItem;
import pack.service.*;

import java.util.List;
import java.util.Optional;

@ChatEventsProcessor
public class OrderHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private Sender sender;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private UberService uberService;

    @Autowired
    private UberOrderService uberOrderService;

    @Autowired
    private OrderService orderService;

    @Postback(value = Payload.MAKE_ORDER, states = State.LOGGED)
    public void handleMakeOrder(User user) {
        userService.save(user.getChatId(), State.START_INPUT);
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.START_INPUT)
                .postback("My places", "SELECT_FROM_PLACES")
                .location()
                .build();
        sender.send(request);
    }

    @Text(states = {State.START_INPUT})
    public void handleStartInputText(User user, @Text String text) {
        String answer;
        Optional<LatLng> startPoint = orderService.handleAddress(text);
        if (startPoint.isPresent()) {
            orderService.createOrder(user, startPoint.get());
            userService.save(user, State.END_INPUT);
            answer = MessageText.START_INPUT_TRUE;
        } else {
            answer = MessageText.PRODUCTS_ABSENT;
        }

        Request request = QuickReplies.builder()
                .user(user)
                .text(answer)
                .location()
                .build();
        sender.send(request);
    }

    @Location(states = {State.START_INPUT})
    public void handleStartInputLocation(User user, @Location Coordinates coord) {
        LatLng startPoint = new LatLng(coord.getLatitude(), coord.getLongitude());
        Request request;

        // Check whether any product is available
        List<ProductItem> productsNearBy = uberOrderService.getProductsNearBy(user, coord);

        if (!productsNearBy.isEmpty()) {
            orderService.createOrder(user, startPoint);
            userService.save(user, State.END_INPUT);
            request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.START_INPUT_TRUE)
                    .location()
                    .build();
        } else {
            userService.save(user, State.LOGGED);
            request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.PRODUCTS_ABSENT)
                    .build();
        }


        sender.send(request);
    }


    @Text(states = State.END_INPUT)
    public void handleEndInput(User user, @Text String text) {
        Request request;

        String answer;
        Optional<LatLng> endPoint = orderService.handleAddress(text);
        if (endPoint.isPresent()) {
            Coordinates coord = new Coordinates();
            coord.setLatitude(endPoint.get().lat);
            coord.setLongitude(endPoint.get().lng);

            orderService.addEndPoint(user, coord);
            userService.save(user, State.FARE_CONFIRMATION);
            answer = MessageText.END_INPUT_TRUE;
            request = TextMessage.builder()
                    .user(user)
                    .text(answer)
                    .build();
        } else {
            answer = MessageText.END_INPUT_FALSE;
            request = QuickReplies.builder()
                    .user(user)
                    .text(answer)
                    .location()
                    .build();
        }

        sender.send(request);
    }

    @Location(states = {State.END_INPUT})
    public void handleEndLocation(User user, @Location Coordinates coord) {
        orderService.addEndPoint(user, coord);
        String estimateFare = orderService.getEstimateFare(user);
        userService.save(user, State.FARE_CONFIRMATION);
        Request request = QuickReplies.builder()
                .user(user)
                .text(estimateFare)
                .postback("Confirm", Payload.CONFIRM_RIDE)
                .postback("Discard", Payload.DISCARD_RIDE)
                .build();

        sender.send(request);
    }

}