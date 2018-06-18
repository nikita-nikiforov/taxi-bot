package pack.controller;

import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pack.entity.User;
import pack.service.UberAuthService;
import pack.service.UberService;
import pack.service.UserService;

@Controller
public class UberController {

    @Autowired
    Sender sender;

    @Autowired
    UberService uberService;

    @Autowired
    UberAuthService uberAuthService;

    @Autowired
    UserService userService;

    @ResponseBody
    @GetMapping("uber-link")
    public String getUberCode(@RequestParam("code") String code, @RequestParam("state") long chatId) {
        User user = userService.getUserByChatId(chatId);

        String result;
        boolean success = uberAuthService.authorizeUser(chatId, code);
        if (success) {
            result = "Successfully authorized.";
            userService.save(user, "LOGGED");
            sender.send(user,"You have been authorized");

        } else {
            result = "Failed to authorize";
            sender.send(user,"You have been authorized");

        }
        return result;
    }
}
