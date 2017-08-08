package at.ac.tuwien.dsg.emma.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpRequest.
 */
public class HttpRequest {

    private URI uri;
    private Map<String, String> query;

    public HttpRequest(URI uri) {
        this(uri, new HashMap<>());
    }

    public HttpRequest(URI uri, Map<String, String> query) {
        this.uri = uri;
        this.query = query;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    /**
     * Executes this request as a HTTP GET request.
     *
     * @return a response object
     * @throws IOException if an exception occurred during the request
     */
    public HttpResponse get() throws IOException {
        return new HttpResponse((HttpURLConnection) toUrl().openConnection()).open();
    }

    /**
     * Add a parameter to the query.
     *
     * @param key the parameter name
     * @param value the parameter value
     * @return this request for chaining
     */
    public HttpRequest param(String key, Object value) {
        query.put(key, String.valueOf(value));
        return this;
    }

    public URL toUrl() throws MalformedURLException {
        return new URL(uri.toURL() + compileParameters(query));
    }

    private String compileParameters(Map<String, String> map) {
        if (map.isEmpty()) {
            return "";
        }

        StringBuilder str = new StringBuilder();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (str.length() == 0) {
                str = str.append("?");
            } else {
                str.append("&");
            }

            str.append(encode(entry.getKey()))
                    .append("=")
                    .append(encode(entry.getValue()));
        }

        return str.toString();
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "uri=" + uri +
                ", query=" + query +
                '}';
    }
}
