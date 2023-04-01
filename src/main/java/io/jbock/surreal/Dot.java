package io.jbock.surreal;

import java.awt.geom.Ellipse2D;

final class Dot {
    final int n;
    final int row;
    final Ellipse2D.Float shape;
    boolean hover;

    Dot(int n, int row, Ellipse2D.Float shape) {
        this.n = n;
        this.row = row;
        this.shape = shape;
    }

    boolean geq(Dot other) {
        if (other == null) {
            return false;
        }
        if (!other.hover) {
            return false;
        }
        return row == other.row && n >= other.n;
    }

    @Override
    public String toString() {
        return Boolean.toString(hover);
    }
}
