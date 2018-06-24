package pack.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.UberCredentialRepository;
import pack.entity.UberCredential;
import pack.entity.User;

@Service
public class UberCredentialService {
    @Autowired
    private UberCredentialRepository uberCredentialRepository;

    public UberCredential getCredentialByChatId(long chatId) {
        return uberCredentialRepository.findByUserChatId(chatId);
    }

    public boolean save(UberCredential uberCredential) {
        UberCredential saved = uberCredentialRepository.save(uberCredential);
        return saved != null;
    }

    public String getAccessTokenByUser(User user) {
        return uberCredentialRepository.findByUserChatId(user.getChatId()).getAccess_token();
    }
}
