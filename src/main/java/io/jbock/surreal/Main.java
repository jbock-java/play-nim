package io.jbock.surreal;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        NimPresenter presenter = NimPresenter.create();
        SwingUtilities.invokeLater(presenter::init);
    }
}
