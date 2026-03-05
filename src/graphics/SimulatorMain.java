package graphics;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import engine.MainEngine;
import model.Process;

public class SimulatorMain extends JPanel {
    private Branding branding;
    private MainEngine mainEngine;
    private JPanel parentContainer;

    private JPanel mainPanel;
    private JPanel leftPanel, rightPanel;
    
    private JLabel algorithmLabel, quantumTimeLabel;
    private JComboBox<String> algorithmComboBox;
    private JTextField quantumTimeField;
    private JButton simulateBtn;
    private JButton addProcessBtn, removeProcessBtn, importTextFileBtn, randomProcessesBtn;
    
    private JPanel processTablePanel;
    private JLabel priorityNoHeader;

    private ArrayList<Process> processes = new ArrayList<>();
    private Set<JPanel> selectedRows = new HashSet<>();
    private Set<String> usedProcessIds = new HashSet<>();
    private Set<Color> usedColors = new HashSet<>();

    public SimulatorMain(MainEngine mainEngine, Branding branding, JPanel parentContainer) {
        this.branding = branding;
        this.mainEngine = mainEngine;
        this.parentContainer = parentContainer;

        setLayout(new BorderLayout());
        setBackground(branding.dark);

        initializeMainPanel();
        initializePanels();

        updateAlgorithmFields();
    }

    public void initializeMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(branding.dark);

        int mw = 20, mh = 20;

        JPanel north = blankPanel(branding.dark, 0, mh);
        JPanel south = blankPanel(branding.dark, 0, mh);
        JPanel west = blankPanel(branding.dark, mw, 0);
        JPanel east = blankPanel(branding.dark, mw, 0);

        wrapper.add(north, BorderLayout.NORTH);
        wrapper.add(south, BorderLayout.SOUTH);
        wrapper.add(west, BorderLayout.WEST);
        wrapper.add(east, BorderLayout.EAST);

        mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBackground(branding.dark);
        wrapper.add(mainPanel, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }
    
    public void initializePanels() {
        initializeLeftPanel();
        initializeRightPanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
    }
    
    private void initializeLeftPanel() {
        leftPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(branding.dark);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(branding.light);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);
                g2.dispose();
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(350, 0));

        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);
        leftContent.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel algorithmPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(branding.dark);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(branding.light);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        algorithmPanel.setOpaque(false);
        algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.Y_AXIS));
        algorithmPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        algorithmPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        algorithmPanel.setPreferredSize(new Dimension(getWidth(), 300));
        algorithmPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        algorithmLabel = new JLabel("Algorithm");
        algorithmLabel.setFont(branding.jetBrainsBMedium);
        algorithmLabel.setForeground(branding.light);
        algorithmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] algorithms = {
            "FCFS (First Come First Serve)",
            "SJF (Shortest Job First)",
            "SRTF (Shortest Remaining Time First)",
            "Round Robin",
            "Priority (Preemptive)",
            "Priority (Non-Preemptive)"
        };
        algorithmComboBox = new JComboBox<>(algorithms);
        styleComboBox(algorithmComboBox);
        algorithmComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        algorithmComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        algorithmComboBox.addActionListener(e -> updateAlgorithmFields());

        quantumTimeLabel = new JLabel("Quantum Time");
        quantumTimeLabel.setFont(branding.jetBrainsBSmall);
        quantumTimeLabel.setForeground(branding.darkGray);
        quantumTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        quantumTimeField = new JTextField();
        styleTextField(quantumTimeField);
        quantumTimeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        quantumTimeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        simulateBtn = createOtherBtn("Simulate");
        JPanel simulateWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        simulateWrapper.setOpaque(false);
        simulateWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        simulateWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        simulateWrapper.add(simulateBtn);
        simulateBtn.addActionListener(e -> {
            if (!validateProcessTable()) return;
            
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            int quantum = 0;

            if (algorithm.contains("Round Robin")) {
                quantum = Integer.parseInt(quantumTimeField.getText().trim());
            }

            ArrayList<Process> processes = mainEngine.getGUI().getSimulatorMain().getProcesses();
            mainEngine.runSimulation(processes, algorithm, quantum);

            CardLayout cl = (CardLayout) parentContainer.getLayout();
            cl.show(parentContainer, "SimulatorOutput");

            mainEngine.getGUI().getSimulatorOutput().loadSimulationResults();
        });

        algorithmPanel.add(algorithmLabel);
        algorithmPanel.add(Box.createVerticalStrut(8));
        algorithmPanel.add(algorithmComboBox);
        algorithmPanel.add(Box.createVerticalStrut(14));
        algorithmPanel.add(quantumTimeLabel);
        algorithmPanel.add(Box.createVerticalStrut(6));
        algorithmPanel.add(quantumTimeField);
        algorithmPanel.add(Box.createVerticalStrut(50));
        algorithmPanel.add(simulateWrapper);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addProcessBtn = createProcessButton(branding.lightIcoAddProcess, "Add Process");
        removeProcessBtn = createProcessButton(branding.lightIcoRemoveProcess, "Remove Process");
        importTextFileBtn = createProcessButton(branding.lightIcoImportProcess, "Import Text File");
        randomProcessesBtn = createProcessButton(branding.lightIcoRandomProcess, "Random Processes");

        addProcessBtn.addActionListener(e -> addProcess());
        removeProcessBtn.addActionListener(e -> removeProcess());
        randomProcessesBtn.addActionListener(e -> randomProcesses());
        importTextFileBtn.addActionListener(e -> importFromFile());

        buttonsPanel.add(addProcessBtn);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(removeProcessBtn);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(importTextFileBtn);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(randomProcessesBtn);
        buttonsPanel.add(Box.createVerticalGlue());
        
        leftContent.add(algorithmPanel);
        leftContent.add(Box.createVerticalStrut(20));
        leftContent.add(buttonsPanel);
        leftContent.add(Box.createVerticalGlue());

        leftPanel.add(leftContent, BorderLayout.CENTER);
    }
    
    public void initializeRightPanel() {
        rightPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(branding.dark);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(branding.light);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                g2.dispose();
            }
        };
        rightPanel.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel headerRow = new JPanel(new GridLayout(1, 4, 0, 0));
        headerRow.setOpaque(false);
        headerRow.setPreferredSize(new Dimension(0, 60));
        headerRow.setBorder(new EmptyBorder(10, 20, 10, 20));

        headerRow.add(createHeaderLabel("Process ID", false));
        headerRow.add(createHeaderLabel("Burst Time", false));
        headerRow.add(createHeaderLabel("Arrival Time", false));
        priorityNoHeader = createHeaderLabel("Priority No.", true);
        headerRow.add(priorityNoHeader);

        processTablePanel = new JPanel();
        processTablePanel.setLayout(new BoxLayout(processTablePanel, BoxLayout.Y_AXIS));
        processTablePanel.setOpaque(false);
        processTablePanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        String initId1 = generateProcessId(); usedProcessIds.add(initId1);
        String initId2 = generateProcessId(); usedProcessIds.add(initId2);
        String initId3 = generateProcessId(); usedProcessIds.add(initId3);
        Color initC1 = nextProcessColor();
        Color initC2 = nextProcessColor();
        Color initC3 = nextProcessColor();

        processTablePanel.add(createProcessRow(initId1, initC1, "2", "0", false));
        processTablePanel.add(Box.createVerticalStrut(20));
        processTablePanel.add(createProcessRow(initId2, initC2, "2", "2", false));
        processTablePanel.add(Box.createVerticalStrut(20));
        processTablePanel.add(createProcessRow(initId3, initC3, "2", "4", false));

        Process p1 = new Process(initId1, initC1, 0, 2, 1);
        Process p2 = new Process(initId2, initC2, 2, 2, 2);
        Process p3 = new Process(initId3, initC3, 4, 2, 3);

        processes.add(p1);
        processes.add(p2);
        processes.add(p3);

        JPanel topleftContent = new JPanel(new BorderLayout());
        topleftContent.setOpaque(false); 
        topleftContent.add(headerRow, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(processTablePanel,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        topleftContent.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 14));
        footer.setOpaque(false);
        JButton backToMenuButton = createOtherBtn("Back To Menu");
        backToMenuButton.setPreferredSize(new Dimension(190, 44));
        backToMenuButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) parentContainer.getLayout();
            cl.show(parentContainer, "MainMenu");
        });
        footer.add(backToMenuButton);

        wrapper.add(topleftContent, BorderLayout.CENTER);
        wrapper.add(footer, BorderLayout.SOUTH);

        rightPanel.add(wrapper, BorderLayout.CENTER);
    }

    public JPanel createProcessRow(String name, Color labelColor,
                                    String burst, String arrival, boolean priorityEnabled) {
        JPanel row = new JPanel(new GridLayout(1, 4, 20, 0));
        row.setBackground(branding.dark);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        
        JLabel idLabel = new JLabel(name, SwingConstants.CENTER);
        idLabel.setFont(branding.jetBrainsBMedium);
        idLabel.setForeground(branding.light);
        idLabel.setOpaque(true);
        idLabel.setBackground(labelColor);
        idLabel.setBorder(new LineBorder(labelColor, 10));
        idLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        idLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedRows.contains(row)) {
                    selectedRows.remove(row);
                    idLabel.setBorder(new LineBorder(labelColor, 10));
                } else {
                    selectedRows.add(row);
                    idLabel.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(labelColor.brighter().brighter().brighter(), 6),
                        new EmptyBorder(7, 7, 7, 7)
                    ));
                }
            }
        });
        
        JTextField burstField = new JTextField(burst);
        styleTextField(burstField);
        burstField.setHorizontalAlignment(JTextField.CENTER);
        
        JTextField arrivalField = new JTextField(arrival);
        styleTextField(arrivalField);
        arrivalField.setHorizontalAlignment(JTextField.CENTER);
        
        JTextField priorityField = new JTextField();
        priorityField.setHorizontalAlignment(JTextField.CENTER);
        priorityField.setText("");
        
        styleProcessField(priorityField, priorityEnabled);

        row.add(idLabel);
        row.add(burstField);
        row.add(arrivalField);
        row.add(priorityField);

        return row;
    }

    public JLabel createHeaderLabel(String text, boolean muted) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(branding.jetBrainsBMedium);
        lbl.setForeground(muted ? branding.darkGray : branding.light);
        return lbl;
    }
    
    public JButton createOtherBtn(String text) {
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
        btn.setFont(branding.jetBrainsBMedium);
        btn.setForeground(branding.light);
        btn.setBackground(branding.dark);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(160, 44));
        btn.setMaximumSize(new Dimension(200, 44));

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
    
    public JButton createProcessButton(ImageIcon icon, String text) {
        JButton btn = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(branding.light);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(branding.jetBrainsBMedium);
        btn.setForeground(branding.light);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(10);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        btn.setPreferredSize(new Dimension(350, 62));
        btn.setBorder(new EmptyBorder(0, 20, 0, 20));
        btn.setBackground(branding.dark);

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

    public void styleTextField(JTextField field) {
        field.setFont(branding.jetBrainsRMedium);
        field.setForeground(branding.light);
        field.setBackground(branding.dark);
        field.setCaretColor(branding.light);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(branding.light, 2, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }

    public void styleComboBox(JComboBox<String> box) {
        box.setFont(branding.jetBrainsRMedium);
        box.setForeground(branding.light);
        box.setBackground(branding.dark);
        box.setBorder(new LineBorder(branding.light, 3, true));
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? branding.darkGray : branding.dark);
                setForeground(branding.light);
                setFont(branding.jetBrainsRMedium);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
    }

    public JPanel blankPanel(Color bg, int w, int h) {
        JPanel p = new JPanel();
        p.setBackground(bg);
        if (w > 0) p.setPreferredSize(new Dimension(w, 0));
        if (h > 0) p.setPreferredSize(new Dimension(0, h));
        return p;
    }

    public String generateProcessId() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String id;
        do {
            StringBuilder sb = new StringBuilder("P:");
            for (int i = 0; i < 2; i++) {
                sb.append(chars.charAt((int)(Math.random() * chars.length())));
            }
            id = sb.toString();
        } while (usedProcessIds.contains(id));
        return id;
    }

    public Color nextProcessColor() {
        for (Color c : branding.processColor) {
            if (!usedColors.contains(c)) {
                usedColors.add(c);
                return c;
            }
        }
        
        return branding.processColor[0];
    }

    public void addProcess() {
        long currentCount = Arrays.stream(processTablePanel.getComponents()).filter(c -> c instanceof JPanel).count();
        if (currentCount >= 20) return;

        String id = generateProcessId();
        usedProcessIds.add(id);
        Color color = nextProcessColor();
        boolean priorityEnabled = isPriorityAlgorithm();

        JPanel newRow = createProcessRow(id, color, "1", "0", priorityEnabled);
        processTablePanel.add(Box.createVerticalStrut(20));
        processTablePanel.add(newRow);
        processTablePanel.revalidate();
        processTablePanel.repaint();
    }

    public void removeProcess() {
        if (selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a process by clicking on its Process ID label.",
                "No Process Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        long totalRows = Arrays.stream(processTablePanel.getComponents()).filter(c -> c instanceof JPanel).count();

        if (totalRows - selectedRows.size() < 3) {
            JOptionPane.showMessageDialog(this,
                "At least three processes must remain.",
                "Cannot Remove", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Component> toRemove = new ArrayList<>();
        Component[] comps = processTablePanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (selectedRows.contains(comps[i])) {
                toRemove.add(comps[i]);
                if (i > 0 && !(comps[i - 1] instanceof JPanel)) {
                    toRemove.add(comps[i - 1]);
                } else if (i == 0 && i + 1 < comps.length && !(comps[i + 1] instanceof JPanel)) {
                    toRemove.add(comps[i + 1]);
                }
            }
        }
        
        for (Component c : toRemove) {
            if (c instanceof JPanel) {
                JPanel row = (JPanel) c;
                Component first = row.getComponent(0);
                if (first instanceof JLabel) {
                    usedProcessIds.remove(((JLabel) first).getText());
                    usedColors.remove(((JLabel) first).getBackground());
                }
            }
            processTablePanel.remove(c);
        }

        selectedRows.clear();
        processTablePanel.revalidate();
        processTablePanel.repaint();
    }

    public void randomProcesses() {
        processTablePanel.removeAll();
        usedProcessIds.clear();
        usedColors.clear();
        selectedRows.clear();

        // Processes: 3-20
        int processCount = 3 + (int)(Math.random() * 18);
        boolean priorityEnabled = isPriorityAlgorithm();
        
        ArrayList<Integer> priorities = new ArrayList<>();
        if (priorityEnabled) {
            for (int i = 1; i <= 20; i++) {
                priorities.add(i);
            }
            Collections.shuffle(priorities);
        }

        for (int i = 0; i < processCount; i++) {
            String id = generateProcessId();
            usedProcessIds.add(id);

            // Burst: 1–30
            String burst = String.valueOf(1 + (int)(Math.random() * 30));

            // Arrival: 0–30
            String arrival = String.valueOf((int)(Math.random() * 31));

            Color color = nextProcessColor();

            JPanel row = createProcessRow(id, color, burst, arrival, priorityEnabled);

            if (priorityEnabled) {
                String priority = String.valueOf(priorities.get(i));
                JTextField priorityField = (JTextField) row.getComponent(3);
                priorityField.setText(priority);
                styleProcessField(priorityField, true);
            } else {
                JTextField priorityField = (JTextField) row.getComponent(3);
                styleProcessField(priorityField, false);
                styleHeaderLabel(priorityNoHeader, false);
            }

            processTablePanel.add(row);
            if (i < processCount - 1) {
                processTablePanel.add(Box.createVerticalStrut(20));
            }
        }

        processTablePanel.revalidate();
        processTablePanel.repaint();
    }

    public void importFromFile() {
        JFileChooser chooser = new JFileChooser(new File("src"));
        chooser.setDialogTitle("Import Processes from Text File");
        chooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        List<String[]> rows = new ArrayList<>();
        boolean algoPriority = isPriorityAlgorithm();
        Set<String> importedProcessIds = new HashSet<>();
        boolean hasError = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null && !hasError) {
                lineNumber++;
                line = line.trim();

                String[] parts = line.split(",");
                
                if (parts.length != 4 || 
                    parts[0].trim().isEmpty() ||
                    importedProcessIds.contains(parts[0].trim())) {
                    hasError = true;
                    break;
                }
                importedProcessIds.add(parts[0].trim());
                
                int burst;
                try { burst = Integer.parseInt(parts[1].trim()); }
                catch (NumberFormatException e) {
                    hasError = true;
                    break;
                }
                if (burst < 1 || burst > 30) {
                    hasError = true;
                    break;
                }

                int arrival;
                try { arrival = Integer.parseInt(parts[2].trim()); }
                catch (NumberFormatException e) {
                    hasError = true;
                    break;
                }
                if (arrival < 0 || arrival > 30) {
                    hasError = true;
                    break;
                }
                
                int priority;
                try { priority = Integer.parseInt(parts[3].trim()); }
                catch (NumberFormatException e) {
                    hasError = true;
                    break;
                }
                if (priority < 1 || priority > 20) {
                    hasError = true;
                    break;
                }

                rows.add(parts);
                if (rows.size() > 20) {
                    hasError = true;
                    break;
                }
            }
        } catch (IOException e) {
            hasError = true;
        }

        if (hasError || rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error in importing text file! Invalid format detected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (algoPriority) {
            Set<Integer> seen = new HashSet<>();
            for (int i = 0; i < rows.size(); i++) {
                int p = Integer.parseInt(rows.get(i)[3].trim());
                if (!seen.add(p)) {
                    JOptionPane.showMessageDialog(this, "Error in importing text file! Invalid format detected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        processTablePanel.removeAll();
        processes.clear();
        usedProcessIds.clear();
        usedColors.clear();
        selectedRows.clear();

        for (int i = 0; i < rows.size(); i++) {
            String[] parts = rows.get(i);
            String id = parts[0].trim();
            usedProcessIds.add(id);
            Color color = nextProcessColor();
            int burst = Integer.parseInt(parts[1].trim());
            int arrival = Integer.parseInt(parts[2].trim());

            JPanel row = createProcessRow(id, color, String.valueOf(burst), String.valueOf(arrival), algoPriority);
            
            if (algoPriority) {
                JTextField priorityField = (JTextField) row.getComponent(3);
                priorityField.setText(parts[3].trim());
                styleProcessField(priorityField, true);
            }

            processTablePanel.add(row);
            if (i < rows.size() - 1) processTablePanel.add(Box.createVerticalStrut(20));

            processes.add(new model.Process(id, color, arrival, burst, i + 1));
        }

        processTablePanel.revalidate();
        processTablePanel.repaint();
    }

    public boolean isPriorityAlgorithm() {
        String algo = (String) algorithmComboBox.getSelectedItem();
        return algo.contains("Priority");
    }

    public boolean isRoundRobinAlgorithm() {
        String algo = (String) algorithmComboBox.getSelectedItem();
        return algo.contains("Round Robin");
    }

    public void updateAlgorithmFields() {
        boolean roundRobin = isRoundRobinAlgorithm();
        boolean priority = isPriorityAlgorithm();

        styleProcessField(quantumTimeField, roundRobin);
        styleHeaderLabel(quantumTimeLabel, roundRobin);
        
        styleHeaderLabel(priorityNoHeader, priority);
        for (Component comp : processTablePanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel row = (JPanel) comp;
                JTextField priorityField = (JTextField) row.getComponent(3);
                priorityField.setEnabled(priority);
                styleProcessField(priorityField, priority);
            }
        }
    }

    public void styleProcessField(JTextField field, boolean enabled) {
        field.setEnabled(enabled);
        field.setFont(branding.jetBrainsBMedium);
        field.setBackground(branding.dark);
        field.setForeground(enabled ? branding.light : branding.darkGray);
        field.setCaretColor(enabled ? branding.light : branding.darkGray);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(enabled ? branding.light : branding.darkGray, 2, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }
    
    public void styleHeaderLabel(JLabel label, boolean enabled) {
        label.setForeground(enabled ? branding.light : branding.darkGray);
    }

    public boolean validateProcessTable() {
        if (algorithmComboBox.getSelectedItem().toString().contains("Round Robin")) {
            if (!isInteger(quantumTimeField.getText())) {
                showValidationError("Quantum Time must be an integer.");
                return false;
            }

            int quantum = Integer.parseInt(quantumTimeField.getText().trim());

            if (quantum < 1 || quantum > 10) {
                showValidationError("Quantum Time must be between 1 and 10.");
                return false;
            }
        }
        
        Set<Integer> usedPriorities = new HashSet<>();

        for (Component comp : processTablePanel.getComponents()) {
            if (!(comp instanceof JPanel)) continue;

            JPanel row = (JPanel) comp;
            Component[] fields = row.getComponents();

            JTextField burstField = (JTextField) fields[1];
            JTextField arrivalField = (JTextField) fields[2];
            JTextField priorityField = (JTextField) fields[3];

            // Burst
            if (!isInteger(burstField.getText())) {
                showValidationError("Burst Time must be an integer.");
                return false;
            }

            int burst = Integer.parseInt(burstField.getText().trim());
            if (burst < 1 || burst > 30) {
                showValidationError("Burst Time must be between 1 and 30.");
                return false;
            }

            // Arrival
            if (!isInteger(arrivalField.getText())) {
                showValidationError("Arrival Time must be an integer.");
                return false;
            }

            int arrival = Integer.parseInt(arrivalField.getText().trim());
            if (arrival < 0 || arrival > 30) {
                showValidationError("Arrival Time must be between 0 and 30.");
                return false;
            }

            // Priority
            if (priorityField.isEnabled()) {
                if (!isInteger(priorityField.getText())) {
                    showValidationError("Priority must be an integer.");
                    return false;
                }

                int priority = Integer.parseInt(priorityField.getText().trim());

                if (priority < 1 || priority > 20) {
                    showValidationError("Priority must be between 1 and 20.");
                    return false;
                }

                if (!usedPriorities.add(priority)) {
                    showValidationError("Priority values must not duplicate.");
                    return false;
                }
            }
        }

        return true;
    }

    public void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isInteger(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // ==================================================
    //                GETTERS AND SETTERS
    // ==================================================

    public ArrayList<Process> getProcesses() {
        ArrayList<Process> currentProcesses = new ArrayList<>();

        for (Component comp : processTablePanel.getComponents()) {
            if (!(comp instanceof JPanel)) {
                continue;
            }

            JPanel row = (JPanel) comp;
            if (row.getComponentCount() < 4) {
                continue;
            }

            JLabel idLabel = (JLabel) row.getComponent(0);
            JTextField burstField = (JTextField) row.getComponent(1);
            JTextField arrivalField = (JTextField) row.getComponent(2);
            JTextField priorityField = (JTextField) row.getComponent(3);

            int burst = Integer.parseInt(burstField.getText().trim());
            int arrival = Integer.parseInt(arrivalField.getText().trim());
            int priority = 0;

            if (priorityField.isEnabled()) {
                priority = Integer.parseInt(priorityField.getText().trim());
            }

            Process process = new Process(
                idLabel.getText().trim(),
                idLabel.getBackground(),
                arrival,
                burst,
                priority
            );

            currentProcesses.add(process);
        }

        return currentProcesses;
    }
    
    public void refreshStyles() {
        styleComboBox(algorithmComboBox);
        updateAlgorithmFields();

        addProcessBtn.setIcon(branding.lightIcoAddProcess);
        removeProcessBtn.setIcon(branding.lightIcoRemoveProcess);
        importTextFileBtn.setIcon(branding.lightIcoImportProcess);
        randomProcessesBtn.setIcon(branding.lightIcoRandomProcess);

        for (Component comp : processTablePanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel row = (JPanel) comp;
                row.setBackground(branding.dark);
                for (int i = 1; i < row.getComponentCount(); i++) {
                    Component c = row.getComponent(i);
                    if (c instanceof JTextField) {
                        JTextField tf = (JTextField) c;
                        boolean enabled = tf.isEnabled();
                        if (i == 3) {
                            styleProcessField(tf, enabled);
                        } else {
                            styleTextField(tf);
                        }
                    }
                }
            }
        }
    }

}