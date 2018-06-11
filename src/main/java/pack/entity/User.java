package pack.entity;

import com.botscrew.botframework.domain.user.Bot;
import com.botscrew.botframework.domain.user.PlatformUser;
import com.botscrew.messengercdk.model.DefaultMessengerBot;
import com.botscrew.messengercdk.model.MessengerUser;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User implements MessengerUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private Long chatId;
    private String state;

    public User() {
        state = "INITIAL";
    }

    public User(Long chatId, String state) {
        this.chatId = chatId;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Long getChatId() {
        return chatId;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public Bot getBot() {
        return new DefaultMessengerBot((Long)null, (String)null);
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setState(String state) {
        this.state = state;
    }
}