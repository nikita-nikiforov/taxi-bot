package pack.model;

public class ProductResponse {

    private Product[] products;

    public static class Product {
        private boolean upfront_fare_enabled;
        private int capacity;
        private String product_id;
        private PriceDetails price_details;
        private String image;
        private boolean cash_enabled;
        private boolean shared;
        private String short_description;
        private String display_name;
        private String product_group;
        private String description;

        public Product() {
        }

        public static class PriceDetails {
            private ServiceFees[] service_fees;
            private double cost_per_minute;
            private String distance_unit;
            private double minimum;
            private double cost_per_distance;
            private int base;
            private int cancellation_fee;
            private String currency_code;

            public static class ServiceFees {
                private double fee;
                private String name;

                public double getFee() {
                    return fee;
                }

                public void setFee(double fee) {
                    this.fee = fee;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }

            public ServiceFees[] getService_fees() {
                return service_fees;
            }

            public void setService_fees(ServiceFees[] service_fees) {
                this.service_fees = service_fees;
            }

            public double getCost_per_minute() {
                return cost_per_minute;
            }

            public void setCost_per_minute(double cost_per_minute) {
                this.cost_per_minute = cost_per_minute;
            }

            public String getDistance_unit() {
                return distance_unit;
            }

            public void setDistance_unit(String distance_unit) {
                this.distance_unit = distance_unit;
            }

            public double getMinimum() {
                return minimum;
            }

            public void setMinimum(double minimum) {
                this.minimum = minimum;
            }

            public double getCost_per_distance() {
                return cost_per_distance;
            }

            public void setCost_per_distance(double cost_per_distance) {
                this.cost_per_distance = cost_per_distance;
            }

            public int getBase() {
                return base;
            }

            public void setBase(int base) {
                this.base = base;
            }

            public int getCancellation_fee() {
                return cancellation_fee;
            }

            public void setCancellation_fee(int cancellation_fee) {
                this.cancellation_fee = cancellation_fee;
            }

            public String getCurrency_code() {
                return currency_code;
            }

            public void setCurrency_code(String currency_code) {
                this.currency_code = currency_code;
            }
        }

        public boolean isUpfront_fare_enabled() {
            return upfront_fare_enabled;
        }

        public void setUpfront_fare_enabled(boolean upfront_fare_enabled) {
            this.upfront_fare_enabled = upfront_fare_enabled;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
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

        public boolean isCash_enabled() {
            return cash_enabled;
        }

        public void setCash_enabled(boolean cash_enabled) {
            this.cash_enabled = cash_enabled;
        }

        public boolean isShared() {
            return shared;
        }

        public void setShared(boolean shared) {
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
}