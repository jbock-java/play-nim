package io.jbock.surreal;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class NimWindow extends JFrame {

    private Nim nim;

    private final List<HoverShape> shapes = new ArrayList<>();

    private HoverShape hover;

    private static final class HoverShape {
        final int n;
        final int row;
        final Ellipse2D.Float shape;
        boolean hover;

        private HoverShape(int n, int row, Ellipse2D.Float shape) {
            this.n = n;
            this.row = row;
            this.shape = shape;
        }

        boolean geq(HoverShape other) {
            if (other == null) {
                return false;
            }
            return row == other.row && n >= other.n;
        }
    }

    private static final int WIDTH_CANVAS = 560;
    private static final int HEIGHT = 400;

    public static final int WIDTH_PANEL = 280;
    public static final int HEIGHT_BUTTON_PANE = 20;

    private final Canvas canvas = new Canvas() {
        @Override
        public void paint(Graphics g) {
            render();
        }
    };

    private final MouseMotionListener mouseListener = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (hover != null && hover.shape.contains(e.getX(), e.getY())) {
                return;
            }
            boolean found = false;
            boolean update = false;
            for (HoverShape f : shapes) {
                if (f.shape.contains(e.getX(), e.getY())) {
                    hover = f;
                    if (!f.hover) {
                        update = true;
                    }
                    f.hover = true;
                    found = true;
                } else {
                    if (f.hover) {
                        update = true;
                    }
                    f.hover = false;
                } 
            }
            if (!found) {
                hover = null;
            }
            if (update) {
                render();
            }
        }
    };

    private final JList<Nim> actions = new JList<>();
    private final JPanel sidePanel = new JPanel();
    private final JPanel buttonPanel = new JPanel();
    private final JSplitPane splitPane = new JSplitPane();
    private final JScrollPane scrollPanel = new JScrollPane(actions);
    private final JButton pauseButton = new JButton("Undo");
    private final JButton editButton = new JButton("New Game");

    private NimWindow() {
        super("The Game of Nim");
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
        canvas.setSize(WIDTH_CANVAS, HEIGHT);
        canvas.setVisible(true);
        canvas.setFocusable(false);
        canvas.setBackground(Color.DARK_GRAY);
        getContentPane().add(splitPane);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(sidePanel);
        splitPane.setRightComponent(canvas);
        splitPane.setEnabled(false);
        actions.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        actions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPanel.setSize(WIDTH_PANEL, HEIGHT - HEIGHT_BUTTON_PANE);
        sidePanel.setSize(WIDTH_PANEL, HEIGHT);
        splitPane.setMinimumSize(new Dimension(WIDTH_PANEL, HEIGHT));
        sidePanel.setLayout(new BorderLayout());
        sidePanel.add(scrollPanel, BorderLayout.CENTER);
        buttonPanel.setSize(WIDTH_PANEL, HEIGHT_BUTTON_PANE);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(pauseButton);
        buttonPanel.add(editButton);
        sidePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Color
        actions.setForeground(Color.WHITE);
        buttonPanel.setBackground(Color.DARK_GRAY);
        actions.setBackground(Color.DARK_GRAY);
        sidePanel.setBackground(Color.DARK_GRAY);
        canvas.addMouseMotionListener(mouseListener);
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
                shapes.add(new HoverShape(n, row, f));
            }
        }
        canvas.repaint();
    }

    private void render() {
        if (nim == null) {
            return;
        }
        BufferStrategy bufferStrategy = getBufferStrategy();
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        for (HoverShape f : shapes) {
            g.setPaint(f.geq(hover) ? Color.RED : Color.CYAN);
            g.fill(f.shape);
        }
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
}
