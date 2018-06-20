package pack.model;

public class StatusChangedResponse {
    private String event_id;
    private long event_time;
    private String event_type;
    private Meta meta;
    private String resource_href;

    public static class Meta {
        private String user_id;
        private String resource_id;
        private String status;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getResource_id() {
            return resource_id;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public long getEvent_time() {
        return event_time;
    }

    public void setEvent_time(long event_time) {
        this.event_time = event_time;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getResource_href() {
        return resource_href;
    }

    public void setResource_href(String resource_href) {
        this.resource_href = resource_href;
    }
}