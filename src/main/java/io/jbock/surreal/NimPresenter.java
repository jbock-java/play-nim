package io.jbock.surreal;

class NimPresenter {

    private final NimWindow window;

    private NimPresenter(NimWindow window) {
        this.window = window;
    }

    static NimPresenter create() {
        NimWindow window = NimWindow.create();
        window.setOnClick(window::set);
        window.setOnNewGame(() -> window.set(Nim.create(3, 4, 6)));
        return new NimPresenter(window);
    }

    void set(Nim nim) {
        window.set(nim);
    }
}
