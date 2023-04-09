package io.jbock.surreal;

import static javax.swing.SwingUtilities.invokeLater;

public class Main {

    public static void main(String[] args) {
        invokeLater(() -> NimPresenter.create().init());
    }
}
