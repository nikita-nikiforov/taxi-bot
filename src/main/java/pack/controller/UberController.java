package pack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pack.service.UberService;

@Controller
public class UberController {

    @Autowired
    UberService uberService;

    @ResponseBody
    @GetMapping("uber-link")
    public String getUberCode(@RequestParam("code") String code) {

        uberService.authorize(code);
        return code;
    }
}