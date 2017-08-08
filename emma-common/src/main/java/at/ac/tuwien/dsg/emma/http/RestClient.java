package at.ac.tuwien.dsg.emma.http;

import java.net.URI;

public class RestClient {

    private URI rootURI;

    public RestClient(String rootUri) {
        this(URI.create(rootUri));
    }

    public RestClient(URI rootURI) {
        this.rootURI = rootURI;
    }

    public HttpRequest request() {
        return new HttpRequest(rootURI);
    }

    public HttpRequest request(String endpoint) {
        return new HttpRequest(rootURI.resolve(endpoint));
    }

}
