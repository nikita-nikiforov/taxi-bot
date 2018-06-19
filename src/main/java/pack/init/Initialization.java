package pack.init;

import com.botscrew.messengercdk.model.outgoing.element.button.GetStartedButton;
import com.botscrew.messengercdk.model.outgoing.profile.Greeting;
import com.botscrew.messengercdk.model.outgoing.profile.menu.PersistentMenu;
import com.botscrew.messengercdk.model.outgoing.profile.menu.PostbackMenuItem;
import com.botscrew.messengercdk.model.outgoing.profile.menu.WebMenuItem;
import com.botscrew.messengercdk.service.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class Initialization {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    Messenger messenger;

    @PostConstruct
    public void initMessengerProfile() {
        messenger.setGetStartedButton(new GetStartedButton("GET_STARTED"));

        messenger.setGreeting(new Greeting("Hi!"));

        PersistentMenu menu = new PersistentMenu(
                Arrays.asList(
                        new PostbackMenuItem("Call support", "CALL_SUPPORT"),
                        new WebMenuItem("Visit our website", "https://uber.com")
                )
        );

        messenger.setPersistentMenu(menu);
    }

//    @PostConstruct
//    public void updateWebhook() {
//        System.out.println(BASE_URL);
//        messenger.setWebHook("https://5230e671.ngrok.io" + "/messenger/events",
//                Arrays.asList(
//                        WebHook.Field.MESSAGES,
//                        WebHook.Field.POSTBACKS
//                ));
//    }
}