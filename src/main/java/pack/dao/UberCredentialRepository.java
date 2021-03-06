package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.UberCredential;

public interface UberCredentialRepository
        extends JpaRepository<UberCredential, Integer> {
    UberCredential findByUserChatId(long id);

    UberCredential findByUuid(String uuid);
}
