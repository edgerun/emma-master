package at.ac.tuwien.dsg.emma.manager.rest;

/**
 * ConnectResponse.
 */
public class ConnectResponse {
    private String URI;

    public ConnectResponse(String URI) {
        this.URI = URI;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }
}
