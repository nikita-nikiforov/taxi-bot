package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.FavoritePlace;

import java.util.List;

public interface FavPlaceRepository extends JpaRepository<FavoritePlace, Integer> {
    List<FavoritePlace> findAllByUserChatId(long chatId);
}
