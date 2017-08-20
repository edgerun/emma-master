package at.ac.tuwien.dsg.emma.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * PropertiesTest.
 */
public class PropertiesTest {
    @Test
    public void addAll_fromStringArguments() throws Exception {
        Properties properties = new Properties();

        StringArguments stringArguments = new StringArguments("asdf", "123", "--emma.name=foo bar", "--emma.broker.port=1883");

        properties.setAll(stringArguments);

        assertThat(properties.getProperty("emma.name"), is("foo bar"));
        assertThat(properties.getProperty("emma.broker.port"), is("1883"));
        assertNull(properties.getProperty("asdf"));
        assertNull(properties.getProperty("123"));
    }

    @Test
    public void getInt_onValidInt_returnsInt() throws Exception {
        Properties properties = new Properties();

        properties.setProperty("answer", 42);

        assertThat(properties.getInt("answer"), is(42));
    }

    @Test(expected = NumberFormatException.class)
    public void getInt_onInvalidInt_throwsException() throws Exception {
        Properties properties = new Properties();

        properties.setProperty("answer", "fourtytwo");

        properties.getInt("answer");
    }
}