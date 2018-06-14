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
import pack.entity.User;
import pack.service.GeocodingService;
import pack.service.OrderService;
import pack.service.UberService;
import pack.service.UserService;

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
    private OrderService orderService;

    @Postback(value = "MAKE_ORDER", states = "INITIAL")
    public void handleMakeOrder(User user) {
        userService.save(user.getChatId(), "START_INPUT");
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.START_INPUT.toString())
                .postback("My places", "SELECT_FROM_PLACES")
                .location()
                .build();
        sender.send(request);
    }

    @Text(states = {"START_INPUT"})
    public void handleStartInputText(User user, @Text String text) {
        String answer;
        Optional<LatLng> startPoint = orderService.handleAddress(text);
        if (startPoint.isPresent()) {
            orderService.createOrder(user, startPoint.get());
            userService.save(user, "END_INPUT");
            answer = MessageText.START_INPUT_TRUE.toString();
        } else {
            answer = MessageText.START_INPUT_FALSE.toString();
        }

        Request request = QuickReplies.builder()
                .user(user)
                .text(answer)
                .location()
                .build();
        sender.send(request);
    }

    @Location(states = {"START_INPUT"})
    public void handleStartInputLocation(User user, @Location Coordinates coord) {
        LatLng startPoint = new LatLng(coord.getLatitude(), coord.getLongitude());
        orderService.createOrder(user, startPoint);
        userService.save(user, "END_INPUT");
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.START_INPUT_TRUE.toString())
                .location()
                .build();
        sender.send(request);
    }

    @Text(states = {"END_INPUT"})
    public void handleEndInput(User user, @Text String text) {
        Request request;

        String answer;
        Optional<LatLng> endPoint = orderService.handleAddress(text);
        if (endPoint.isPresent()) {
            orderService.addEndPoint(user, endPoint.get());
            userService.save(user, "WAIT_FOR_CAR");
            answer = MessageText.END_INPUT_TRUE.toString();
            request = TextMessage.builder()
                    .user(user)
                    .text(answer)
                    .build();
        } else {
            answer = MessageText.END_INPUT_FALSE.toString();
            request = QuickReplies.builder()
                    .user(user)
                    .text(answer)
                    .location()
                    .build();
        }

        sender.send(request);
    }

    @Location(states = {"END_INPUT"})
    public void handleEndLocation(User user, @Location Coordinates coord) {
        LatLng endPoint = new LatLng(coord.getLatitude(), coord.getLongitude());
        orderService.addEndPoint(user, endPoint);
        userService.save(user, "WAIT_FOR_CAR");
        Request request = TextMessage.builder()
                .user(user)
                .text(MessageText.END_INPUT_TRUE.toString())
                .build();
        sender.send(request);
    }

    @Text(states = "WAIT_FOR_CAR")
    public void handleWaitForCar(User user) {
        sender.send(user, "You're waiting for a car :)");
    }
}