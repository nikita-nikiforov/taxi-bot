package pack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.UberCredentialRepository;
import pack.entity.UberCredential;
import pack.entity.User;

@Service
public class UberCredentialService {

    @Autowired
    UberCredentialRepository uberCredentialRepository;

    public UberCredential getCredentialByChatId(long chatId) {
        return uberCredentialRepository.findByUserChatId(chatId);
    }

    public void save(UberCredential uberCredential) {
        uberCredentialRepository.save(uberCredential);
    }

    public String getAccessTokenByUser(User user) {
        return uberCredentialRepository.findByUserChatId(user.getChatId()).getAccess_token();
    }
}
