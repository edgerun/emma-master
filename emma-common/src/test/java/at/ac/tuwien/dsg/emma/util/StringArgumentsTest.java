package at.ac.tuwien.dsg.emma.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

/**
 * StringArgumentsTest.
 */
public class StringArgumentsTest {
    @Test
    public void parse() throws Exception {
        StringArguments args = new StringArguments("arg1", "--longopt1=longval1", "--longopt2", "--longopt3");

        Map<String, String> options = args.parseOptions();

        assertThat(options.size(), is(3));
        assertThat(options.get("longopt1"), is("longval1"));
        assertThat(options.get("longopt2"), is("true"));
        assertThat(options.get("longopt3"), is("true"));
    }
}