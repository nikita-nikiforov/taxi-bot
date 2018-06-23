package pack.model;

import pack.entity.Order;

public class FareRequest {
    private String product_id;
    private double start_latitude;
    private double start_longitude;
    private double end_latitude;
    private double end_longitude;

    public FareRequest() {
    }

    public FareRequest(String product_id, double start_latitude, double start_longitude,
                       double end_latitude, double end_longitude) {
        this.product_id = product_id;
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.end_latitude = end_latitude;
        this.end_longitude = end_longitude;
    }

    public FareRequest(Order order, ProductItem productItem) {
        this.start_latitude = order.getStartLat();
        this.start_longitude = order.getStartLong();
        this.end_latitude = order.getEndLat();
        this.end_longitude = order.getEndLong();
        this.product_id = productItem.getProduct_id();
    }

    public String getProduct_id() {
        return product_id;
    }

    public double getStart_latitude() {
        return start_latitude;
    }

    public double getStart_longitude() {
        return start_longitude;
    }

    public double getEnd_latitude() {
        return end_latitude;
    }

    public double getEnd_longitude() {
        return end_longitude;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setStart_latitude(double start_latitude) {
        this.start_latitude = start_latitude;
    }

    public void setStart_longitude(double start_longitude) {
        this.start_longitude = start_longitude;
    }

    public void setEnd_latitude(double end_latitude) {
        this.end_latitude = end_latitude;
    }

    public void setEnd_longitude(double end_longitude) {
        this.end_longitude = end_longitude;
    }
}
