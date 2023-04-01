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
        System.out.println(nim);
        nim.moves();
        System.out.println(nim);
    }

    @Test
    void testPlay2() {
        Nim nim = Nim.create(3, 4, 5);
        System.out.println(nim);
        nim.moves();
        System.out.println(nim);
    }

    @Test
    void testPlay3() {
        Nim nim = Nim.create(12, 7, 8);
        System.out.println(nim);
        nim.moves();
        System.out.println(nim);
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
        System.out.println(moves);
    }

    @Test
    void testPlay7() {
        Nim nim = Nim.create(13, 7, 8);
        List<Nim> moves = nim.moves();
        System.out.println(moves);
    }

    @Test
    void testBinary() {
        int[] bin = Nim.binary(13);
        assertArrayEquals(new int[]{1, 0, 1, 1}, bin);
    }
}