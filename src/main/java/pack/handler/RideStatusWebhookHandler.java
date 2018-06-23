package pack.handler;

import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pack.constant.Payload;
import pack.entity.UberRide;
import pack.entity.User;
import pack.model.ReceiptResponse;
import pack.service.MessageService;
import pack.service.OrderService;
import pack.service.UberRideService;

@Component
public class RideStatusWebhookHandler {

    @Autowired
    private Sender sender;

    @Autowired
    private UberRideHandler uberRideHandler;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UberRideService uberRideService;

    @Autowired
    private MessageService messageService;

    public void handleProcessing(User user, UberRide uberRide) {
        uberRideHandler.handleUberProcessingText(user);
    }

    public void handleAccepted(User user, UberRide uberRide) {
        uberRideHandler.handleUberAcceptedText(user);
    }

    public void handleArriving(User user, UberRide uberRide) {
        uberRideHandler.handleUberArrivingText(user);
    }

    public void handleInProgress(User user, UberRide uberRide) {
        uberRideHandler.handleUberInProgressText(user);
    }

    public void handleCompleted(User user, UberRide uberRide) {
        uberRideHandler.handleUberCompletedText(user);
    }

    public void handleReceipt(User user, ReceiptResponse receiptResponse) {
        String receipt = messageService.getReceiptTemplate(receiptResponse);
        Request request = QuickReplies.builder()
                .user(user)
                .text(receipt)
                .postback("Details", Payload.FARE_DETAILS)
                .build();
        sender.send(request);
    }
}
