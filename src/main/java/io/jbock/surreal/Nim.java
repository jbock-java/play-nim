package io.jbock.surreal;

import io.parmigiano.Permutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

final class Nim {

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
                int n = ThreadLocalRandom.current().nextInt(1, max + 1);
                state[i] = n;
            }
            if (good(state)) {
                return create(state);
            }
        }
    }

    private static boolean good(int[] state) {
        if (sum(state) == 0) {
            return false;
        }
        HashSet<Integer> test = new HashSet<>();
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
            int sum = sum(j);
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

    @Override
    public String toString() {
        return Arrays.toString(state);
    }

    private int sum(int exclude) {
        return sum(state, exclude);
    }

    private static int sum(int[] state) {
        return sum(state, -1);
    }

    private static int sum(int[] state, int exclude) {
        int result = 0;
        for (int j = 0; j < state.length; j++) {
            if (j == exclude) {
                continue;
            }
            int i = state[j];
            result = result ^ i;
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
        for (int i = 0; i < 5; i++) {
            int[] move = tryRandomMove();
            if (move != null && good(move)) {
                return create(move);
            }
        }
        int[] newState = Arrays.copyOf(state, state.length);
        for (int i = 0; i < state.length; i++) {
            if (newState[i] > 0) {
                newState[i] -= 1;
                break;
            }
        }
        return create(newState);
    }

    private int[] tryRandomMove() {
        Permutation p = Permutation.random(state.length);
        int[] newState = p.apply(state);
        for (int i = 0; i < newState.length; i++) {
            if (newState[i] <= 1) {
                continue;
            }
            newState[i] -= ThreadLocalRandom.current().nextInt(1, newState[i]);
            return p.invert().apply(newState);
        }
        return null;
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
