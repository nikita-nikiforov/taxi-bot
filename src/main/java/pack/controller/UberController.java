package pack.controller;

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
import pack.service.WebhookService;
import pack.service.dao.UberCredentialService;
import pack.service.dao.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UberController {
    @Autowired
    private UberAuthService uberAuthService;

    @Autowired
    private UserService userService;

    @Autowired
    private StartHandler startHandler;

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private UberRideService uberRideService;

    @Autowired
    private UberCredentialService uberCredentialService;

    @Autowired
    private AppProperties appProperties;

    @GetMapping(value = "/uber-auth", params = {"code", "state"})
    public void getUberCode(@RequestParam("code") String code, @RequestParam("state") long chatId,
                              HttpServletResponse response) {
        User user = userService.getUserByChatId(chatId);        // Get user from DB
        // If user credentials is absent, then authorize
        if(!uberCredentialService.getCredentialByChatIdOptional(chatId).isPresent()){
            // If User's access token have been caught and saved, then true
            boolean success = uberAuthService.authorizeUser(chatId, code);
            if (success) {
                userService.save(user, State.LOGGED);
                startHandler.handleLoggedState(user);
            } else {
                startHandler.handleGetStarted(user);
            }
        }
        try {
            response.sendRedirect(appProperties.getCLOSE_SUCCESS_WEBVIEW_URL());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // When user denied to give access to Uber
    @GetMapping(value = "/uber-auth", params = {"error", "state"})
    public void failedAuth(@RequestParam("error") String error, @RequestParam("state") long chatId,
                            HttpServletResponse response) {
        try {
            response.sendRedirect(appProperties.getCLOSE_ERROR_WEBVIEW_URL());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/uber-webhook")
    public void getUberResponse(@RequestBody StatusChangedResponse response) {
        switch (response.getEvent_type()) {
            case "requests.status_changed":
                webhookService.handleStatusChangeWebhook(response);
            case "requests.receipt_ready":
                webhookService.handleReceiptWebhook(response);
        }
    }
}