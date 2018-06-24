package pack.entity;

import javax.persistence.*;

@Entity
public class UberCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String access_token;
    private String uuid;
    private boolean has_fav_places;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UberCredential() {
        has_fav_places = false;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isHas_fav_places() {
        return has_fav_places;
    }

    public void setHas_fav_places(boolean has_fav_places) {
        this.has_fav_places = has_fav_places;
    }
}
