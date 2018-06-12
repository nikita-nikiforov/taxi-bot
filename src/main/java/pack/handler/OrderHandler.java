//package pack.handler;
//
//import com.botscrew.botframework.annotation.ChatEventsProcessor;
//import com.botscrew.messengercdk.service.Sender;
//import org.springframework.beans.factory.annotation.Autowired;
//import pack.service.UserService;
//
//@ChatEventsProcessor
//public class OrderHandler {
//
//    @Autowired
//    private UserService userService;
//
//    private Sender sender;
//
//
//    @Postback(value = "MAKE_ORDER", states = "INITIAL")
//    public void handleMakeOrder(MessengerUser user) {
//        userService.save(user.getChatId(), "START_INPUT");
//        sender.send(user, MessageText.START_INPUT.toString());
//    }
//
//    @Autowired
//    public void setSender(Sender sender) {
//        this.sender = sender;
//    }
//}
