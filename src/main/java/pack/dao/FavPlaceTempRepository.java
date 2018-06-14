package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.FavoritePlaceTemp;

public interface FavPlaceTempRepository extends JpaRepository<FavoritePlaceTemp, Integer> {

    FavoritePlaceTemp findByUserChatIdAndNameIsNull(long chatId);
}
