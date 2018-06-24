package pack.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.UberCredentialRepository;
import pack.entity.UberCredential;
import pack.entity.User;

import java.util.Optional;

@Service
public class UberCredentialService {
    @Autowired
    private UberCredentialRepository uberCredentialRepository;

    public UberCredential getCredentialByChatId(long chatId) {
        return uberCredentialRepository.findByUserChatId(chatId);
    }

    public Optional<UberCredential> getCredentialByChatIdOptional(long chatId) {
        Optional<UberCredential> result = Optional.empty();
        UberCredential credential = getCredentialByChatId(chatId);
        if(credential != null) result = Optional.of(credential);
        return result;
    }

    public boolean save(UberCredential uberCredential) {
        UberCredential saved = uberCredentialRepository.save(uberCredential);
        return saved != null;
    }

    public String getAccessTokenByUser(User user) {
        return uberCredentialRepository.findByUserChatId(user.getChatId()).getAccess_token();
    }
}
