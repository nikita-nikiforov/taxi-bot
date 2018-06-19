package pack.entity;

import javax.persistence.*;

@Entity
public class Orderr {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private double startLat;
    private double startLong;

    private double endLat;
    private double endLong;
    private String fare_id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Orderr() {
    }

    public Orderr(User user) {
        this.user = user;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFare_id() {
        return fare_id;
    }

    public void setFare_id(String fare_id) {
        this.fare_id = fare_id;
    }
}
