package cifrado.mvc;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CifradoView extends JFrame {

    private static final Color PRIMARY = new Color(39, 118, 220);
    private static final Color PRIMARY_DARK = new Color(44, 121, 229);
    private static final Color SUCCESS = new Color(62, 195, 119);
    private static final Color ERROR = new Color(235, 47, 76);
    private static final Color WARNING = new Color(255, 184, 0);
    private static final Color JEANS = new Color(103, 143, 168);
    private static final Color TEXT_PRIMARY = new Color(31, 31, 31);
    private static final Color TEXT_SECONDARY = new Color(91, 92, 95);
    private static final Color LIGHT_BACKGROUND = new Color(248, 249, 251);
    private static final Color LIGHT_PANEL = Color.WHITE;
    private static final Color LIGHT_BORDER = new Color(229, 231, 234);
    private static final Color DARK_BACKGROUND = new Color(18, 18, 18);
    private static final Color DARK_PANEL = new Color(30, 30, 30);
    private static final Color DARK_BORDER = new Color(48, 48, 48);
    private static final Color DARK_FIELD = new Color(20, 20, 20);
    private static final Color DARK_BUTTON = new Color(25, 25, 25);
    private static final Color DARK_PRIMARY_BUTTON = new Color(24, 62, 108);
    private static final Color DARK_SUCCESS_BUTTON = new Color(25, 75, 48);

    private final JComboBox<?> cmbA;
    private final JTextField txtB;
    private final JTextArea txtEntrada;
    private final JTextArea txtResultado;
    private final JTextArea txtHistorial;
    private final JButton btnCifrar;
    private final JButton btnDescifrar;
    private final JButton btnUsarResultado;
    private final JButton btnLimpiar;
    private final JButton btnLimpiarHistorial;
    private final JButton btnThemeToggle;
    private JButton btnMaximize;
    private final JButton btnCargarArchivo;
    private final JButton btnExportarArchivo;
    private final JLabel lblEstado;
    private final JLabel lblContador;
    private final JLabel lblTitulo;
    private final List<JLabel> mutedLabels = new ArrayList<>();
    private final JPanel mainPanel;
    private final JPanel titleBar;
    private boolean darkMode;
    private boolean statusError;
    private boolean maximized;
    private Point dragOffset;
    private java.awt.Rectangle restoreBounds;
    private Timer themeTimer;

    public CifradoView() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(940, 680));
        setSize(1160, 790);
        setLocationRelativeTo(null);
        if (supportsRoundedWindow()) {
            setBackground(new Color(0, 0, 0, 0));
        } else {
            setBackground(UIManager.getColor("Panel.background"));
        }

        mainPanel = new RoundedPanel(new BorderLayout(0, 18));
        mainPanel.setBorder(new EmptyBorder(15, 20, 16, 20));
        setContentPane(mainPanel);

        titleBar = createHeader();
        mainPanel.add(titleBar, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(18, 0));
        content.setOpaque(false);
        content.add(createWorkspace(), BorderLayout.CENTER);
        content.add(createHistory(), BorderLayout.EAST);
        mainPanel.add(content, BorderLayout.CENTER);

        JPanel footer = createFooter();
        mainPanel.add(footer, BorderLayout.SOUTH);

        cmbA = (JComboBox<?>) findControl("keySelector");
        txtB = findKeyField();
        txtEntrada = findTextArea("input");
        txtResultado = findTextArea("output");
        txtHistorial = findTextArea("history");
        btnThemeToggle = (JButton) findControl("theme");
        btnCargarArchivo = (JButton) findControl("load");
        btnExportarArchivo = (JButton) findControl("export");
        btnCifrar = (JButton) findControl("encrypt");
        btnDescifrar = (JButton) findControl("decrypt");
        btnUsarResultado = (JButton) findControl("reuse");
        btnLimpiar = (JButton) findControl("clear");
        btnLimpiarHistorial = (JButton) findControl("clearHistory");
        lblEstado = (JLabel) findControl("status");
        lblContador = (JLabel) findControl("counter");
        lblTitulo = (JLabel) findControl("title");

        applyAppPalette();
        refreshTitleColor();
        setupWindowControls();
        setupTextCounter();
        applyWindowShape();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 2, 4, 2));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 2));
        heading.setOpaque(false);
        JLabel title = new JLabel("Cifrado Afín");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.putClientProperty("app.control", "title");
        JLabel subtitle = new JLabel("Laboratorio de seguridad clásica");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleMuted(subtitle);
        heading.add(title);
        heading.add(subtitle);
        brand.add(heading, BorderLayout.CENTER);
        header.add(brand, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 3));
        controls.setOpaque(false);
        JButton theme = softButton("Modo oscuro");
        theme.setIcon(new AppIcon("moon", 16, 16, PRIMARY));
        theme.setIconTextGap(8);
        theme.putClientProperty("app.control", "theme");
        controls.add(theme);
        JButton minimize = windowButton("minimize", "Minimizar");
        minimize.addActionListener(e -> setState(JFrame.ICONIFIED));
        controls.add(minimize);
        btnMaximize = windowButton("maximize", "Maximizar");
        btnMaximize.putClientProperty("app.control", "maximize");
        btnMaximize.addActionListener(e -> toggleMaximize());
        controls.add(btnMaximize);
        JButton close = windowButton("close", "Cerrar");
        close.setForeground(ERROR);
        close.addActionListener(e -> dispose());
        controls.add(close);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(0, 14));
        workspace.setOpaque(false);
        workspace.add(createKeysCard(), BorderLayout.NORTH);

        JPanel editors = new JPanel(new GridLayout(2, 1, 0, 14));
        editors.setOpaque(false);
        editors.add(createEditorCard(true));
        editors.add(createEditorCard(false));
        workspace.add(editors, BorderLayout.CENTER);
        return workspace;
    }

    private JPanel createKeysCard() {
        JPanel card = new CardPanel(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(14, 17, 14, 17));

        JPanel description = new JPanel(new GridLayout(2, 1, 0, 3));
        description.setOpaque(false);
        JLabel title = new JLabel("Claves del algoritmo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel hint = new JLabel("Elige un valor coprimo para a y un desplazamiento b.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        styleMuted(hint);
        description.add(title);
        description.add(hint);
        card.add(description, BorderLayout.WEST);

        JPanel fields = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        fields.setOpaque(false);
        JLabel aLabel = new JLabel("CLAVE A");
        aLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        styleMuted(aLabel);
        JComboBox<Integer> a = new JComboBox<>(CifradoAfinStrategy.getValoresAValidos());
        a.setSelectedItem(5);
        a.setPreferredSize(new Dimension(100, 36));
        a.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        a.putClientProperty("app.control", "keySelector");
        fields.add(aLabel);
        fields.add(a);

        JLabel bLabel = new JLabel("CLAVE B");
        bLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        styleMuted(bLabel);
        JTextField b = new JTextField("8", 5);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.putClientProperty("app.control", "keyField");
        b.setToolTipText("Número entero entre 0 y 41");
        fields.add(bLabel);
        fields.add(b);
        card.add(fields, BorderLayout.EAST);
        return card;
    }

    private JPanel createEditorCard(boolean input) {
        JPanel card = new CardPanel(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(15, 17, 13, 17));

        JPanel titleBar = new JPanel(new BorderLayout(8, 0));
        titleBar.setOpaque(false);
        JPanel titleGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleGroup.setOpaque(false);
        JLabel title = new JLabel(input ? "Texto de entrada" : "Resultado");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleGroup.add(title);
        titleBar.add(titleGroup, BorderLayout.WEST);

        if (input) {
            JButton load = softButton("Abrir archivo");
            load.putClientProperty("app.control", "load");
            titleBar.add(load, BorderLayout.EAST);
        } else {
            JButton export = softButton("Guardar resultado");
            export.putClientProperty("app.control", "export");
            titleBar.add(export, BorderLayout.EAST);
        }
        card.add(titleBar, BorderLayout.NORTH);

        JTextArea text = new JTextArea();
        text.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setMargin(new java.awt.Insets(8, 9, 8, 9));
        text.putClientProperty("app.control", input ? "input" : "output");
        text.setToolTipText(input ? "Escribe o pega el texto que deseas procesar" : "Aquí aparecerá el texto procesado");
        if (!input) {
            text.setEditable(false);
            text.setText("");
        } else {
            text.setText("HOLA MUNDO ÁÉÍÓÚ Ñ 12345;");
        }
        JScrollPane scroll = new JScrollPane(text);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        card.add(scroll, BorderLayout.CENTER);

        JLabel counter = new JLabel(input ? text.getText().length() + " caracteres" : "Listo para procesar");
        counter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        styleMuted(counter);
        counter.putClientProperty("app.control", input ? "counter" : "outputHint");
        card.add(counter, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createHistory() {
        JPanel card = new CardPanel(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 16, 14, 16));
        card.setPreferredSize(new Dimension(290, 0));

        JPanel heading = new JPanel(new BorderLayout(0, 5));
        heading.setOpaque(false);
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Actividad reciente");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel subtitle = new JLabel("Las operaciones aparecen aquí");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        styleMuted(subtitle);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        heading.add(titlePanel, BorderLayout.CENTER);

        JButton clearHistory = softButton("Borrar");
        clearHistory.setIcon(new AppIcon("trash", 14, 14, TEXT_SECONDARY));
        clearHistory.setIconTextGap(6);
        clearHistory.putClientProperty("app.control", "clearHistory");
        heading.add(clearHistory, BorderLayout.EAST);
        card.add(heading, BorderLayout.NORTH);

        JTextArea history = new JTextArea();
        history.setEditable(false);
        history.setLineWrap(true);
        history.setWrapStyleWord(true);
        history.setFont(new Font("Consolas", Font.PLAIN, 12));
        history.setMargin(new java.awt.Insets(9, 8, 9, 8));
        history.putClientProperty("app.control", "history");
        JScrollPane scroll = new JScrollPane(history);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        card.add(scroll, BorderLayout.CENTER);

        JLabel hint = new JLabel("El historial se conserva mientras la app está abierta.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        styleMuted(hint);
        card.add(hint, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 10));
        footer.setOpaque(false);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        buttons.setOpaque(false);

        JButton encrypt = actionButton("Cifrar", PRIMARY);
        encrypt.putClientProperty("app.control", "encrypt");
        JButton decrypt = actionButton("Descifrar", SUCCESS);
        decrypt.putClientProperty("app.control", "decrypt");
        JButton reuse = softButton("Usar resultado como entrada");
        reuse.putClientProperty("app.control", "reuse");
        JButton clear = softButton("Limpiar");
        clear.setIcon(new AppIcon("trash", 15, 15, TEXT_SECONDARY));
        clear.setIconTextGap(7);
        clear.putClientProperty("app.control", "clear");
        buttons.add(encrypt);
        buttons.add(decrypt);
        buttons.add(reuse);
        buttons.add(clear);
        footer.add(buttons, BorderLayout.NORTH);

        JLabel status = new JLabel("Sistema listo. Ingresa un texto para comenzar.");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        status.setForeground(SUCCESS);
        status.setBorder(new EmptyBorder(3, 2, 0, 0));
        status.putClientProperty("app.control", "status");
        footer.add(status, BorderLayout.SOUTH);
        return footer;
    }

    private JTextField findKeyField() {
        return (JTextField) findControl("keyField");
    }

    private JTextArea findTextArea(String key) {
        return (JTextArea) findControl(key);
    }

    private java.awt.Component findControl(String key) {
        return findControl(mainPanel, key);
    }

    private java.awt.Component findControl(java.awt.Container parent, String key) {
        for (java.awt.Component child : parent.getComponents()) {
            if (child instanceof javax.swing.JComponent component
                    && key.equals(component.getClientProperty("app.control"))) {
                return child;
            }
            if (child instanceof java.awt.Container container) {
                java.awt.Component found = findControl(container, key);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private JButton actionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    private JButton softButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(7, 11, 7, 11));
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    private JButton windowButton(String iconName, String description) {
        JButton button = new JButton(new AppIcon(iconName, 15, 15, TEXT_SECONDARY));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 9, 8, 9));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setToolTipText(description);
        button.getAccessibleContext().setAccessibleName(description);
        return button;
    }

    private void styleMuted(JLabel label) {
        mutedLabels.add(label);
        label.setForeground(mutedTextColor());
    }

    private Color mutedTextColor() {
        return darkMode ? new Color(195, 195, 195) : TEXT_SECONDARY;
    }

    private void setupWindowControls() {
        MouseAdapter dragHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                Point screen = event.getLocationOnScreen();
                dragOffset = new Point(screen.x - getX(), screen.y - getY());
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                if (!maximized && dragOffset != null) {
                    Point screen = event.getLocationOnScreen();
                    setLocation(screen.x - dragOffset.x, screen.y - dragOffset.y);
                }
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    toggleMaximize();
                }
            }
        };
        titleBar.addMouseListener(dragHandler);
        titleBar.addMouseMotionListener(dragHandler);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent event) {
                applyWindowShape();
            }
        });
    }

    private void toggleMaximize() {
        if (maximized) {
            if (restoreBounds != null) {
                setBounds(restoreBounds);
            }
            maximized = false;
        } else {
            restoreBounds = getBounds();
            setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getMaximumWindowBounds());
            maximized = true;
        }
        btnMaximize.setIcon(new AppIcon(maximized ? "restore" : "maximize", 15, 15,
                darkMode ? new Color(190, 190, 190) : TEXT_SECONDARY));
        btnMaximize.setToolTipText(maximized ? "Restaurar ventana" : "Maximizar ventana");
        btnMaximize.getAccessibleContext().setAccessibleName(
                maximized ? "Restaurar ventana" : "Maximizar ventana");
        refreshIconColors();
        applyWindowShape();
    }

    private void applyWindowShape() {
        if (isDisplayable() && supportsRoundedWindow() && !maximized) {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 24, 24));
        } else if (isDisplayable() && supportsRoundedWindow()) {
            setShape(null);
        }
    }

    private boolean supportsRoundedWindow() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT);
    }

    private void setupTextCounter() {
        txtEntrada.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                lblContador.setText(txtEntrada.getText().length() + " caracteres");
            }

            @Override
            public void insertUpdate(DocumentEvent event) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                update();
            }
        });
    }

    public void alternarTema() {
        boolean nextDarkMode = !darkMode;
        Color oldBackground = darkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
        try {
            if (nextDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (javax.swing.UnsupportedLookAndFeelException exception) {
            mostrarMensajeError("No fue posible cambiar el tema: " + exception.getMessage());
            return;
        }

        darkMode = nextDarkMode;
        applyAppPalette();
        SwingUtilities.updateComponentTreeUI(this);
        applyComponentPalette(mainPanel);
        btnThemeToggle.setText(darkMode ? "Modo claro" : "Modo oscuro");
        btnThemeToggle.setIcon(new AppIcon(darkMode ? "sun" : "moon", 16, 16,
                darkMode ? new Color(255, 202, 66) : PRIMARY));
        btnThemeToggle.getAccessibleContext().setAccessibleName(
                darkMode ? "Cambiar a modo claro" : "Cambiar a modo oscuro");
        mutedLabels.forEach(label -> label.setForeground(mutedTextColor()));
        refreshStatusColor();
        refreshActionColors();
        refreshIconColors();
        refreshTitleColor();
        revalidate();
        repaint();
        animateThemeTransition(oldBackground);
    }

    private void animateThemeTransition(Color curtainColor) {
        if (themeTimer != null) {
            themeTimer.stop();
        }
        final long startTime = System.nanoTime();
        final int duration = 720;
        ThemeRevealOverlay overlay = new ThemeRevealOverlay(curtainColor);
        setGlassPane(overlay);
        overlay.setVisible(true);
        Point revealCenter = SwingUtilities.convertPoint(btnThemeToggle,
                new Point(btnThemeToggle.getWidth() / 2, btnThemeToggle.getHeight() / 2), overlay);
        overlay.setRevealCenter(revealCenter);
        double farthestX = Math.max(revealCenter.x, overlay.getWidth() - revealCenter.x);
        double farthestY = Math.max(revealCenter.y, overlay.getHeight() - revealCenter.y);
        final double maxRadius = Math.hypot(farthestX, farthestY) + 2;
        themeTimer = new Timer(16, event -> {
            double rawProgress = Math.min(1d,
                    (System.nanoTime() - startTime) / (duration * 1_000_000d));
            double easedProgress = rawProgress * rawProgress * rawProgress
                    * (rawProgress * (rawProgress * 6d - 15d) + 10d);
            overlay.setRevealRadius(maxRadius * easedProgress);
            overlay.repaint();
            if (rawProgress >= 1d) {
                themeTimer.stop();
                overlay.setVisible(false);
            }
        });
        themeTimer.start();
    }

    public static void configurarTemaInicial() {
        applyUiPalette(false);
    }

    private void applyAppPalette() {
        applyUiPalette(darkMode);
    }

    private void refreshStatusColor() {
        lblEstado.setForeground(statusError
                ? (darkMode ? new Color(255, 130, 145) : ERROR)
                : (darkMode ? new Color(111, 210, 157) : SUCCESS));
    }

    private void refreshTitleColor() {
        lblTitulo.setForeground(darkMode ? new Color(211, 112, 122) : TEXT_PRIMARY);
    }

    private void refreshActionColors() {
        JButton encrypt = (JButton) findControl("encrypt");
        JButton decrypt = (JButton) findControl("decrypt");
        encrypt.setBackground(darkMode ? DARK_PRIMARY_BUTTON : PRIMARY);
        decrypt.setBackground(darkMode ? DARK_SUCCESS_BUTTON : SUCCESS);
        encrypt.setForeground(Color.WHITE);
        decrypt.setForeground(Color.WHITE);
    }

    private void refreshIconColors() {
        refreshIconColors(mainPanel);
    }

    private void refreshIconColors(java.awt.Container parent) {
        for (java.awt.Component child : parent.getComponents()) {
            if (child instanceof JButton button && button.getIcon() instanceof AppIcon icon) {
                if (icon.type.equals("sun")) {
                    icon.setColor(new Color(255, 202, 66));
                } else if (icon.type.equals("moon")) {
                    icon.setColor(PRIMARY);
                } else if (icon.type.equals("close")) {
                    icon.setColor(darkMode ? new Color(255, 120, 130) : ERROR);
                } else {
                    icon.setColor(darkMode ? new Color(190, 190, 190) : TEXT_SECONDARY);
                }
                button.repaint();
            }
            if (child instanceof java.awt.Container container) {
                refreshIconColors(container);
            }
        }
    }

    private void applyComponentPalette(java.awt.Container parent) {
        Color foreground = darkMode ? new Color(245, 245, 245) : TEXT_PRIMARY;
        Color fieldBackground = darkMode ? DARK_FIELD : LIGHT_PANEL;
        Color buttonBackground = darkMode ? DARK_BUTTON : new Color(242, 243, 245);

        for (java.awt.Component child : parent.getComponents()) {
            if (child instanceof javax.swing.text.JTextComponent textComponent) {
                textComponent.setBackground(fieldBackground);
                textComponent.setForeground(foreground);
                textComponent.setCaretColor(foreground);
                textComponent.setSelectedTextColor(foreground);
                textComponent.setSelectionColor(darkMode
                        ? new Color(45, 80, 120) : new Color(211, 229, 248));
            } else if (child instanceof JComboBox<?> comboBox) {
                comboBox.setBackground(fieldBackground);
                comboBox.setForeground(foreground);
                comboBox.setBorder(BorderFactory.createLineBorder(
                        darkMode ? new Color(65, 65, 65) : LIGHT_BORDER));
                if (comboBox.isEditable() && comboBox.getEditor().getEditorComponent() instanceof javax.swing.text.JTextComponent editor) {
                    editor.setBackground(fieldBackground);
                    editor.setForeground(foreground);
                    editor.setCaretColor(foreground);
                }
            } else if (child instanceof JButton button) {
                String control = (String) button.getClientProperty("app.control");
                if ("encrypt".equals(control)) {
                    button.setBackground(darkMode ? DARK_PRIMARY_BUTTON : PRIMARY);
                    button.setForeground(Color.WHITE);
                } else if ("decrypt".equals(control)) {
                    button.setBackground(darkMode ? DARK_SUCCESS_BUTTON : SUCCESS);
                    button.setForeground(Color.WHITE);
                } else {
                    button.setBackground(buttonBackground);
                    button.setForeground(darkMode ? new Color(235, 235, 235) : TEXT_SECONDARY);
                }
                button.setOpaque(true);
            } else if (child instanceof JScrollPane scrollPane) {
                scrollPane.setBackground(darkMode ? DARK_PANEL : LIGHT_PANEL);
                scrollPane.getViewport().setBackground(fieldBackground);
            }

            if (child instanceof java.awt.Container container) {
                applyComponentPalette(container);
            }
        }
    }

    private static void applyUiPalette(boolean dark) {
        Color background = dark ? DARK_BACKGROUND : LIGHT_BACKGROUND;
        Color panel = dark ? DARK_PANEL : LIGHT_PANEL;
        Color field = dark ? DARK_FIELD : panel;
        Color button = dark ? DARK_BUTTON : new Color(242, 243, 245);
        Color border = dark ? DARK_BORDER : LIGHT_BORDER;
        Color primary = dark ? DARK_PRIMARY_BUTTON : PRIMARY;
        Color primarySoft = dark ? new Color(27, 55, 88) : new Color(211, 229, 248);
        Color primarySubtle = dark ? new Color(19, 34, 49) : new Color(244, 249, 254);
        Color warning = dark ? new Color(190, 141, 0) : WARNING;
        Color error = dark ? new Color(180, 39, 60) : ERROR;
        Color success = dark ? new Color(39, 137, 82) : SUCCESS;
        Color jeans = dark ? new Color(76, 111, 132) : JEANS;
        Color foreground = dark ? new Color(239, 239, 239) : TEXT_PRIMARY;
        UIManager.put("App.cardBackground", panel);
        UIManager.put("App.cardBorder", border);
        UIManager.put("App.primary", primary);
        UIManager.put("App.primarySoft", primarySoft);
        UIManager.put("App.primarySubtle", primarySubtle);
        UIManager.put("App.warning", warning);
        UIManager.put("App.error", error);
        UIManager.put("App.success", success);
        UIManager.put("App.jeans", jeans);
        UIManager.put("Panel.background", background);
        UIManager.put("Label.foreground", foreground);
        UIManager.put("TextField.background", field);
        UIManager.put("TextField.foreground", foreground);
        UIManager.put("TextField.caretForeground", foreground);
        UIManager.put("TextField.inactiveForeground", dark ? new Color(205, 205, 205) : TEXT_SECONDARY);
        UIManager.put("TextArea.background", field);
        UIManager.put("TextArea.foreground", foreground);
        UIManager.put("TextArea.caretForeground", foreground);
        UIManager.put("TextArea.inactiveForeground", dark ? new Color(205, 205, 205) : TEXT_SECONDARY);
        UIManager.put("TextComponent.foreground", foreground);
        UIManager.put("ComboBox.background", field);
        UIManager.put("ComboBox.foreground", foreground);
        UIManager.put("ComboBox.buttonBackground", dark ? DARK_BUTTON : new Color(242, 243, 245));
        UIManager.put("ComboBox.buttonArrowColor", dark ? new Color(220, 220, 220) : TEXT_SECONDARY);
        UIManager.put("Button.background", button);
        UIManager.put("Button.foreground", foreground);
        UIManager.put("Component.borderColor", border);
        UIManager.put("Component.focusColor", primary);
        UIManager.put("Component.focusedBorderColor", primary);
        UIManager.put("TextComponent.selectionBackground", primarySoft);
        UIManager.put("TextComponent.selectionForeground", foreground);
        UIManager.put("ScrollBar.thumb", dark ? new Color(76, 76, 76) : new Color(196, 198, 201));
        UIManager.put("ScrollBar.track", background);
        UIManager.put("ToolTip.background", panel);
        UIManager.put("ToolTip.foreground", foreground);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(border));
        UIManager.put("Button.arc", 16);
        UIManager.put("Component.arc", 13);
        UIManager.put("TextComponent.arc", 12);
    }

    public int getValorA() {
        return ((Number) cmbA.getSelectedItem()).intValue();
    }

    public String getTextoB() {
        return txtB.getText().trim();
    }

    public String getTextoEntrada() {
        return txtEntrada.getText();
    }

    public String getTextoResultado() {
        return txtResultado.getText();
    }

    public void setTextoResultado(String result) {
        txtResultado.setText(result);
    }

    public void setTextoEntrada(String text) {
        txtEntrada.setText(text);
        txtEntrada.requestFocusInWindow();
    }

    public void setHistorial(String history) {
        txtHistorial.setText(history);
        txtHistorial.setCaretPosition(0);
    }

    public void setEstado(String status) {
        statusError = false;
        refreshStatusColor();
        lblEstado.setText(status);
    }

    public void mostrarMensajeError(String message) {
        statusError = true;
        refreshStatusColor();
        lblEstado.setText(message);
        txtEntrada.requestFocusInWindow();
    }

    public void addCifrarListener(ActionListener listener) {
        btnCifrar.addActionListener(listener);
    }

    public void addDescifrarListener(ActionListener listener) {
        btnDescifrar.addActionListener(listener);
    }

    public void addUsarResultadoListener(ActionListener listener) {
        btnUsarResultado.addActionListener(listener);
    }

    public void addLimpiarListener(ActionListener listener) {
        btnLimpiar.addActionListener(listener);
    }

    public void addLimpiarHistorialListener(ActionListener listener) {
        btnLimpiarHistorial.addActionListener(listener);
    }

    public void addThemeToggleListener(ActionListener listener) {
        btnThemeToggle.addActionListener(listener);
    }

    public void addCargarArchivoListener(ActionListener listener) {
        btnCargarArchivo.addActionListener(listener);
    }

    public void addExportarArchivoListener(ActionListener listener) {
        btnExportarArchivo.addActionListener(listener);
    }

    public JFrame getFrame() {
        return this;
    }

    private static class RoundedPanel extends JPanel {

        RoundedPanel(java.awt.LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color background = UIManager.getColor("Panel.background");
            g.setColor(background == null ? LIGHT_BACKGROUND : background);
            g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 24, 24));
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class CardPanel extends JPanel {

        CardPanel(java.awt.LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color background = UIManager.getColor("App.cardBackground");
            if (background == null) {
                background = UIManager.getColor("Panel.background");
            }
            g.setColor(background == null ? Color.WHITE : background);
            g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            g.setColor(UIManager.getColor("App.cardBorder") == null
                    ? new Color(220, 224, 232) : UIManager.getColor("App.cardBorder"));
            g.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 18, 18));
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class AppIcon implements Icon {

        private final String type;
        private final int width;
        private final int height;
        private Color color;

        AppIcon(String type, int width, int height, Color color) {
            this.type = type;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        void setColor(Color color) {
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        @Override
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(x, y);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            double cx = width / 2d;
            double cy = height / 2d;
            switch (type) {
                case "sun" ->
                    paintSun(g, cx, cy);
                case "moon" ->
                    paintMoon(g);
                case "minimize" ->
                    g.draw(new Line2D.Double(3, height - 4, width - 3, height - 4));
                case "maximize" ->
                    g.draw(new RoundRectangle2D.Double(3, 3, width - 6, height - 6, 2, 2));
                case "restore" -> {
                    g.draw(new RoundRectangle2D.Double(5, 3, width - 7, height - 7, 2, 2));
                    g.draw(new RoundRectangle2D.Double(3, 5, width - 7, height - 7, 2, 2));
                }
                case "close" -> {
                    g.draw(new Line2D.Double(4, 4, width - 4, height - 4));
                    g.draw(new Line2D.Double(width - 4, 4, 4, height - 4));
                }
                case "trash" ->
                    paintTrash(g);
                default -> {
                }
            }
            g.dispose();
        }

        private void paintSun(Graphics2D g, double cx, double cy) {
            g.draw(new Ellipse2D.Double(cx - 3.2, cy - 3.2, 6.4, 6.4));
            for (int angle = 0; angle < 360; angle += 45) {
                double radians = Math.toRadians(angle);
                double x1 = cx + Math.cos(radians) * 5;
                double y1 = cy + Math.sin(radians) * 5;
                double x2 = cx + Math.cos(radians) * 7;
                double y2 = cy + Math.sin(radians) * 7;
                g.draw(new Line2D.Double(x1, y1, x2, y2));
            }
        }

        private void paintMoon(Graphics2D g) {
            Area crescent = new Area(new Ellipse2D.Double(2, 2, width - 4, height - 4));
            crescent.subtract(new Area(new Ellipse2D.Double(7, 0, width - 6, height - 6)));
            g.fill(crescent);
        }

        private void paintTrash(Graphics2D g) {
            double left = 4;
            double right = width - 4;
            g.draw(new Line2D.Double(left + 1, 5, right - 1, 5));
            g.draw(new Line2D.Double(width / 2d - 2, 3, width / 2d + 2, 3));
            g.draw(new Line2D.Double(left + 2, 6, left + 3, height - 3));
            g.draw(new Line2D.Double(right - 2, 6, right - 3, height - 3));
            g.draw(new Line2D.Double(left + 3, height - 3, right - 3, height - 3));
            g.draw(new Line2D.Double(width / 2d - 2, 8, width / 2d - 2, height - 5));
            g.draw(new Line2D.Double(width / 2d + 2, 8, width / 2d + 2, height - 5));
        }
    }

    private static class ThemeRevealOverlay extends JPanel {

        private final Color curtainColor;
        private Point revealCenter = new Point();
        private double revealRadius;

        ThemeRevealOverlay(Color curtainColor) {
            this.curtainColor = curtainColor;
            setOpaque(false);
        }

        void setRevealCenter(Point revealCenter) {
            this.revealCenter = revealCenter;
        }

        void setRevealRadius(double revealRadius) {
            this.revealRadius = revealRadius;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Area curtain = new Area(new java.awt.Rectangle(0, 0, getWidth(), getHeight()));
            Area reveal = new Area(new Ellipse2D.Double(
                    revealCenter.x - revealRadius,
                    revealCenter.y - revealRadius,
                    revealRadius * 2,
                    revealRadius * 2));
            curtain.subtract(reveal);
            g.setColor(curtainColor);
            g.fill(curtain);
            g.dispose();
        }
    }
}
