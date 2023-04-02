package io.jbock.surreal;

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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NimWindow extends JFrame {

    public static final String TITLE = "The Game of Nim";
    private Nim nim;

    private final List<Dot> shapes = new ArrayList<>();

    private Dot hover;

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

    private final Canvas canvas = new Canvas() {
        @Override
        public void paint(Graphics g) {
            render();
        }
    };

    private static final int WIDTH_CANVAS = 600;
    private static final int HEIGHT_CANVAS = 280;
    private static final int HEIGHT = 320;

    public static final int WIDTH_PANEL = 300;
    public static final int HEIGHT_BUTTON_PANE = 20;


    private final MouseMotionListener mouseListener = new MouseAdapter() {

        @Override
        public void mouseMoved(MouseEvent e) {
            if (hover != null && hover.shape.contains(e.getX(), e.getY())) {
                return;
            }
            boolean hoverFound = false;
            for (Dot f : shapes) {
                if (hoverFound) {
                    f.hover = false;
                    continue;
                }
                if (f.shape.contains(e.getX(), e.getY())) {
                    hoverFound = true;
                    f.hover = true;
                    hover = f;
                } else {
                    f.hover = false;
                }
            }
            if (hoverFound) {
                render();
                return;
            }
            if (hover != null) {
                render();
            }
            hover = null;
        }
    };
    private final HistoryListener listSelectionListener = new HistoryListener(actions);

    private NimWindow() {
        super(TITLE);
    }

    static NimWindow create() {
        NimWindow view = new NimWindow();
        view.setSize(WIDTH_CANVAS + WIDTH_PANEL, HEIGHT);
        view.createElements();
        view.setVisible(true);
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.getRootPane().registerKeyboardAction(
                e -> view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        view.canvas.createBufferStrategy(1);
        view.setLocationRelativeTo(null);
        return view;
    }

    @Override
    public BufferStrategy getBufferStrategy() {
        return canvas.getBufferStrategy();
    }

    private void createElements() {
        setResizable(false);
        messagePane.setPreferredSize(new Dimension(WIDTH_CANVAS - 150, HEIGHT - HEIGHT_CANVAS));
        messagePane.setEditable(false);
        canvas.setMinimumSize(new Dimension(WIDTH_CANVAS, HEIGHT_CANVAS));
        canvas.setSize(WIDTH_CANVAS, HEIGHT_CANVAS);
        canvas.setVisible(true);
        canvas.setFocusable(false);
        canvas.setBackground(Color.DARK_GRAY);
        getContentPane().add(splitPane);
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
        panel.add(new JCheckBox("Explore"));
        panel.add(messagePane);
        panel.add(new JLabel("Rows"));
        panel.add(new JSpinner());
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
        canvas.addMouseMotionListener(mouseListener);
        actions.addListSelectionListener(listSelectionListener);
        actions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    void set(Nim nim) {
        this.nim = nim;
        shapes.clear();
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
        canvas.repaint();
    }

    void addToHistory(Nim nim) {
        actionsModel.addElement(nim);
    }

    private void render() {
        if (nim == null) {
            return;
        }
        BufferStrategy bufferStrategy = getBufferStrategy();
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        for (Dot f : shapes) {
            g.setPaint(f.geq(hover) ? Color.RED : Color.CYAN);
            g.fill(f.shape);
        }
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    void setOnMove(Consumer<Nim> onMove) {
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                for (Dot dot : shapes) {
                    if (dot.shape.contains(e.getX(), e.getY())) {
                        if (hover == null || !hover.hover || hover != dot) {
                            return;
                        }
                        onMove.accept(nim.set(dot.row, dot.n));
                        break;
                    }
                }
            }
        });
    }

    void setOnNewGame(Runnable onNewGame) {
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

    void clearSelection() {
        actions.removeListSelectionListener(listSelectionListener);
        actions.clearSelection();
        actions.addListSelectionListener(listSelectionListener);
    }

    void setSelection(int selection) {
        actions.removeListSelectionListener(listSelectionListener);
        actions.setSelectedIndex(selection);
        actions.addListSelectionListener(listSelectionListener);
    }
}
