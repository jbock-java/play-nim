package io.jbock.surreal;

import javax.swing.SwingUtilities;

class NimChecker {

    public static void main(String[] args) {
        NimPresenter presenter = NimPresenter.create();
        SwingUtilities.invokeLater(() -> {
            presenter.set(Nim.create(3, 4, 6));
        });
    }
}
