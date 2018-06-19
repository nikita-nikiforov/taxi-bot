package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.ListTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.SenderAction;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.element.WebAction;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.model.HistoryItem;
import pack.service.MessageService;
import pack.service.UberService;
import pack.service.UserService;

import java.util.List;

@ChatEventsProcessor
public class UberHandler {

    @Value("${uber-login-link}")
    private String LOGIN_LINK;

    @Value("${uber-logout-link}")
    private String LOGOUT_LINK;

    @Autowired
    Sender sender;

    @Autowired
    UberService uberService;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    // TODO

    @Postback(value = Payload.UBER_AUTH, states = State.INITIAL)
    public void makeUberOrder(User user) {
//        JSONObject jsonObject = uberService.makeMagic(user);

        String IMAGE_URL = "https://farm2.staticflickr.com/1740/42628921432_cca0a3d541_z_d.jpg";
        TemplateElement element = TemplateElement.builder()
                .title("Title")
                .subtitle("Subtitle")
                .imageUrl(IMAGE_URL)
                .build();
        Request request = ListTemplate.builder()
                .addElement(TemplateElement.builder()
                        .title("Authorization")
                        .imageUrl(IMAGE_URL)
                        .build())
                .addElement(TemplateElement.builder()
                        .title("Uber log in")
                        .subtitle("Authorization link")
                    .defaultAction(WebAction.builder()
                            .url(LOGIN_LINK + "&state=" + user.getChatId())
                            .makeTallWebView()
                            .build())
                .build())
                .addElement(TemplateElement.builder()
                        .title("Uber log out")
                        .subtitle("Link to log out")
                        .defaultAction(WebAction.builder()
                                .url(LOGOUT_LINK)
                                .makeTallWebView()
                                .build())
                        .build())
                .user(user)
                .build();

        sender.send(SenderAction.typingOn(user));
        sender.send(request);
    }

    @Postback(value = Payload.SHOW_TRIPS)
    public void handleDefaultButton(User user) {
        List<HistoryItem> list = uberService.getHistoryList(user);
//        String answer = messageService.getHistoryRides(list);
        List<TemplateElement> templateElements = messageService.getTemplateElements(list);

        Request request = GenericTemplate.builder()
                .elements(templateElements)
                .user(user)
                .build();

        sender.send(request);
    }
}
