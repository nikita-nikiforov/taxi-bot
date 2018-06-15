package pack.entity;

import javax.persistence.*;

@Entity
public class UberCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String access_token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UberCredential() {
    }

    public UberCredential(String access_token, User user) {
        this.access_token = access_token;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
