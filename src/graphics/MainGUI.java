package graphics;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import engine.MainEngine;

public class MainGUI {
    public Branding branding;
    private MainEngine engine;
    private JFrame mainFrame;
    private JPanel mainPanel;

    private SplashScreen splashScreen;
    private MainMenu mainMenu;
    private MainMenuHowToUse mainMenuHowToUse;
    private MainMenuSettings mainMenuSettings;
    private SimulatorMain simulatorMain;
    private SimulatorOutput simulatorOutput;

    public MainGUI(MainEngine engine) {
        this.engine = engine;
        this.branding = new Branding();

        initializeMainFrame();
        initializeMainPanel();

        mainFrame.setVisible(true);
    }

    public void initializeMainFrame() {
        mainFrame = new JFrame();
        mainFrame.setSize(new Dimension(1240,720));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("QuantumQueue");
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
    }

    public void initializeMainPanel(){
        mainPanel = new JPanel();
        mainPanel.setBackground(branding.dark);
        mainPanel.setLayout(new CardLayout());
        
        splashScreen = new SplashScreen(engine, branding, mainPanel);
        mainMenu = new MainMenu(engine, branding, mainPanel);
        mainMenuHowToUse = new MainMenuHowToUse(engine, branding, mainPanel);
        mainMenuSettings = new MainMenuSettings(engine, branding, mainPanel);
        mainMenuSettings.setThemeChangeListener(this::applyTheme);
        simulatorMain = new SimulatorMain(engine, branding, mainPanel);
        simulatorOutput = new SimulatorOutput(engine, branding, mainPanel);
        
        mainPanel.add(splashScreen, "SplashScreen");
        mainPanel.add(mainMenu, "MainMenu");
        mainPanel.add(mainMenuHowToUse, "HowToUse");
        mainPanel.add(mainMenuSettings, "Settings");
        mainPanel.add(simulatorMain, "SimulatorMain");
        mainPanel.add(simulatorOutput, "SimulatorOutput");
        mainFrame.add(mainPanel);
    }


    // ==================================================
    //                   THEME
    // ==================================================
    public void applyTheme() {
        applyThemeRecursive(mainPanel);
        simulatorMain.refreshStyles();
        simulatorOutput.refreshStyles();
        mainMenu.refreshIcons();
        mainPanel.repaint();
    }

    private void applyThemeRecursive(Container parent) {
        for (Component c : parent.getComponents()) {
            Color bg = c.getBackground();
            if (bg != null) {
                if (bg.equals(branding.dark) || bg.equals(branding.light)) {
                    c.setBackground(branding.dark);
                }
            }
            
            Color fg = c.getForeground();
            if (fg != null && (fg.equals(branding.dark) || fg.equals(branding.light))) {
                c.setForeground(branding.light);
            }
            if (c instanceof Container) {
                applyThemeRecursive((Container) c);
            }
        }
    }

    // ==================================================
    //                GETTERS AND SETTERS
    // ==================================================

    public SimulatorMain getSimulatorMain() {
        return simulatorMain;
    }
    
    public SimulatorOutput getSimulatorOutput() {
        return simulatorOutput;
    }
}