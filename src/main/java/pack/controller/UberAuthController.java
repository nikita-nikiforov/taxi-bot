package pack.controller;

import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pack.constant.State;
import pack.entity.User;
import pack.handler.StartHandler;
import pack.model.StatusChangedResponse;
import pack.service.UberAuthService;
import pack.service.UberOrderService;
import pack.service.api.UberApiService;
import pack.service.UserService;

@Controller
public class UberAuthController {

    @Autowired
    Sender sender;

    @Autowired
    UberApiService uberApiService;

    @Autowired
    UberAuthService uberAuthService;

    @Autowired
    UserService userService;

    @Autowired
    StartHandler startHandler;

    @Autowired
    UberOrderService uberOrderService;

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
            startHandler.handleGetStarted(user);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "uber-webhook")
    public String getUberResponse(@RequestBody StatusChangedResponse response) {
        switch (response.getEvent_type()) {
            case "requests.status_changed":
                uberOrderService.proceedStatusChangedWebhook(response);
        }
        return "success";
    }
}