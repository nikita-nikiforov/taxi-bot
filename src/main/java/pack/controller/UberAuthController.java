package pack.controller;

import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pack.constant.State;
import pack.entity.User;
import pack.handler.StartHandler;
import pack.service.UberAuthService;
import pack.service.UberService;
import pack.service.UserService;

@Controller
public class UberAuthController {

    @Autowired
    Sender sender;

    @Autowired
    UberService uberService;

    @Autowired
    UberAuthService uberAuthService;

    @Autowired
    UserService userService;

    @Autowired
    StartHandler startHandler;

    @ResponseBody
    @GetMapping("uber-link")
    public String getUberCode(@RequestParam("code") String code, @RequestParam("state") long chatId) {
        User user = userService.getUserByChatId(chatId);        // Get user from DB

        String result;                                          // Message to be sent
        // If User's access token have been got and saved, then true
        boolean success = uberAuthService.authorizeUser(chatId, code);
        if (success) {
            result = "Successfully authorized. Please, return to the chat.";
            userService.save(user, State.LOGGED);
            startHandler.handleAuthorizedState(user);

        } else {
            result = "Failed to authorize";
            startHandler.handleInitialText(user);
        }
        return result;
    }

}