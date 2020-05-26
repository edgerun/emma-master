package io.edgerun.emma.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Properties.
 */
public class Properties extends java.util.Properties {

    private static final long serialVersionUID = 1L;

    public Properties() {

    }

    public Properties(java.util.Properties defaults) {
        super(defaults);
    }

    public URI getURI(String key) {
        return URI.create(getProperty(key));
    }

    public URL getURL(String key) {
        try {
            return new URL(getProperty(key));
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
    }

    public int getInt(String key) throws NumberFormatException {
        return Integer.parseInt(getProperty(key));
    }

    public Object setProperty(String key, Object value) {
        return setProperty(key, String.valueOf(value));
    }

    public void load(String resource) {
        load(resource, Properties.class.getClassLoader());
    }

    public void load(String resource, ClassLoader cl) {
        try (InputStream in = cl.getResourceAsStream(resource)) {
            load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Parses option-like arguments (i,e., "--option=value") from the given StringArguments and adds them as properties
     * to this Properties object.
     *
     * @param arguments the arguments.
     */
    public void setAll(StringArguments arguments) {
        putAll(arguments.parseOptions());
    }

}
