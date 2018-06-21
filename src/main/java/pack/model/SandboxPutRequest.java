package pack.model;

public class SandboxPutRequest {
    private String status;

    public SandboxPutRequest() {
    }

    public SandboxPutRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
