package pack.entity;

import com.botscrew.messengercdk.model.MessengerUser;

import javax.persistence.*;

@Entity
public class User implements MessengerUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private Long chatId;
    private String state;

    @OneToOne(mappedBy = "user")
    private UberCredential credential;

    @OneToOne(mappedBy = "user")
    private Order order;

    public User() {
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

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UberCredential getCredential() {
        return credential;
    }

    public void setCredential(UberCredential credential) {
        this.credential = credential;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}