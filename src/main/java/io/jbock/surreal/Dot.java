package io.jbock.surreal;

import java.awt.geom.Ellipse2D;

final class Dot {
  final int n;
  final int row;
  final Ellipse2D.Float shape;

  Dot(int n, int row, Ellipse2D.Float shape) {
    this.n = n;
    this.row = row;
    this.shape = shape;
  }

  boolean gt(int otherRow, int otherPos) {
    return row == otherRow && n > otherPos;
  }

  boolean le(int otherRow, int otherPos) {
    return row == otherRow && n <= otherPos;
  }

  boolean isAt(int otherRow, int otherPos) {
    return row == otherRow && n == otherPos;
  }
}
