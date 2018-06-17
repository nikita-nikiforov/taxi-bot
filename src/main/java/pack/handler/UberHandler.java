package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
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
//        JSONObject jsonObject = uberService.makeMagic(user);

        String IMAGE_URL = "https://farm2.staticflickr.com/1740/42628921432_cca0a3d541_z_d.jpg";
        TemplateElement element = TemplateElement.builder()
                .title("Title")
                .subtitle("Subtitle")
                .imageUrl(IMAGE_URL)
                .build();
        Request request = GenericTemplate.builder()
                .addElement(element)
                .addElement(element)
                .addElement(element)
                .user(user)
                .build();

        sender.send(request);
    }
}
