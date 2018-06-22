package pack.controller;

import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pack.constant.State;
import pack.entity.User;
import pack.handler.StartHandler;
import pack.init.AppProperties;
import pack.model.StatusChangedResponse;
import pack.service.UberAuthService;
import pack.service.UberRideService;
import pack.service.UserService;
import pack.service.api.UberApiService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UberController {
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
    private UberRideService uberRideService;

    @Autowired
    private AppProperties appProperties;

    @GetMapping("/uber-auth")
    public void getUberCode(@RequestParam("code") String code, @RequestParam("state") long chatId,
                              HttpServletResponse response) {
        User user = userService.getUserByChatId(chatId);        // Get user from DB         // Message to be sent
        // If User's access token have been caught and saved, then true
        boolean success = uberAuthService.authorizeUser(chatId, code);
        if (success) {
            userService.save(user, State.LOGGED);
            startHandler.handleLoggedState(user);

        } else {
            startHandler.handleGetStarted(user);
        }
        try {
            response.sendRedirect(appProperties.getCLOSE_WEBVIEW_URL());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/uber-webhook")
    public void getUberResponse(@RequestBody StatusChangedResponse response) {
        switch (response.getEvent_type()) {
            case "requests.status_changed":
                uberRideService.proceedStatusChangeWebhook(response);
        }
    }

    @GetMapping("/test")
    public String testView() {
        return "uber-auth";
    }
}