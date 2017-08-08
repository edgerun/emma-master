package at.ac.tuwien.dsg.emma.http;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;

import org.junit.Test;

/**
 * HttpRequestTest.
 */
public class HttpRequestTest {
    @Test
    public void toUrl_withoutQuery_createsUrlCorrectly() throws Exception {
        URL url = new HttpRequest(URI.create("http://localhost")).toUrl();

        assertEquals("http://localhost", url.toString());
    }

    @Test
    public void toUrl_withSingleQueryParameter_createsUrlCorrectly() throws Exception {
        URL url = new HttpRequest(URI.create("http://localhost"))
                .param("foo", "bar")
                .toUrl();

        assertEquals("http://localhost?foo=bar", url.toString());
    }

    @Test
    public void toUrl_withMultipleQueryParameters_createsUrlCorrectly() throws Exception {
        URL url = new HttpRequest(URI.create("http://localhost"), new LinkedHashMap<>())
                .param("foo", "bar")
                .param("answer", 42)
                .toUrl();

        assertEquals("http://localhost?foo=bar&answer=42", url.toString());
    }
}