package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.dao.OrderRepository;
import pack.dao.UberCredentialRepository;
import pack.dao.UberRideRepository;
import pack.dao.UserRepository;
import pack.entity.Order;
import pack.entity.UberCredential;
import pack.entity.UberRide;
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
            case "clean all":
                cleanAll(user);
                sender.send(user, "Bye!");
                break;
            default: sender.send(user, "Can't understand you.");
        }

    }

    private void cleanAll(User user) {
        uberApiService.deleteRideRequest(user);

        userRepository.delete(user.getChatId());

        Order order = orderService.getOrderByChatId(user.getChatId());
        orderRepository.delete(order);

        UberRide uberRide = uberRideRepository.findByOrderUserChatId(user.getChatId()).get();
        uberRideRepository.delete(uberRide);

        UberCredential uberCredential = uberCredentialRepository.findByUserChatId(user.getChatId());
        uberCredentialRepository.delete(uberCredential);
    }
}
