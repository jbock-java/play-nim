package io.jbock.surreal;

import io.parmigiano.Permutation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Nim {

    private final int[] state;

    private Nim(int[] state) {
        this.state = state;
    }

    static Nim create(int... state) {
        return new Nim(state);
    }

    static Nim random(int rows, int max) {
        int[] state = new int[rows];
        while (true) {
            for (int i = 0; i < rows; i++) {
                int n = ThreadLocalRandom.current().nextInt(max - 1) + 1;
                state[i] = n;
            }
            if (good(state)) {
                return create(state);
            }
        }
    }

    private static boolean good(int[] state) {
        if (isZero(sum(state))) {
            return false;
        }
        Set<Integer> test = new HashSet<>();
        for (int i : state) {
            if (!test.add(i)) {
                return false;
            }
        }
        return true;
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

    Nim set(int row, int n) {
        int[] newState = Arrays.copyOf(state, state.length);
        newState[row] = n;
        return new Nim(newState);
    }

    static int[] binary(int n) {
        return bin(BigInteger.valueOf(n));
    }

    private static int[] bin(BigInteger n) {
        int[] result = new int[n.bitLength()];
        for (int i = 0; i < result.length; i++) {
            result[i] = n.testBit(i) ? 1 : 0;
        }
        return result;
    }

    private static int translate(int[] bin) {
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
        return sum(state, exclude);
    }

    private static int[] sum(int[] state) {
        return sum(state, -1);
    }

    private static int[] sum(int[] state, int exclude) {
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

    private static int[] add(int[] a, int[] b) {
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

    int[] state() {
        return state;
    }

    Nim randomMove() {
        Permutation p = Permutation.random(state.length);
        int[] newState = p.apply(state);
        for (int i = 0; i < newState.length; i++) {
            if (newState[i] > 0) {
                newState[i] -= ThreadLocalRandom.current().nextInt(1, newState[i]);
                break;
            }
        }
        return create(p.invert().apply(newState));
    }

    boolean isEmpty() {
        return isZero(state);
    }

    private static boolean isZero(int[] ints) {
        for (int i : ints) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }

    int rows() {
        return state.length;
    }
}
