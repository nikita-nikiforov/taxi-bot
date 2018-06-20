package pack.model;

import pack.entity.Order;
import pack.entity.OrderUberInfo;

public class MakeTripRequest {
    private String fare_id;
    private String product_id;
    private double start_latitude;
    private double start_longitude;
    private double end_latitude;
    private double end_longitude;

    public MakeTripRequest() {
    }

    public MakeTripRequest(Order order, OrderUberInfo uberInfo) {
        this.fare_id = uberInfo.getFare_id();
        this.product_id = uberInfo.getProduct_id();
        this.start_latitude = order.getStartLat();
        this.start_longitude = order.getStartLong();
        this.end_latitude = order.getEndLat();
        this.end_longitude = order.getEndLong();
    }

    public String getFare_id() {
        return fare_id;
    }

    public void setFare_id(String fare_id) {
        this.fare_id = fare_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public double getStart_latitude() {
        return start_latitude;
    }

    public void setStart_latitude(double start_latitude) {
        this.start_latitude = start_latitude;
    }

    public double getStart_longitude() {
        return start_longitude;
    }

    public void setStart_longitude(double start_longitude) {
        this.start_longitude = start_longitude;
    }

    public double getEnd_latitude() {
        return end_latitude;
    }

    public void setEnd_latitude(double end_latitude) {
        this.end_latitude = end_latitude;
    }

    public double getEnd_longitude() {
        return end_longitude;
    }

    public void setEnd_longitude(double end_longitude) {
        this.end_longitude = end_longitude;
    }
}
