package io.jbock.surreal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NimTest {

    @Test
    void testPlay() {
        Nim nim = Nim.create(1, 20, 17);
        List<Nim> moves = nim.moves();
        assertEquals(List.of(Nim.create(1, 16, 17)), moves);
    }

    @Test
    void testPlay2() {
        Nim nim = Nim.create(3, 4, 5);
        List<Nim> moves = nim.moves();
        assertEquals(List.of(Nim.create(1, 4, 5)), moves);
    }

    @Test
    void testPlay3() {
        Nim nim = Nim.create(12, 7, 8);
        List<Nim> moves = nim.moves();
        assertEquals(List.of(Nim.create(12, 4, 8)), moves);
    }

    @Test
    void testPlay4() {
        Nim nim = Nim.create(1, 4, 6);
        List<Nim> moves = nim.moves();
        assertTrue(moves.contains(Nim.create(1, 4, 5)));
    }

    @Test
    void testPlay5() {
        Nim nim = Nim.create(13, 15, 7);
        List<Nim> moves = nim.moves();
        assertEquals(3, moves.size());
        assertTrue(moves.contains(Nim.create(8, 15, 7)));
        assertTrue(moves.contains(Nim.create(13, 15, 2)));
        assertTrue(moves.contains(Nim.create(13, 10, 7)));
    }

    @Test
    void testPlay6() {
        Nim nim = Nim.create(6, 3, 4);
        List<Nim> moves = nim.moves();
        assertEquals(List.of(Nim.create(6, 2, 4)), moves);
    }

    @Test
    void testPlay7() {
        Nim nim = Nim.create(13, 7, 8);
        List<Nim> moves = nim.moves();
        assertEquals(List.of(Nim.create(13, 5, 8)), moves);
    }

    @Test
    void testPlay8() {
        Nim nim = Nim.create(5, 16, 20);
        List<Nim> moves = nim.moves();
        assertEquals(List.of(Nim.create(4, 16, 20)), moves);
    }

    @Test
    void testPlay9() {
        Nim nim = Nim.create(2, 13, 14);
        List<Nim> moves = nim.moves();
        assertEquals(List.of(Nim.create(2, 12, 14)), moves);
    }

    @Test
    void testPlay10() {
        Nim nim = Nim.create(1, 3, 7, 6);
        List<Nim> moves = nim.moves();
        assertTrue(moves.contains(Nim.create(1, 0, 7, 6)));
        assertTrue(moves.contains(Nim.create(1, 3, 4, 6)));
        assertTrue(moves.contains(Nim.create(1, 3, 7, 5)));
    }
}