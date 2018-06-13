package pack.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
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

    public Optional<LatLng> getAddress(String address) {
        Optional<LatLng> result = Optional.empty();                // to return
        try {
            GeocodingResult[] geoResults = GeocodingApi.geocode(geoApiContext, address).await();
            if (geoResults.length > 0) {
                GeocodingResult geoResult = geoResults[0];
                result = Optional.of(geoResult.geometry.location);
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
