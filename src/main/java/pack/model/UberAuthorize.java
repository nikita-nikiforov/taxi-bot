package pack.model;

public class UberAuthorize {

    private String client_id;
    private String response_type;
    private String redirect_uri;

    public UberAuthorize() {
    }

    public UberAuthorize(String client_id, String response_type, String redirect_uri) {
        this.client_id = client_id;
        this.response_type = response_type;
        this.redirect_uri = redirect_uri;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getResponse_type() {
        return response_type;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    @Override
    public String toString() {
        return "UberAuthorize{" +
                "client_id='" + client_id + '\'' +
                ", response_type='" + response_type + '\'' +
                ", redirect_uri='" + redirect_uri + '\'' +
                '}';
    }
}
