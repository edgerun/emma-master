package io.edgerun.emma.controller.overlay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.edgerun.emma.controller.network.graph.AscendingPair;

/**
 * AscendingPairTest.
 */
public class AscendingPairTest {
    @Test
    public void withUnequalUnorderedValues_behavesCorrectly() throws Exception {
        AscendingPair<Integer> p1 = AscendingPair.of(2, 1);
        AscendingPair<Integer> p2 = AscendingPair.of(1, 2);

        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));

        assertEquals("(1,2)", p1.toString());
        assertEquals("(1,2)", p2.toString());
    }

    @Test
    public void withUnequalOrderedValues_behavesCorrectly() throws Exception {
        AscendingPair<Integer> p1 = AscendingPair.of(1, 2);
        AscendingPair<Integer> p2 = AscendingPair.of(1, 2);

        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
    }

    @Test
    public void withEqualValues_behavesCorrectly() throws Exception {
        AscendingPair<Integer> p1 = AscendingPair.of(1, 1);
        AscendingPair<Integer> p2 = AscendingPair.of(1, 1);
        AscendingPair<Integer> p3 = AscendingPair.of(1, 2);

        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
        assertFalse(p1.equals(p3));
        assertFalse(p2.equals(p3));

        assertEquals("(1,1)", p1.toString());
        assertEquals("(1,1)", p2.toString());
    }
}