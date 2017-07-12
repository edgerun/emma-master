package at.ac.tuwien.dsg.emma.overlay;

import java.util.Objects;

/**
 * Destination.
 */
public class Destination {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Destination that = (Destination) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
