package io.jbock.surreal;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class NimPresenter {

    private final NimWindow window;
    
    private Nim state;

    private NimPresenter(NimWindow window) {
        this.window = window;
    }

    static NimPresenter create() {
        NimWindow window = NimWindow.create();
        window.setText("Let's start!!!");
        NimPresenter result = new NimPresenter(window);
        window.setOnClick(nim -> {
            result.state = nim;
            window.set(nim);
            result.onComputerMoveButtonClicked();
        });
        window.setOnNewGame(() -> {
            window.setText("Let's start!!!");
            result.state = Nim.random(3);            
            window.set(result.state);
        });
        window.setOnHistoryClick(nim -> {
            result.state = nim;
            window.set(result.state);
        });
        window.setOnComputerMoveButtonClicked(result::onComputerMoveButtonClicked);
        return result;
    }

    void onComputerMoveButtonClicked() {
        if (state.isEmpty()) {
            window.setText("You won!!!");
            return;
        }
        List<Nim> moves = state.moves();
        if (!moves.isEmpty()) {
            state = moves.get(ThreadLocalRandom.current().nextInt(moves.size()));
            window.set(state);
            if (state.isEmpty()) {
                window.setText("I won!!!");
            } else {
                window.setText("Phew. I am sure you could do better.");
            } 
            return;
        }
        window.setText("Wow. It was a good move.");
        state = state.randomMove();
        window.set(state);
    }

    void set(Nim nim) {
        window.set(nim);
    }
}
