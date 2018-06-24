package pack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.entity.User;
import pack.model.Place;
import pack.service.api.UberApiService;

import java.util.List;
import java.util.Optional;

@Service
public class FavoritePlaceService {

    @Autowired
    private UberApiService uberApiService;

    public Optional<List<Place>> getList(User user) {
        Optional<List<Place>> result = Optional.empty();
        uberApiService.getFavoritePlace("work");
        uberApiService.getFavoritePlace("home");
        return null;
    }
}
