package pack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pack.init.AppProperties;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class MainController {
    @Autowired
    private AppProperties appProperties;

    @GetMapping("/")
    public void handleMainPage(HttpServletResponse response) {
        try {
            response.sendRedirect(appProperties.getM_ME_LINK());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
