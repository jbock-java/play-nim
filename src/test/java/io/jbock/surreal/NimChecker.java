package io.jbock.surreal;

import javax.swing.SwingUtilities;

class NimChecker {

    public static void main(String[] args) {
        NimWindow w = NimWindow.create();
        SwingUtilities.invokeLater(() -> {
            w.set(Nim.create(3, 4, 6));
        });
    }
}
