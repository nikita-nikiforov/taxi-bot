package pack.entity;

import javax.persistence.*;

@Entity
public class FavoritePlace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private double lat;
    private double lng;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public FavoritePlace() {
    }

    public FavoritePlace(FavoritePlaceTemp tempPlace, String name) {
        this.lat = tempPlace.getLat();
        this.lng = tempPlace.getLng();
        this.user = tempPlace.getUser();
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
