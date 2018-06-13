package pack.service;

import com.google.maps.model.LatLng;
import org.springframework.stereotype.Service;

@Service
public class UberService {

    public void handleCoordinates(LatLng latLng) {
        System.out.println(latLng.toString());
    }

}
