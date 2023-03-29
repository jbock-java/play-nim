package io.jbock.surreal;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
        assertEquals(3, nim.nimSum());
        nim.moves();
        assertArrayEquals(new int[]{1, 4, 5}, nim.state());
        assertEquals(0, nim.nimSum());
    }

    @Test
    void testPlay5() {
        Nim nim = Nim.create(13, 15, 7);
        List<Nim> moves = nim.moves();
        assertTrue(moves.contains(Nim.create(8, 15, 7)));
        assertTrue(moves.contains(Nim.create(13, 15, 2)));
        assertTrue(moves.contains(Nim.create(13, 10, 7)));
    }

    @Test
    void testNimSum() {
        Nim nim = Nim.create(1, 1, 1);
        int[] bin = nim.bin(13);
        assertArrayEquals(new int[]{1, 0, 1, 1}, bin);
    }

    @Test
    void testNimAdd() {
        Nim nim = Nim.create(1, 1, 1);
        int[] a = nim.nimSum(new int[]{0}, 3);
        System.out.println(Arrays.toString(a));
        int[] b = nim.nimSum(new int[]{0}, 4);
        System.out.println(Arrays.toString(b));
        int[] c = nim.nimSum(new int[]{0}, 5);
        System.out.println(Arrays.toString(c));
        int[] d = nim.nimSum(a, b);
        int[] e = nim.nimSum(d, c);
        System.out.println(Arrays.toString(e));
    }
}