package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.entity.User;
import pack.service.OrderService;
import pack.service.UserService;

@ChatEventsProcessor
public class DefaultHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private Sender sender;

    @Text
    public void handleText(User user, @Text String text) {
        switch (text.toLowerCase()) {
            case "exit":
            case "cansel":
            case "stop":
                orderService.stopTrip(user);
                sender.send(user, "Your trip has been stopped.");
                userService.save(user, "LOGGED");
                startHandler.handleAuthorizedState(user);
                break;
            default: sender.send(user, "Can't understand you.");
        }

    }
}
