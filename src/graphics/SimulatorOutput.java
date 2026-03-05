package graphics;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import engine.MainEngine;
import util.GanttChartBlocks;
import model.Process;

public class SimulatorOutput extends JPanel {
    private Branding branding;
    private MainEngine mainEngine;
    private JPanel parentContainer;

    private JPanel mainPanel;
    private JPanel topPanel, bottomPanel;
    
    private JLabel timerLabel;
    private JLabel algorithmNameLabel;
    private JPanel ganttChartPanel;

    private JScrollPane ganttScrollPane;
    private JScrollPane tableScrollPane;
    
    private Timer simulationTimer;
    private int currentTime = 0;
    private int totalTime = 0;
    private JPanel liveGanttChart;
    
    private JButton goBackButton;
    private JPanel tableHeaderPanel;

    public SimulatorOutput(MainEngine mainEngine, Branding branding, JPanel parentContainer) {
        this.branding = branding;
        this.mainEngine = mainEngine;
        this.parentContainer = parentContainer;

        setLayout(new BorderLayout());
        setBackground(branding.dark);

        initializeMainPanel();
        initializePanels();
    }

    public void initializeMainPanel() {
        JPanel margin = new JPanel(new BorderLayout());
        margin.setBackground(branding.dark);

        int marginWidth = 20, marginHeight = 20;

        margin.add(blankPanel(branding.dark, 0, marginHeight), BorderLayout.NORTH);
        margin.add(blankPanel(branding.dark, 0, marginHeight), BorderLayout.SOUTH);
        margin.add(blankPanel(branding.dark, marginWidth, 0), BorderLayout.WEST);
        margin.add(blankPanel(branding.dark, marginWidth, 0), BorderLayout.EAST);

        mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(branding.dark);
        margin.add(mainPanel, BorderLayout.CENTER);

        add(margin, BorderLayout.CENTER);
    }
    
    public void initializePanels() {
        initializeTopPanel();
        initializeBottomPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);
    }
    
    public void initializeTopPanel() {
        topPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(branding.dark);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(branding.light);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(0, 200));
        topPanel.setBorder(new EmptyBorder(14, 18, 14, 18));
        
        timerLabel = new JLabel("Timer: 0:06");
        timerLabel.setFont(branding.jetBrainsBMedium);
        timerLabel.setForeground(branding.light);
        
        algorithmNameLabel = new JLabel("");
        algorithmNameLabel.setFont(branding.jetBrainsBMedium);
        algorithmNameLabel.setForeground(branding.light);
        algorithmNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.add(timerLabel, BorderLayout.WEST);
        headerRow.add(algorithmNameLabel, BorderLayout.EAST);
        
        JPanel chartContent = createGanttChart();
        ganttScrollPane = new JScrollPane(chartContent, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScrollPane.setBorder(null);
        ganttScrollPane.setOpaque(false);
        ganttScrollPane.getViewport().setOpaque(false);

        ganttChartPanel = new JPanel(new BorderLayout());
        ganttChartPanel.setOpaque(false);
        ganttChartPanel.add(ganttScrollPane, BorderLayout.CENTER);

        topPanel.add(headerRow, BorderLayout.NORTH);
        topPanel.add(ganttChartPanel, BorderLayout.CENTER);
    }
    
    public JPanel createGanttChart() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel chart = new JPanel() {
            String[] labels  = {"P1", "P2", "P3"};
            Color[] colors  = {
                branding.processColor[0],
                branding.processColor[1],
                branding.processColor[2]
            };
            int[] ticks = { 0, 2, 4, 6 };
            int blockHeight = 38;
            int blockWidth = 100;
            int timeStampLoc = blockHeight + 6;
            int paddingLeft = 10;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int totalBars = labels.length;
                int totalWidth = totalBars * blockWidth;
                int startX = Math.max((getWidth() - totalWidth) / 2, paddingLeft);
                
                for (int i = 0; i < totalBars; i++) {
                    int x = startX + i * blockWidth;
                    g2.setColor(colors[i]);
                    g2.fillRect(x, 0, blockWidth, blockHeight);
                    
                    g2.setColor(Color.WHITE);
                    g2.setFont(branding.jetBrainsBMedium);
                    FontMetrics fm = g2.getFontMetrics();
                    int lx = x + (blockWidth - fm.stringWidth(labels[i])) / 2;
                    int ly = (blockHeight + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(labels[i], lx, ly);
                }
                
                g2.setFont(branding.jetBrainsBSmall);
                g2.setColor(branding.light);
                FontMetrics fm = g2.getFontMetrics();
                for (int i = 0; i <= totalBars; i++) {
                    int x = startX + i * blockWidth;
                    String t = String.valueOf(ticks[i]);
                    int tx = x - fm.stringWidth(t) / 2;
                    g2.drawString(t, tx, timeStampLoc + fm.getAscent());
                }

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(320 + paddingLeft * 2, 60);
            }
        };
        chart.setOpaque(false);

        wrapper.add(chart);
        return wrapper;
    }
    
    public void initializeBottomPanel() {
        bottomPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(branding.dark);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(branding.light);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 24, 24, 24));
        
        goBackButton = createGoBackButton("Go Back");

        JPanel goBackRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        goBackRow.setOpaque(false);
        goBackRow.add(goBackButton);
        
        JPanel tableContent = createProcessTable();

        bottomPanel.add(goBackRow, BorderLayout.NORTH);
        bottomPanel.add(tableContent, BorderLayout.CENTER);
    }

    public JPanel createProcessTable() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] headers = {
            "Process ID",
            "Burst Time",
            "Arrival Time",
            "Priority No.",
            "Waiting\nTime",
            "Turnaround\nTime",
            "Average\nWaiting\nTime",
            "Average\nTurnaround\nTime"
        };
        
        tableHeaderPanel = new JPanel(new GridLayout(1, 8, 8, 0));
        tableHeaderPanel.setOpaque(false);
        for (String h : headers) {
            tableHeaderPanel.add(createMultiLineHeader(h));
        }
        
        Object[][] data = {
            { "P1", "2", "0", "N/A", "0", "2", "0", "2" },
            { "P2", "2", "2", "N/A", "0", "2", "0", "2" },
            { "P3", "2", "4", "N/A", "0", "2", "0", "2" },
        };
        Color[] placeholderColors = { branding.processColor[0], branding.processColor[1], branding.processColor[2] };

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setOpaque(false);

        for (int i = 0; i < data.length; i++) {
            dataPanel.add(Box.createVerticalStrut(8));
            dataPanel.add(createDataRow(data[i], placeholderColors[i]));
        }
        
        tableScrollPane = new JScrollPane(dataPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setBorder(null);
        tableScrollPane.setOpaque(false);
        tableScrollPane.getViewport().setOpaque(false);

        wrapper.add(tableHeaderPanel, BorderLayout.NORTH);
        wrapper.add(tableScrollPane, BorderLayout.CENTER);

        return wrapper;
    }

    public JPanel createDataRow(Object[] values, Color idColor) {
        JPanel row = new JPanel(new GridLayout(1, values.length, 8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        for (int i = 0; i < values.length; i++) {
            String text = values[i].toString();
            JLabel cell = new JLabel(text, SwingConstants.CENTER);
            cell.setFont(branding.jetBrainsBMedium);
            
            if (i == 0 && idColor != null) {
                cell.setOpaque(true);
                cell.setBackground(idColor);
                cell.setForeground(Color.WHITE);
            } else {
                cell.setOpaque(false);
                cell.setForeground(branding.light);
            }

            cell.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(branding.light, 1, true),
                new EmptyBorder(6, 4, 6, 4)
            ));
            row.add(cell);
        }
        return row;
    }

    public JPanel createMultiLineHeader(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        String[] lines = text.split("\n");
        JPanel linesPanel = new JPanel();
        linesPanel.setLayout(new BoxLayout(linesPanel, BoxLayout.Y_AXIS));
        linesPanel.setOpaque(false);

        for (String line : lines) {
            JLabel lbl = new JLabel(line, SwingConstants.CENTER);
            lbl.setFont(branding.jetBrainsBMedium);
            lbl.setForeground(branding.light);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            linesPanel.add(lbl);
        }

        panel.add(linesPanel, gbc);
        return panel;
    }

    public JButton createGoBackButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(branding.dark);
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
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 48));

        btn.addActionListener(e -> {
            System.out.println("@ Go Back to SimulatorMain");
            if (simulationTimer != null && simulationTimer.isRunning()) {
                simulationTimer.stop();
            }
            CardLayout cl = (CardLayout) parentContainer.getLayout();
            cl.show(parentContainer, "SimulatorMain");
        });

        return btn;
    }

    public JPanel blankPanel(Color bg, int w, int h) {
        JPanel p = new JPanel();
        p.setBackground(bg);
        if (w > 0) p.setPreferredSize(new Dimension(w, 0));
        if (h > 0) p.setPreferredSize(new Dimension(0, h));
        return p;
    }

    public void loadSimulationResults() {
        ArrayList<Process> results = mainEngine.getFinalProcesses();
        ArrayList<GanttChartBlocks> blocks = mainEngine.getGanttChartBlocks();
        setAlgorithmNameLabel(mainEngine.getSelectedAlgorithm());
        
        totalTime = 0;
        if (blocks != null) {
            for (GanttChartBlocks b : blocks)
                totalTime = Math.max(totalTime, b.getEndTime());
        }
        
        currentTime = 0;
        timerLabel.setText("Timer: 0:00");

        updateGanttChart(blocks);
        updateProcessTable(results);
        
        if (simulationTimer != null && simulationTimer.isRunning()) {
            simulationTimer.stop();
        }
        
        simulationTimer = new Timer(1000, null);
        simulationTimer.addActionListener(e -> {
            currentTime++;
            int minutes = currentTime / 60;
            int seconds = currentTime % 60;
            timerLabel.setText("Timer: " + String.format("%d:%02d", minutes, seconds));
            if (liveGanttChart != null) liveGanttChart.repaint();
            if (currentTime >= totalTime) {
                simulationTimer.stop();
            }
        });
        simulationTimer.start();
    }

    public void updateGanttChart(ArrayList<GanttChartBlocks> blocks) {
        int pxPerBlockUnit = 30;
        int blockHeight = 38;
        int timeStampLoc = blockHeight + 15;
        int paddingLeft = 10;

        liveGanttChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (blocks == null || blocks.isEmpty()) return;

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int x = paddingLeft;

                g2.setFont(branding.jetBrainsBSmall);
                g2.setColor(branding.light);
                FontMetrics fmTick = g2.getFontMetrics();
                g2.drawString("0", x - fmTick.stringWidth("0") / 2, timeStampLoc);

                for (GanttChartBlocks block : blocks) {
                    int width = (block.getEndTime() - block.getStartTime()) * pxPerBlockUnit;
                    g2.setColor(block.getColor());
                    g2.fillRoundRect(x, 0, width, blockHeight, 10, 10);
                    
                    g2.setColor(Color.WHITE);
                    g2.setFont(branding.jetBrainsBMedium);
                    FontMetrics fm = g2.getFontMetrics();
                    int lx = x + (width - fm.stringWidth(block.getProcessID())) / 2;
                    int ly = (blockHeight + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(block.getProcessID(), lx, ly);
                    
                    g2.setFont(branding.jetBrainsBSmall);
                    String t = String.valueOf(block.getEndTime());
                    g2.setColor(branding.light);
                    FontMetrics fmS = g2.getFontMetrics();
                    g2.drawString(t, x + width - fmS.stringWidth(t) / 2, timeStampLoc);

                    x += width;
                }
                
                int filledPx = paddingLeft + currentTime * pxPerBlockUnit;
                int totalPx  = x;
                if (filledPx < totalPx) {
                    g2.setColor(new Color(0, 0, 0, 160));
                    g2.fillRect(filledPx, 0, totalPx - filledPx, blockHeight);
                }

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                int totalWidth = paddingLeft;
                if (blocks != null) {
                    for (GanttChartBlocks b : blocks)
                        totalWidth += (b.getEndTime() - b.getStartTime()) * pxPerBlockUnit;
                }
                totalWidth += paddingLeft;
                return new Dimension(totalWidth, 60);
            }
        };
        liveGanttChart.setOpaque(false);
        
        JPanel ganttWrapper = new JPanel(new GridBagLayout());
        ganttWrapper.setOpaque(false);
        ganttWrapper.add(liveGanttChart);

        ganttScrollPane.setViewportView(ganttWrapper);
        ganttScrollPane.revalidate();
        ganttScrollPane.repaint();
    }

    public void updateProcessTable(ArrayList<Process> results) {
        String[] headers = {
            "Process ID",
            "Burst Time",
            "Arrival Time",
            "Priority No.",
            "Waiting\nTime",
            "Turnaround\nTime",
            "Average\nWaiting\nTime",
            "Average\nTurnaround\nTime"
        };
        
        tableHeaderPanel.removeAll();
        tableHeaderPanel.setLayout(new GridLayout(1, headers.length, 8, 0));
        for (String h : headers) tableHeaderPanel.add(createMultiLineHeader(h));
        tableHeaderPanel.revalidate();
        tableHeaderPanel.repaint();
        
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setOpaque(false);
        
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (Process p : results) {
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
        }
        
        double avgWaitingTime = results.isEmpty() ? 0 : (double) totalWaitingTime / results.size();
        double avgTurnaroundTime = results.isEmpty() ? 0 : (double) totalTurnaroundTime / results.size();

        for (Process p : results) {
            Object[] row = {
                p.getID(),
                String.valueOf(p.getBurstTime()),
                String.valueOf(p.getArrivalTime()),
                p.getPriority() > 0 ? String.valueOf(p.getPriority()) : "N/A",
                String.valueOf(p.getWaitingTime()),
                String.valueOf(p.getTurnaroundTime()),
                String.format("%.2f", avgWaitingTime),
                String.format("%.2f", avgTurnaroundTime)
            };
            dataPanel.add(Box.createVerticalStrut(8));
            dataPanel.add(createDataRow(row, p.getColor()));
        }

        tableScrollPane.setViewportView(dataPanel);
        tableScrollPane.revalidate();
        tableScrollPane.repaint();
    }
    
    public void refreshStyles() {
        ArrayList<Process> results = mainEngine.getFinalProcesses();
        if (results != null && !results.isEmpty()) {
            updateProcessTable(results);
        } else {
            Object[][] data = {
                {"P1", "2", "0", "N/A", "0", "2", "0", "2"},
                {"P2", "2", "2", "N/A", "0", "2", "0", "2"},
                {"P3", "2", "4", "N/A", "0", "2", "0", "2"},
            };
            Color[] placeholderColors = {
                branding.processColor[0],
                branding.processColor[1],
                branding.processColor[2]
            };

            JPanel dataPanel = new JPanel();
            dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
            dataPanel.setOpaque(false);
            for (int i = 0; i < data.length; i++) {
                dataPanel.add(Box.createVerticalStrut(8));
                dataPanel.add(createDataRow(data[i], placeholderColors[i]));
            }
            tableScrollPane.setViewportView(dataPanel);
            tableScrollPane.revalidate();
            tableScrollPane.repaint();
        }
    }

    public void setAlgorithmNameLabel(String label) {
        algorithmNameLabel.setText(label);
    }
}