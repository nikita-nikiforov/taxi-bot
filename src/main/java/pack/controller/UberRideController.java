package pack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pack.model.StatusChangedResponse;
import pack.service.UberOrderService;

@Controller
public class UberRideController {

    @Autowired
    private UberOrderService uberOrderService;

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
