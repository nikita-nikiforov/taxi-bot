package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.service.UserService;

@ChatEventsProcessor
public class DefaultHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private Sender sender;

//    @Text
//    public void defHandler(MessengerUser user) {
//
//    }
}
