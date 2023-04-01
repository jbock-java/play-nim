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
            window.clearSelection();
            window.set(nim);
            result.onComputerMoveButtonClicked();
        });
        window.setOnNewGame(() -> {
            window.setText("Let's start!!!");
            window.clearHistory();
            result.state = Nim.random(ThreadLocalRandom.current().nextInt(4) + 3);            
            window.set(result.state);
        });
        window.setOnHistoryClick(nim -> {
            result.state = nim;
            window.set(result.state, false);
        });
        window.setOnComputerMoveButtonClicked(result::onComputerMoveButtonClicked);
        return result;
    }

    void onComputerMoveButtonClicked() {
        if (state.isEmpty()) {
            window.set(state);
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
        state = state.randomMove();
        window.set(state);
        window.setText("Wow. It was a good move.");
    }

    void set(Nim nim) {
        window.set(nim);
    }
}
