package at.ac.tuwien.dsg.emma.http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RestClient {

    private URI rootURI;

    private Map<String, String> defaultParameters;

    public RestClient(String rootUri) {
        this(URI.create(rootUri));
    }

    public RestClient(URI rootURI) {
        this.rootURI = rootURI;
    }

    public Map<String, String> getDefaultParameters() {
        if (defaultParameters == null) {
            defaultParameters = new HashMap<>();
        }
        return defaultParameters;
    }

    public HttpRequest request() {
        return request(rootURI);
    }

    public HttpRequest request(String endpoint) {
        return request(rootURI.resolve(endpoint));
    }

    private HttpRequest request(URI uri) {
        if (defaultParameters == null) {
            return new HttpRequest(uri);
        } else {
            return new HttpRequest(uri, new HashMap<>(defaultParameters));
        }
    }

}
