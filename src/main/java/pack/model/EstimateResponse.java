package pack.model;

public class EstimateResponse {
    private Fare fare;
    private Trip trip;
    private int pickup_estimate;

    public static class Fare {
        private Breakdown[] breakdown;
        private double value;
        private String fare_id;
        private long expires_at;
        private String display;
        private String currency_code;

        public static class Breakdown {
            private String type;
            private String name;
            private double value;

            public Breakdown() {
            }

            public String getType() {
                return type;
            }

            public String getName() {
                return name;
            }

            public double getValue() {
                return value;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setValue(double value) {
                this.value = value;
            }
        }

        public Breakdown[] getBreakdown() {
            return breakdown;
        }

        public double getValue() {
            return value;
        }

        public String getFare_id() {
            return fare_id;
        }

        public long getExpires_at() {
            return expires_at;
        }

        public String getDisplay() {
            return display;
        }

        public String getCurrency_code() {
            return currency_code;
        }
    }

    public class Trip {
        private String distance_unit;
        private int duration_estimate;
        private double distance_estimate;

        public String getDistance_unit() {
            return distance_unit;
        }

        public int getDuration_estimate() {
            return duration_estimate;
        }

        public double getDistance_estimate() {
            return distance_estimate;
        }
    }

    public Fare getFare() {
        return fare;
    }

    public Trip getTrip() {
        return trip;
    }

    public int getPickup_estimate() {
        return pickup_estimate;
    }
}

