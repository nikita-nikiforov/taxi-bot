package pack.model;

public class HistoryResponse {

    private int count;
    private History[] history;

    public static class History {
        private String status;
        private String distance;
        private String product_id;
        private String start_time;
        private StartCity start_city;
        private String end_time;
        private String request_id;
        private String request_time;

        public class StartCity {
            private String latitude;
            private String display_name;
            private String longitude;

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public String getDisplay_name() {
                return display_name;
            }

            public void setDisplay_name(String display_name) {
                this.display_name = display_name;
            }

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }
        }

        public History() {
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public StartCity getStart_city() {
            return start_city;
        }

        public void setStart_city(StartCity start_Start_city) {
            this.start_city = start_Start_city;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getRequest_id() {
            return request_id;
        }

        public void setRequest_id(String request_id) {
            this.request_id = request_id;
        }

        public String getRequest_time() {
            return request_time;
        }

        public void setRequest_time(String request_time) {
            this.request_time = request_time;
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public History[] getHistory() {
        return history;
    }

    public void setHistory(History[] history) {
        this.history = history;
    }
}
