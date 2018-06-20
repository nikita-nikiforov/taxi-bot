package pack.entity;

import javax.persistence.*;

@Entity
public class OrderUberInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String fare_id;
    private String product_id;
    private String request_id;

    public OrderUberInfo() {
    }

    public OrderUberInfo(Order order, String fare_id, String product_id) {
        this.order = order;
        this.fare_id = fare_id;
        this.product_id = product_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }
}
