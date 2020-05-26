package io.edgerun.emma.util;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MultiValueHashMapTest {

    private MultiValueMap<Integer, String> map;

    @Before
    public void setUp() throws Exception {
        map = new MultiValueHashMap<>();
    }

    @Test
    public void addValue_modifiesCollectionsCorrectly() throws Exception {
        assertTrue(map.addValue(1, "foo"));
        assertTrue(map.addValue(1, "bar"));
        assertTrue(map.addValue(2, "foo"));

        assertThat(map.get(1).size(), is(2));
        assertThat(map.get(2).size(), is(1));

        assertThat(map.get(1).get(0), is("foo"));
        assertThat(map.get(1).get(1), is("bar"));
        assertThat(map.get(2).get(0), is("foo"));
    }

    @Test
    public void removeValue_modifiesCollectionsCorrectly() throws Exception {
        map.addValue(1, "foo");
        map.addValue(1, "bar");
        map.addValue(2, "foo");

        assertTrue(map.removeValue(1, "foo"));

        assertThat(map.get(1).size(), is(1));
        assertThat(map.get(2).size(), is(1));

        assertThat(map.get(1).get(0), is("bar"));
        assertThat(map.get(2).get(0), is("foo"));
    }

    @Test
    public void removeValue_returnsFalseOnSubsequentCalls() throws Exception {
        map.addValue(1, "foo");
        map.addValue(1, "bar");
        map.addValue(2, "foo");

        assertTrue(map.removeValue(1, "foo"));
        assertFalse(map.removeValue(1, "foo"));
    }

    @Test
    public void removeValue_returnsFalseOnNonExistingValue() throws Exception {
        map.addValue(1, "foo");
        assertFalse(map.removeValue(1, "bar"));
    }

    @Test
    public void removeValue_returnsFalseOnNonExistingIndex() throws Exception {
        map.addValue(1, "foo");
        assertFalse(map.removeValue(2, "foo"));
    }

    @Test
    public void flat_size_returnsCorrectSize() throws Exception {
        map.addValue(1, "foo");
        map.addValue(1, "bar");
        map.addValue(2, "foo");

        assertThat(map.flatSize(), is(3L));

        map.removeValue(1, "foo");
        assertThat(map.flatSize(), is(2L));
    }

    @Test
    public void flatForEach_executesActionForEachValue() throws Exception {
        map.addValue(1, "foo");
        map.addValue(1, "bar");
        map.addValue(2, "foo");

        List<String> calls = new ArrayList<>();

        map.flatForEach((k, v) -> calls.add(String.format("%d:%s", k, v)));

        assertThat(calls.size(), is(3));
        assertThat(calls, hasItem("1:foo"));
        assertThat(calls, hasItem("1:bar"));
        assertThat(calls, hasItem("2:foo"));
    }

    @Test
    public void get_onNonExistingKey_returnsNull() throws Exception {
        assertNull(map.get(1));
    }

    @Test
    public void maybe_onNonExistingKey_returnsEmptyOptional() throws Exception {
        assertThat(map.maybe(1).isPresent(), is(false));
    }
}
