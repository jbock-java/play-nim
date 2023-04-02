package io.jbock.surreal;

import java.util.LinkedHashMap;
import java.util.Map;

class HistoryManager {

    private final NimWindow window;

    private final Map<Nim, Integer> history = new LinkedHashMap<>();

    HistoryManager(NimWindow window) {
        this.window = window;
    }

    void add(Nim nim) {
        window.set(nim);
        Integer selection = history.get(nim);
        if (selection == null) {
            history.put(nim, history.size());
            window.addToHistory(nim);
            window.clearSelection();
        } else {
            window.setSelection(selection);
        } 
    }
}
