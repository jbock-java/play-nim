package io.jbock.surreal;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

class NimWindow extends JPanel {

    private static final String TITLE = "The Game of Nim";
    private static final Color CYAN = new Color(0, 183, 235);
    private static final Color COLOR_HOVER = Color.LIGHT_GRAY;
    private static final Color COLOR_ACTIVE = Color.GRAY;
    private Nim nim;

    private final List<Dot> shapes = new ArrayList<>();

    private Color hoverColor = COLOR_HOVER;

    private final DefaultListModel<Nim> actionsModel = new DefaultListModel<>();
    private final JList<Nim> actions = new JList<>(actionsModel);
    private final JPanel leftPanel = new JPanel();
    private final JPanel rightPanel = new JPanel();
    private final JPanel buttonPanel = new JPanel();
    private final JSplitPane splitPane = new JSplitPane();
    private final JScrollPane scrollPanel = new JScrollPane(actions);
    private final JButton computerMoveButton = new JButton("Computer Move");
    private final JButton newGameButton = new JButton("New Game");
    private final JTextField messagePane = new JTextField();

    private int hoverRow = -1;
    private int hoverPos = -1;

    private final Canvas canvas = new Canvas() {
        @Override
        public void paint(Graphics g) {
            render();
        }
    };
    private Runnable onNewGame = () -> {
    };

    private void clearHover() {
        hoverRow = -1;
        hoverPos = -1;
    }

    private boolean isHover() {
        return hoverRow >= 0 && hoverPos >= 0;
    }

    private static final int WIDTH_CANVAS = 600;
    private static final int HEIGHT_CANVAS = 280;
    private static final int HEIGHT = 320;

    public static final int WIDTH_PANEL = 300;
    public static final int HEIGHT_BUTTON_PANE = 20;

    private boolean setHover(Dot f, boolean hover) {
        boolean result = f.isAt(hoverRow, hoverPos) ^ hover;
        if (hover) {
            hoverRow = f.row;
            hoverPos = f.n;
        }
        return result;
    }

    private boolean onMouseMoved(int x, int y) {
        boolean hoverFound = false;
        boolean anyChange = false;
        for (Dot f : shapes) {
            if (hoverFound) {
                anyChange |= setHover(f, false);
                continue;
            }
            if (f.shape.contains(x, y)) {
                hoverFound = true;
                anyChange |= setHover(f, true);
            } else {
                anyChange |= setHover(f, false);
            }
        }
        if (!hoverFound) {
            clearHover();
        }
        return anyChange;
    }

    private final MouseMotionListener mouseMoveListener = new MouseAdapter() {

        @Override
        public void mouseMoved(MouseEvent e) {
            if (onMouseMoved(e.getX(), e.getY())) {
                render();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (isHover()) {
                for (Dot dot : shapes) {
                    if (dot.isAt(hoverRow, hoverPos)) {
                        boolean contains = dot.shape.contains(e.getX(), e.getY());
                        hoverColor = contains ? COLOR_ACTIVE : COLOR_HOVER;
                        render();
                        break;
                    }
                }
            }
        }
    };
    private final HistoryListener listSelectionListener = new HistoryListener(actions);
    private final JSpinner numRows = new JSpinner();
    private final JCheckBox explore = new JCheckBox("Explore");

    private NimWindow() {
    }

    static NimWindow create() {
        JFrame frame = new JFrame(TITLE);
        frame.setResizable(false);
        NimWindow view = new NimWindow();
        view.setOpaque(true);
        view.createElements();
        view.canvas.createBufferStrategy(1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(view);
        frame.setSize(WIDTH_CANVAS + WIDTH_PANEL, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        frame.getRootPane().registerKeyboardAction(
                e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return view;
    }

    private void arrowAction(String keyStroke, Runnable action) {
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(keyStroke), "KEY_" + keyStroke);
        getActionMap().put("KEY_" + keyStroke, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                render();
            }
        });
    }

    private void keyAction(String keyStroke, Runnable action) {
        String key = "KEY_" + keyStroke.replace(' ', '_');
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(keyStroke), key);
        getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void findActiveRow(int direction) {
        if (nim.isEmpty()) {
            return;
        }
        if (hoverRow == -1) {
            hoverRow = nim.rows() - 1;
        }
        int newRow = Math.max(hoverRow, 0);
        do {
            newRow = Math.floorMod(newRow + direction, nim.rows());
        } while (nim.state()[newRow] == 0 && newRow != hoverRow);
        hoverRow = newRow;
        hoverPos = nim.state()[hoverRow];
    }

    private void createElements() {
        arrowAction("LEFT", () -> {
            if (nim.isEmpty()) {
                return;
            }
            if (hoverRow == -1) {
                findActiveRow(1);
            }
            hoverPos = Math.max(0, hoverPos - 1);
        });
        arrowAction("RIGHT", () -> {
            if (nim.isEmpty()) {
                return;
            }
            if (hoverRow == -1) {
                findActiveRow(1);
            }
            if (nim.state()[hoverRow] == 0) {
                return;
            }
            hoverPos = Math.min(nim.state()[hoverRow], hoverPos + 1);
        });
        arrowAction("UP", () -> findActiveRow(-1));
        arrowAction("DOWN", () -> findActiveRow(1));
        messagePane.setPreferredSize(new Dimension(WIDTH_CANVAS - 150, HEIGHT - HEIGHT_CANVAS));
        messagePane.setEditable(false);
        canvas.setMinimumSize(new Dimension(WIDTH_CANVAS, HEIGHT_CANVAS));
        canvas.setSize(WIDTH_CANVAS, HEIGHT_CANVAS);
        canvas.setVisible(true);
        canvas.setFocusable(false);
        canvas.setBackground(Color.DARK_GRAY);
        add(splitPane);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setEnabled(false);
        actions.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        actions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPanel.setSize(WIDTH_PANEL, HEIGHT - HEIGHT_BUTTON_PANE);
        leftPanel.setSize(WIDTH_PANEL, HEIGHT);
        splitPane.setMinimumSize(new Dimension(WIDTH_PANEL, HEIGHT));
        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(canvas, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(explore);
        panel.add(messagePane);
        panel.add(new JLabel("Rows"));
        numRows.setModel(new SpinnerNumberModel(3, 3, 7, 1));
        panel.add(numRows);
        rightPanel.add(panel, BorderLayout.SOUTH);
        leftPanel.add(scrollPanel, BorderLayout.CENTER);
        buttonPanel.setSize(WIDTH_PANEL, HEIGHT_BUTTON_PANE);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(computerMoveButton);
        buttonPanel.add(newGameButton);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Color
        actions.setForeground(Color.WHITE);
        buttonPanel.setBackground(Color.DARK_GRAY);
        actions.setBackground(Color.DARK_GRAY);
        leftPanel.setBackground(Color.DARK_GRAY);

        // Listeners
        canvas.addMouseMotionListener(mouseMoveListener);
        actions.addListSelectionListener(listSelectionListener);
        actions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    void set(Nim nim) {
        this.nim = nim;
        shapes.clear();
        hoverColor = COLOR_HOVER;
        int[] state = nim.state();
        for (int row = 0; row < state.length; row++) {
            int i = state[row];
            for (int n = 0; n < i; n++) {
                int x = 20 + 26 * n;
                int y = 20 + 26 * row;
                Ellipse2D.Float f = new Ellipse2D.Float(x, y, 20, 20);
                shapes.add(new Dot(n, row, f));
            }
        }
        clearHover();
        BufferStrategy bufferStrategy = canvas.getBufferStrategy();
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        render(g);
    }

    void addToHistory(Nim nim) {
        actionsModel.addElement(nim);
    }

    private void render() {
        if (nim == null) {
            return;
        }
        BufferStrategy bufferStrategy = canvas.getBufferStrategy();
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        render(g);
    }

    private void render(Graphics2D g) {
        if (nim == null) {
            return;
        }
        for (Dot f : shapes) {
            g.setPaint(f.geq(hoverRow, hoverPos) ? hoverColor : CYAN);
            g.fill(f.shape);
            if (f.lt(hoverRow, hoverPos)) {
                g.setPaint(Color.WHITE);
                int width = g.getFontMetrics().stringWidth(Integer.toString(f.n + 1));
                g.drawString(Integer.toString(f.n + 1), f.shape.x + 10.5f - (width / 2f), f.shape.y + 16);
            }
        }
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    private void onSpacePressed() {
        hoverColor = COLOR_ACTIVE;
        render();
    }

    private Runnable onSpaceReleased(Consumer<Nim> onMove) {
        return () -> {
            if (nim.isEmpty()) {
                onNewGame.run();
                return;
            }
            for (Dot dot : shapes) {
                if (dot.isAt(hoverRow, hoverPos)) {
                    onMove.accept(nim.set(dot.row, dot.n));
                    return;
                }
            }
        };
    }

    void setOnMove(Consumer<Nim> onMove) {
        keyAction("pressed SPACE", this::onSpacePressed);
        keyAction("released SPACE", onSpaceReleased(onMove));
        keyAction("pressed ENTER", this::onSpacePressed);
        keyAction("released ENTER", onSpaceReleased(onMove));
        keyAction("typed n", () -> onNewGame.run());
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                for (Dot dot : shapes) {
                    if (dot.shape.contains(e.getX(), e.getY())) {
                        if (!isHover() || !dot.isAt(hoverRow, hoverPos)) {
                            break;
                        }
                        onMove.accept(nim.set(dot.row, dot.n));
                        return;
                    }
                }
                onMouseMoved(e.getX(), e.getY());
                hoverColor = COLOR_HOVER;
                render();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                onMouseMoved(e.getX(), e.getY());
                hoverColor = COLOR_ACTIVE;
                render();
            }
        });
    }

    void setOnNewGame(Runnable onNewGame) {
        this.onNewGame = onNewGame;
        newGameButton.addActionListener(e -> onNewGame.run());
    }

    void setOnHistoryClick(Consumer<Nim> onClick) {
        listSelectionListener.setOnClick(onClick);
    }

    void setOnComputerMoveButtonClicked(Runnable onComputerMove) {
        computerMoveButton.addActionListener(e -> onComputerMove.run());
    }

    void setText(String text) {
        messagePane.setText(text);
    }

    void clearHistory() {
        actionsModel.clear();
    }

    void setSelection(int selection) {
        actions.removeListSelectionListener(listSelectionListener);
        actions.setSelectedIndex(selection);
        actions.addListSelectionListener(listSelectionListener);
    }

    void setOnNumRowsChanged(IntConsumer onNumRowsChanged) {
        numRows.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            onNumRowsChanged.accept(((int) source.getValue()));
        });
    }

    void setOnExploreChanged(Consumer<Boolean> onExploreChanged) {
        explore.addItemListener(e -> onExploreChanged.accept(e.getStateChange() == ItemEvent.SELECTED));
    }

    void setComputerMoveEnabled(boolean enabled) {
        computerMoveButton.setEnabled(enabled);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("nim=" + nim)
                .add("row=" + hoverRow)
                .add("pos=" + hoverPos)
                .toString();
    }
}
