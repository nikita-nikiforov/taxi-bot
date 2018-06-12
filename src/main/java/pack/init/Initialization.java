package pack.init;

import com.botscrew.messengercdk.model.outgoing.element.button.GetStartedButton;
import com.botscrew.messengercdk.model.outgoing.profile.Greeting;
import com.botscrew.messengercdk.model.outgoing.profile.menu.NestedMenuItem;
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
    Messenger messenger;

    @PostConstruct
    public void initMessengerProfile() {
        messenger.setGetStartedButton(new GetStartedButton("GET_STARTED"));

        messenger.setGreeting(new Greeting("Hi!"));

        PersistentMenu menu = new PersistentMenu(
                Arrays.asList(
                        new PostbackMenuItem("Order a taxi", "MAKE_ORDER"),
                        new PostbackMenuItem("Show the last trips", "SHOW_TRIPS"),
                        NestedMenuItem.builder()
                            .title("Others")
                            .addMenuItem(new PostbackMenuItem("Add favorite location",
                                    "ADD_LOCATION"))
                            .addMenuItem(NestedMenuItem.builder()
                                    .title("Current trip features")
                                    .addMenuItem(PostbackMenuItem.builder()
                                            .title("Rate the trip")
                                            .payload("RATE_TRIP")
                                            .build())
                                    .addMenuItem(PostbackMenuItem.builder()
                                            .title("Show driver info")
                                            .payload("DRIVER_INFO")
                                            .build())
                                    .build())
                            .addMenuItem(new PostbackMenuItem("Call support", "CALL_SUPPORT"))
                            .addMenuItem(new WebMenuItem("Visit our website", "https://uber.com"))
                            .build()
                )
        );

        messenger.setPersistentMenu(menu);

    }

}
