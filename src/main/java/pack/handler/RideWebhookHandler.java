package pack.handler;

import com.botscrew.messengercdk.model.outgoing.builder.TextMessage;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pack.constant.MessageText;
import pack.entity.User;
import pack.model.ReceiptResponse;
import pack.service.MessageService;

@Component
public class RideWebhookHandler {
    @Autowired
    private Sender sender;

    @Autowired
    private UberRideHandler uberRideHandler;

    @Autowired
    private MessageService messageService;

    public void handleProcessing(User user) {
        uberRideHandler.handleUberProcessingText(user);
    }

    public void handleAccepted(User user) {
        uberRideHandler.handleUberAcceptedText(user);
    }

    public void handleArriving(User user) {
        uberRideHandler.handleUberArrivingText(user);
    }

    public void handleInProgress(User user) {
        uberRideHandler.handleUberInProgressText(user);
    }

    public void handleCompleted(User user) {
        uberRideHandler.handleUberCompletedText(user);
    }

    public void handleReceipt(User user, ReceiptResponse receiptResponse) {
        String receipt = messageService.getReceiptTemplate(receiptResponse);
        Request request = TextMessage.builder()
                .user(user)
                .text(receipt)
                .build();
        sender.send(request);
        sender.send(user, MessageText.BYE);
    }
}
