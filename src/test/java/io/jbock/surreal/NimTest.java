package io.jbock.surreal;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class NimTest {

    @Test
    void testPlay() {
        Nim nim = Nim.create(1, 20, 17);
        System.out.println(nim);
        nim.move();
        System.out.println(nim);
    }

    @Test
    void testPlay2() {
        Nim nim = Nim.create(3, 4, 5);
        System.out.println(nim);
        nim.move();
        System.out.println(nim);
    }

    @Test
    void testPlay3() {
        Nim nim = Nim.create(12, 7, 8);
        System.out.println(nim);
        nim.move();
        System.out.println(nim);
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