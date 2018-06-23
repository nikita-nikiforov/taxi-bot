package pack.service.api;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pack.factory.CoordinatesFactory;

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

    // Take in text, return Coordinates
    public Optional<Coordinates> getCoordinatesFromAddress(String address) {
        Optional<Coordinates> result = Optional.empty();                // to return
        try {
            // Request to Google
            GeocodingResult[] geoResults = GeocodingApi.geocode(geoApiContext, address).await();
            if (geoResults.length > 0) {
                GeocodingResult geoResult = geoResults[0];      // Get the first
                // Transform to Coordinates
                Coordinates coord = CoordinatesFactory.create(geoResult.geometry.location.lat,
                        geoResult.geometry.location.lng);
                result = Optional.of(coord);
            }
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
