package io.jbock.surreal;

import java.math.BigInteger;
import java.util.Arrays;

public final class Nim {

    private static final byte ZERO = 0;
    private static final byte ONE = 1;

    private final int[] state;

    private Nim(int[] state) {
        this.state = state;
    }

    static Nim create(int... state) {
        return new Nim(state);
    }

    boolean move() {
        int x = nimSum();
        if (x == 0) {
            return false;
        }
        for (int i = 0; i < state.length; i++) {
            int n = translate(nimSum(bin(state[i]), x));
            if (n < state[i]) {
                state[i] = n;
                return true;
            }
        }
        return false;
    }

    private int nimSum() {
        int[] sum = new int[0];
        for (int s : state) {
            sum = nimSum(sum, bin(s));
        }
        return translate(sum);
    }

    int[] bin(int n) {
        return bin(BigInteger.valueOf(n));
    }

    int[] bin(BigInteger n) {
        int[] result = new int[n.bitLength()];
        for (int i = 0; i < result.length; i++) {
            result[i] = n.testBit(i) ? ONE : ZERO;
        }
        return result;
    }

    int[] nimSum(int[] a, int[] b) {
        int[] result = new int[Math.max(a.length, b.length)];
        for (int i = 0; i < result.length; i++) {
            int k = i >= a.length ? ZERO : a[i];
            int l = i >= b.length ? ZERO : b[i];
            result[i] = (k + l) % 2;
        }
        return result;
    }

    int[] nimSum(int[] a, int n) {
        return nimSum(a, bin(n));
    }

    int translate(int[] bin) {
        int result = 0;
        for (int i = 0; i < bin.length; i++) {
            int b = bin[i];
            result += b * Math.pow(2, i);
        }
        return result;
    }

    @Override
    public String toString() {
        return Arrays.toString(state) + " | " + nimSum();
    }
}
