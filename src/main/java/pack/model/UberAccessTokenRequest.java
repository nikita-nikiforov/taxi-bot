package pack.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class UberAccessTokenRequest {
    private String client_secret;
    private String client_id;
    private String grant_type;
    private String redirect_uri;
    private String code;

    public UberAccessTokenRequest() {
    }

    public UberAccessTokenRequest(String client_secret, String client_id, String grant_type, String redirect_uri, String code) {
        this.client_secret = client_secret;
        this.client_id = client_id;
        this.grant_type = grant_type;
        this.redirect_uri = redirect_uri;
        this.code = code;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "UberAccessTokenRequest{" +
                "client_secret='" + client_secret + '\'' +
                ", client_id='" + client_id + '\'' +
                ", grant_type='" + grant_type + '\'' +
                ", redirect_uri='" + redirect_uri + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
