package io.jbock.surreal;

public final class Surreal {
    
    private final Surreal left;
    private final Surreal right;

    public Surreal(Surreal left, Surreal right) {
        this.left = left;
        this.right = right;
    }
}
