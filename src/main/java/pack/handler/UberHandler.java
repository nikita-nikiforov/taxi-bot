package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.entity.User;
import pack.service.UberService;

@ChatEventsProcessor
public class UberHandler {

    @Autowired
    Sender sender;

    @Autowired
    UberService uberService;

    @Postback(value = "AUTH_UBER")
    public void authInUber(User user) {
        uberService.authUser();
    }
}
