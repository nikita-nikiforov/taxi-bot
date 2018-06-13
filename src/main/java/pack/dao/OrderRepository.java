package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.Orderr;

public interface OrderRepository extends JpaRepository<Orderr, Integer>{
    Orderr findByUserId(int id);

    Orderr findByUserChatId(Long id);
}
