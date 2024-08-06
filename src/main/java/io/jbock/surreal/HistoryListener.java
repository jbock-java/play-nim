package io.jbock.surreal;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.function.Consumer;

final class HistoryListener implements ListSelectionListener {

  private Consumer<Nim> onClick = nim -> {
  };

  private final JList<Nim> actions;

  HistoryListener(JList<Nim> actions) {
    this.actions = actions;
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }
    int index;
    if (actions.getSelectionModel().isSelectedIndex(e.getFirstIndex())) {
      index = e.getFirstIndex();
    } else {
      index = e.getLastIndex();
    }
    if (index < actions.getModel().getSize()) {
      onClick.accept(actions.getModel().getElementAt(index));
    }
  }

  void setOnClick(Consumer<Nim> onClick) {
    this.onClick = onClick;
  }
}
