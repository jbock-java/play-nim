package io.jbock.surreal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Nim {

    private final int[] state;

    private Nim(int[] state) {
        this.state = state;
    }

    static Nim create(int... state) {
        return new Nim(state);
    }

    List<Nim> moves() {
        List<Nim> result = new ArrayList<>();
        for (int j = 0; j < state.length; j++) {
            int sum = translate(sum(j));
            if (sum < state[j]) {
                result.add(set(j, sum));
            }
        }
        return result;
    }

    private Nim set(int i, int n) {
        int[] newState = Arrays.copyOf(state, state.length);
        newState[i] = n;
        return new Nim(newState);
    }

    int[] binary(int n) {
        return bin(BigInteger.valueOf(n));
    }

    private int[] bin(BigInteger n) {
        int[] result = new int[n.bitLength()];
        for (int i = 0; i < result.length; i++) {
            result[i] = n.testBit(i) ? 1 : 0;
        }
        return result;
    }

    int translate(int[] bin) {
        int result = 0;
        int pow = 1;
        for (int i : bin) {
            result += i * pow;
            pow *= 2;
        }
        return result;
    }

    @Override
    public String toString() {
        return Arrays.toString(state);
    }

    private int[] sum(int exclude) {
        int[] result = new int[0];
        for (int j = 0; j < state.length; j++) {
            if (j == exclude) {
                continue;
            }
            int i = state[j];
            result = add(result, binary(i));
        }
        return result;
    }

    private int[] add(int[] a, int[] b) {
        int[] result = new int[Math.max(a.length, b.length)];
        for (int i = 0; i < result.length; i++) {
            int ai = i >= a.length ? 0 : a[i];
            int bi = i >= b.length ? 0 : b[i];
            result[i] = (ai + bi) % 2;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nim nim = (Nim) o;
        return Arrays.equals(state, nim.state);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(state);
    }
}
