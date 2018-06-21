package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.Payload;
import pack.constant.RideStatusEnum;
import pack.entity.User;
import pack.service.MessageService;

@ChatEventsProcessor
public class UberRideHandler {

    @Autowired
    private Sender sender;

    @Autowired
    private MessageService messageService;

    @Text(states = {"UBER_PROCESSING"})
    public void handleUberProcessingText(User user) {
        Request request = RideStatusEnum.PROCESSING.getRequest(user);
        sender.send(request);
    }

    @Text(states = {"UBER_ACCEPTED"})
    public void handleUberAcceptedText(User user) {
        Request request = RideStatusEnum.ACCEPTED.getRequest(user);
        sender.send(request);
    }

    @Text(states = {"UBER_ARRIVING"})
    public void handleUberArrivingText(User user) {
        Request request = RideStatusEnum.ARRIVING.getRequest(user);
        sender.send(request);
    }

    @Text(states = "UBER_IN_PROGRESS")
    public void handleUberInProgressText(User user) {
        Request request = RideStatusEnum.IN_PROGRESS.getRequest(user);
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
