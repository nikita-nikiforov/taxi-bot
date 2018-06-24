package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.builder.TextMessage;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.element.quickreply.PostbackQuickReply;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.service.MessageService;
import pack.service.UberRideService;

import java.util.Arrays;


@ChatEventsProcessor
public class UberRideHandler {

    @Autowired
    private Sender sender;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UberRideService uberRideService;

    @Autowired
    private RideWebhookHandler rideWebhookHandler;

    @Text(states = State.UBER_PROCESSING)
    public void handleUberProcessingText(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.UBER_PROCESSING)
                .postback("Cancel order", Payload.CANCEL_UBER_RIDE)
                .build();
        sender.send(request);
    }

    @Text(states = State.UBER_ACCEPTED)
    public void handleUberAcceptedText(User user) {
        TemplateElement vehicleTemplate = messageService.getVehicleTemplate(user);
        Request request = GenericTemplate.builder()
                .user(user)
                .addElement(vehicleTemplate)
                .quickReplies(Arrays.asList(new PostbackQuickReply("Driver info", Payload.DRIVER_INFO),
                        new PostbackQuickReply("Cancel order", Payload.CANCEL_UBER_RIDE)))
                .build();
        sender.send(user, MessageText.UBER_ACCEPTED);
        sender.send(request);
    }

    @Text(states = State.UBER_ARRIVING)
    public void handleUberArrivingText(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.UBER_ARRIVING)
                .postback("Driver info", Payload.DRIVER_INFO)
                .postback("Cancel order", Payload.CANCEL_UBER_RIDE)

                .build();
        sender.send(request);
    }

    @Text(states = State.UBER_IN_PROGRESS)
    public void handleUberInProgressText(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.UBER_IN_PROGRESS)
                .postback("Driver info", Payload.DRIVER_INFO)
                .build();
        sender.send(request);
    }

    @Text(states = State.UBER_COMPLETED)
    public void handleUberCompletedText(User user) {
        Request request = TextMessage.builder()
                .user(user)
                .text(MessageText.UBER_COMPLETED)
                .build();
        sender.send(request);
    }

    @Postback(Payload.DRIVER_INFO)
    public void handleDriverInfo(User user) {
        Request request = GenericTemplate.builder()
                .user(user)
                .addElement(messageService.getDriverTemplate(user))
                .build();
        sender.send(request);
        sender.send(user, "Phone number: " + messageService.getDriverPhone(user));
    }

    @Postback(Payload.CANCEL_UBER_RIDE)
    public void handleCancelUberRide(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.CANCEL_TRIP_ASK)
                .postback("Yes", Payload.CONFIRM_RIDE_CANCEL)
                .postback("No", Payload.DISCARD_RIDE_CANCEL)
                .build();
        sender.send(request);
    }

    @Postback(Payload.CONFIRM_RIDE_CANCEL)
    public void handleConfirmRideCancel(User user) {
        uberRideService.cancelUberRide(user);
        Request request = TextMessage.builder()
                .user(user)
                .text(MessageText.RIDE_CANCELED)
                .build();
        sender.send(request);
    }
}
