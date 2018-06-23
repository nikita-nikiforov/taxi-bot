package pack.model;

public class ReceiptResponse {
    private String request_id;
    private String subtotal;
    private String total_fare;
    private String total_charged;
    private float total_owed;
    private String currency_code;
    private String duration;
    private String distance;
    private String distance_label;

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTotal_fare() {
        return total_fare;
    }

    public void setTotal_fare(String total_fare) {
        this.total_fare = total_fare;
    }

    public String getTotal_charged() {
        return total_charged;
    }

    public void setTotal_charged(String total_charged) {
        this.total_charged = total_charged;
    }

    public float getTotal_owed() {
        return total_owed;
    }

    public void setTotal_owed(float total_owed) {
        this.total_owed = total_owed;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance_label() {
        return distance_label;
    }

    public void setDistance_label(String distance_label) {
        this.distance_label = distance_label;
    }
}
