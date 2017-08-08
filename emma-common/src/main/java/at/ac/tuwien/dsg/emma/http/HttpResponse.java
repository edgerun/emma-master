package at.ac.tuwien.dsg.emma.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;

import at.ac.tuwien.dsg.emma.util.IOUtils;

/**
 * HttpResponse.
 */
public class HttpResponse implements Closeable {

    private HttpURLConnection connection;

    private int responseCode = -1;
    private String content;

    public HttpResponse(HttpURLConnection connection) throws IOException {
        this.connection = connection;
    }

    public HttpResponse open() throws IOException {
        try {
            connection.getInputStream();
        } catch (IOException e) {
            if (e.getMessage().contains("returned HTTP response code")) {
                //
            } else {
                throw new IOException();
            }
        }
        return this;
    }

    public boolean isOk() {
        return getResponseCode() < 300 && getResponseCode() >= 200;
    }

    public String getContent() {
        if (content != null) {
            return content;
        }

        try {
            content = IOUtils.toString(getInputStream());
            return content;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public InputStream getInputStream() throws IOException {
        return connection.getInputStream();
    }

    public int getResponseCode() {
        try {
            if (responseCode == -1) {
                responseCode = connection.getResponseCode();
            }

            return responseCode;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(connection.getInputStream());
        connection = null;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "connection=" + connection +
                ", responseCode=" + getResponseCode() +
                '}';
    }
}
