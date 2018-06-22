package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.service.MessageService;

@ChatEventsProcessor
public class UberRideHandler {

    @Autowired
    private Sender sender;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RideStatusWebhookHandler rideStatusWebhookHandler;

    @Text(states = State.UBER_PROCESSING)
    public void handleUberProcessingText(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.UBER_PROCESSING)
                .postback("Cancel order", Payload.CANCEL_TRIP)
                .build();
        sender.send(request);
    }

    @Text(states = State.UBER_ACCEPTED)
    public void handleUberAcceptedText(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.UBER_ACCEPTED)
                .postback("Driver info", Payload.DRIVER_INFO)
                .build();
        sender.send(request);
    }

    @Text(states = State.UBER_ARRIVING)
    public void handleUberArrivingText(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.UBER_ARRIVING)
                .postback("Driver info", Payload.DRIVER_INFO)
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
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.UBER_COMPLETED)
                .postback("1", Payload.TRIP_RATE + "?rate=1")
                .postback("2", Payload.TRIP_RATE + "?rate=2")
                .postback("3", Payload.TRIP_RATE + "?rate=3")
                .postback("4", Payload.TRIP_RATE + "?rate=4")
                .postback("5", Payload.TRIP_RATE + "?rate=5")
                .build();
        sender.send(request);
    }

    @Postback(Payload.DRIVER_INFO)
    public void handleDriverInfo(User user) {
        Request request = GenericTemplate.builder()
                .user(user)
                .addElement(messageService.getDriverInfo(user))
                .build();
        sender.send(request);
    }
}
