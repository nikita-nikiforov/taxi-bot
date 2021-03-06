package pack.controller;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pack.factory.CoordinatesFactory;

@Controller
public class MapboxController {
    @GetMapping("/map")
    public String getMap(@RequestParam("lat") double lat, @RequestParam("lng") double lng,
                         @RequestParam("title") String title, Model model) {
        Coordinates coordinates = CoordinatesFactory.create(lat, lng);
        model.addAttribute("lat", lat);
        model.addAttribute("lng", lng);
        model.addAttribute("title", title);
        return "map";
    }
}
