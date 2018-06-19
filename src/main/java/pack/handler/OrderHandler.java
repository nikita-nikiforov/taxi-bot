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
import pack.entity.Orderr;
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
    private UberService uberService;

    @Autowired
    private UberOrderService uberOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MapboxService mapboxService;

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
        Optional<Coordinates> startPoint = orderService.handleAddress(text);

        if (startPoint.isPresent()) {
            Coordinates coord = startPoint.get();
            orderService.setStartLocation(user, coord);

            userService.save(user, State.START_TEXT_ASKED);

            Request request = GenericTemplate.builder()
                    .addElement(TemplateElement.builder()
                        .title(MessageText.TEXT_ASKED_TITLE)
                        .subtitle(MessageText.START_TEXT_ASKED_SUBTITLE)
                        .imageUrl(mapboxService.getConfirmAddressMapUrl(coord))
                        .button(new PostbackButton("Yes", Payload.CONFIRM_START_POINT))
                        .button(new PostbackButton("No", Payload.DISCARD_START_POINT))
                        .build())
                    .user(user)
                    .build();

            sender.send(request);
        } else {
            Request request = QuickReplies.builder()
                    .user(user)
                    .text(MessageText.PLACE_NOT_FOUND)
                    .location()
                    .build();
            sender.send(request);
        }
    }

    @Postback(value = Payload.CONFIRM_START_POINT)
    public void handleConfirmStartPoint(User user) {
        Orderr order = orderService.getOrderByChatId(user.getChatId());

        Coordinates coord = new Coordinates();
        coord.setLatitude(order.getStartLat());
        coord.setLongitude(order.getStartLong());

        handleStartInputLocation(user, coord);
    }

    @Postback(value = Payload.DISCARD_START_POINT)
    public void handleDiscardStartPoint(User user) {
        userService.save(user, State.START_INPUT);
        handleMakeOrder(user);
    }

    @Location(states = {State.START_INPUT})
    public void handleStartInputLocation(User user, @Location Coordinates coord) {
        Request request;

        // Check whether any product is available
        List<ProductItem> productsNearBy = uberOrderService.getProductsNearBy(user, coord);

        if (!productsNearBy.isEmpty()) {
            orderService.setStartLocation(user, coord);
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
                    .text(MessageText.PLACE_NOT_FOUND)
                    .build();
        }

        sender.send(request);
    }


    @Text(states = State.END_INPUT)
    public void handleEndInput(User user, @Text String text) {
        Request request;
        String answer;

        Optional<Coordinates> endPoint = orderService.handleAddress(text);  // get Coordinates
        if (endPoint.isPresent()) {
            Coordinates coord = endPoint.get();
            orderService.addEndPoint(user, coord);                  // Set them to the order in DB
            userService.save(user, State.END_TEXT_ASKED);        // Update user's state

            request = GenericTemplate.builder()
                    .addElement(TemplateElement.builder()
                            .title(MessageText.TEXT_ASKED_TITLE)
                            .subtitle(MessageText.END_TEXT_ASKED_SUBTITLE)
                            .imageUrl(mapboxService.getConfirmAddressMapUrl(coord))
                            .button(new PostbackButton("Yes", Payload.CONFIRM_END_POINT))
                            .button(new PostbackButton("No", Payload.DISCARD_END_POINT))
                            .build())
                    .user(user)
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

    @Postback(value = Payload.CONFIRM_END_POINT)
    public void handleConfirmEndPoint(User user) {
        Orderr order = orderService.getOrderByChatId(user.getChatId());

        Coordinates coord = new Coordinates();
        coord.setLatitude(order.getEndLat());
        coord.setLongitude(order.getEndLong());

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