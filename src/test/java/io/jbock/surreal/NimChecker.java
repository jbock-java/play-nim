package io.jbock.surreal;

import javax.swing.SwingUtilities;

class NimChecker {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NimWindow.create();
        });
    }
}
