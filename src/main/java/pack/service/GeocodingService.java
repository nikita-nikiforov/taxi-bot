package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class GeocodingService {
    private GeoApiContext geoApiContext;

    @Autowired
    public GeocodingService(Environment env) {
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(env.getRequiredProperty("google-api-key"))  // get key from properties
                .build();
    }

    public Optional<Coordinates> getCoordinatesFromAddress(String address) {
        Optional<Coordinates> result = Optional.empty();                // to return
        try {
            // Request to Google
            GeocodingResult[] geoResults = GeocodingApi.geocode(geoApiContext, address).await();
            if (geoResults.length > 0) {
                GeocodingResult geoResult = geoResults[0];      // Get the first
                Coordinates coord = new Coordinates();          // Transform to Coordinates
                coord.setLatitude(geoResult.geometry.location.lat);
                coord.setLongitude(geoResult.geometry.location.lng);
                result = Optional.of(coord);
            }

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
