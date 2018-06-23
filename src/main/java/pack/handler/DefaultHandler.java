package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Location;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.dao.OrderRepository;
import pack.dao.UberCredentialRepository;
import pack.dao.UberRideRepository;
import pack.dao.UserRepository;
import pack.entity.User;
import pack.service.OrderService;
import pack.service.UserService;
import pack.service.api.UberApiService;

@ChatEventsProcessor
public class DefaultHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private Sender sender;

    @Autowired
    UberRideRepository uberRideRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UberCredentialRepository uberCredentialRepository;

    @Autowired
    UserRepository userRepository;

    @Text
    public void handleText(User user, @Text String text) {
        switch (text.toLowerCase()) {
            case "exit":
            case "cansel":
            case "stop":
                orderService.stopTrip(user);
                sender.send(user, "Your trip has been stopped.");
                userService.save(user, "LOGGED");
                startHandler.handleLoggedState(user);
                break;
            default: sender.send(user, "Can't understand you.");
        }
    }

    @Location
    public void handleStandart(User user) {
        sender.send(user, "Can't understand you.");
    }

}
