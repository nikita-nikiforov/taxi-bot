package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.messengercdk.service.Sender;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import pack.entity.User;
import pack.service.UberService;
import pack.service.UserService;

@ChatEventsProcessor
public class UberHandler {

    @Autowired
    Sender sender;

    @Autowired
    UberService uberService;

    @Autowired
    UserService userService;

    @Postback(value = "UBER_CALL", states = "LOGGED")
    public void makeUberOrder(User user) {
        JSONObject jsonObject = uberService.makeMagic(user);

        sender.send(user, jsonObject.toString());
    }
}
