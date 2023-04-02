package io.jbock.surreal;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class NimPresenter {

    private final NimWindow window;
    private final HistoryManager historyManager;

    private Nim state;

    private NimPresenter(NimWindow window, HistoryManager historyManager) {
        this.window = window;
        this.historyManager = historyManager;
    }

    static NimPresenter create() {
        NimWindow window = NimWindow.create();
        window.setText("Let's start!!!");
        HistoryManager historyManager = new HistoryManager(window);
        NimPresenter presenter = new NimPresenter(window, historyManager);
        window.setOnMove(presenter::onMove);
        window.setOnNewGame(presenter::onNewGame);
        window.setOnHistoryClick(presenter::onHistoryClick);
        window.setOnComputerMoveButtonClicked(presenter::onComputerMoveButtonClicked);
        return presenter;
    }

    private void onMove(Nim newState) {
        state = newState;
        historyManager.add(state);
        onComputerMoveButtonClicked();
    }

    private void onNewGame() {
        window.setText("Let's start!!!");
        window.clearHistory();
        state = Nim.random(ThreadLocalRandom.current().nextInt(2) + 3);
        historyManager.add(state);
    }

    private void onHistoryClick(Nim nim) {
        window.setText("");
        state = nim;
        window.set(state);
    }

    private void onComputerMoveButtonClicked() {
        if (state.isEmpty()) {
            historyManager.add(state);
            window.setText("You won!!!");
            return;
        }
        List<Nim> moves = state.moves();
        if (!moves.isEmpty()) {
            state = moves.get(ThreadLocalRandom.current().nextInt(moves.size()));
            historyManager.add(state);
            if (state.isEmpty()) {
                window.setText("I won!!!");
            } else {
                window.setText("Phew. I am sure you could do better.");
            }
            return;
        }
        state = state.randomMove();
        historyManager.add(state);
        window.setText("Wow. It was a good move.");
    }

    void set(Nim nim) {
        historyManager.add(nim);
    }
}
