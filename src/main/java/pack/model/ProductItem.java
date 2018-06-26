package pack.model;

// Change to ProductResponse.Product TODO
public class ProductItem {
    private String upfront_fare_enabled;
    private String capacity;
    private String product_id;
    private PriceDetails price_details;
    private String image;
    private String cash_enabled;
    private String shared;
    private String short_description;
    private String display_name;
    private String product_group;
    private String description;

    public ProductItem() {
    }

    public class PriceDetails {
        private String[] service_fees;
        private String cost_per_minute;
        private String distance_unit;
        private String minimum;
        private String cost_per_distance;
        private String base;
        private String cancellation_fee;
        private String currency_code;

        public String[] getService_fees() {
            return service_fees;
        }

        public void setService_fees(String[] service_fees) {
            this.service_fees = service_fees;
        }

        public String getCost_per_minute() {
            return cost_per_minute;
        }

        public void setCost_per_minute(String cost_per_minute) {
            this.cost_per_minute = cost_per_minute;
        }

        public String getDistance_unit() {
            return distance_unit;
        }

        public void setDistance_unit(String distance_unit) {
            this.distance_unit = distance_unit;
        }

        public String getMinimum() {
            return minimum;
        }

        public void setMinimum(String minimum) {
            this.minimum = minimum;
        }

        public String getCost_per_distance() {
            return cost_per_distance;
        }

        public void setCost_per_distance(String cost_per_distance) {
            this.cost_per_distance = cost_per_distance;
        }

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public String getCancellation_fee() {
            return cancellation_fee;
        }

        public void setCancellation_fee(String cancellation_fee) {
            this.cancellation_fee = cancellation_fee;
        }

        public String getCurrency_code() {
            return currency_code;
        }

        public void setCurrency_code(String currency_code) {
            this.currency_code = currency_code;
        }
    }

    public String getUpfront_fare_enabled() {
        return upfront_fare_enabled;
    }

    public void setUpfront_fare_enabled(String upfront_fare_enabled) {
        this.upfront_fare_enabled = upfront_fare_enabled;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public PriceDetails getPrice_details() {
        return price_details;
    }

    public void setPrice_details(PriceDetails price_details) {
        this.price_details = price_details;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCash_enabled() {
        return cash_enabled;
    }

    public void setCash_enabled(String cash_enabled) {
        this.cash_enabled = cash_enabled;
    }

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getProduct_group() {
        return product_group;
    }

    public void setProduct_group(String product_group) {
        this.product_group = product_group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
