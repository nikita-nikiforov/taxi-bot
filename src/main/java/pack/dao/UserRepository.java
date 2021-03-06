package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.UberRide;
import pack.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(long id);

    User findByCredentialUuid(String uuid);

    User findByOrderUberRide(UberRide uberRide);
}
