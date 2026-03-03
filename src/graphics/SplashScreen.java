package graphics;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import engine.MainEngine;

public class SplashScreen extends JPanel {
    private Branding branding;
    private MainEngine mainEngine;
    private JPanel parentContainer;
    
    private GridBagConstraints gbc;
    private JButton startBtn;

    public SplashScreen(MainEngine mainEngine, Branding branding, JPanel parentContainer) {
        this.branding = branding;
        this.mainEngine = mainEngine;
        this.parentContainer = parentContainer;

        setLayout(new GridBagLayout());
        setBackground(branding.dark);
        gbc = new GridBagConstraints();

        initializeMainPanel();
    }
    
    public void initializeMainPanel() {
        JLabel simulatorLabel = new JLabel("QuantumQueue");
        simulatorLabel.setFont(branding.jetBrainsBGiant);
        simulatorLabel.setForeground(branding.light);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        add(simulatorLabel, gbc);

        startBtn = createButton("Simulate");
        startBtn.addActionListener(e -> {
            CardLayout cl = (CardLayout) parentContainer.getLayout();
            cl.show(parentContainer, "MainMenu");
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(startBtn, gbc);
    }

    public JButton createButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(branding.light);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(branding.jetBrainsBLarge);
        btn.setForeground(branding.light);
        btn.setBackground(branding.dark);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(250, 70));
        btn.setMaximumSize(new Dimension(250, 70));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(branding.darkGray);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(branding.dark);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(branding.darkGray);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(branding.darkGray);
            }
        });

        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (branding.backgroundDark != null) {
            g.drawImage(branding.backgroundDark, 0, 0, getWidth(), getHeight(), this);
        }
    }
}