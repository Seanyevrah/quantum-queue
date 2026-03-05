package engine;

import java.util.ArrayList;

import graphics.*;
import scheduler.FCFS;
import scheduler.PriorityNonPreemptive;
import scheduler.PriorityPreemptive;
import scheduler.RoundRobin;
import scheduler.SJF;
import scheduler.SRTF;
import model.Process;
import util.GanttChartBlocks;

public class MainEngine {
    private MainGUI gui;

    private FCFS fcfs;
    private SJF sjf;
    private SRTF srtf;
    private RoundRobin roundRobin;
    private PriorityPreemptive priorityPreemptive;
    private PriorityNonPreemptive priorityNonPreemptive;

    private ArrayList<Process> finalProcesses;
    private ArrayList<GanttChartBlocks> ganttChartBlocks;
    private String selectedAlgorithm;

    public MainEngine() {
        finalProcesses = new ArrayList<>();
        ganttChartBlocks = new ArrayList<>();
        selectedAlgorithm = "";
        initializeAlgorithms();
    }

    public void initializeAlgorithms() {
        fcfs = new FCFS();
        sjf = new SJF();
        srtf = new SRTF();
        roundRobin = new RoundRobin();
        priorityPreemptive = new PriorityPreemptive();
        priorityNonPreemptive = new PriorityNonPreemptive();
    }

    public void runSimulation(ArrayList<Process> processes, String algorithm, int quantum) {
        finalProcesses = new ArrayList<>();
        ganttChartBlocks = new ArrayList<>();
        selectedAlgorithm = algorithm == null ? "" : algorithm;

        if (processes == null || processes.isEmpty()) {
            System.out.println("No processes to simulate.");
            return;
        }

        String algorithmKey = normalizeAlgorithmKey(algorithm);
        System.out.println("Running algorithm: " + selectedAlgorithm + " [" + algorithmKey + "]");

        switch (algorithmKey) {
            case "FCFS":
                finalProcesses = fcfs.run(processes);
                ganttChartBlocks = fcfs.getGanttChartBlocks();
                break;

            case "SJF":
                finalProcesses = sjf.run(processes);
                ganttChartBlocks = sjf.getGanttChartBlocks();
                break;

            case "SRTF":
                finalProcesses = srtf.run(processes);
                ganttChartBlocks = srtf.getGanttChartBlocks();
                break;

            case "Round Robin":
                roundRobin.setQuantumTime(quantum);
                finalProcesses = roundRobin.run(processes);
                ganttChartBlocks = roundRobin.getGanttChartBlocks();
                break;

            case "PRIORITY_PREEMPTIVE":
                finalProcesses = priorityPreemptive.run(processes);
                ganttChartBlocks = priorityPreemptive.getGanttChartBlocks();
                break;

            case "PRIORITY_NON_PREEMPTIVE":
                finalProcesses = priorityNonPreemptive.run(processes);
                ganttChartBlocks = priorityNonPreemptive.getGanttChartBlocks();
                break;

            default:
                System.out.println("Unknown algorithm: " + algorithm);
                break;
        }

        gui.getSimulatorOutput().setAlgorithmNameLabel(selectedAlgorithm);
    }

    private String normalizeAlgorithmKey(String algorithm) {
        if (algorithm == null) {
            return "UNKNOWN";
        }

        String normalized = algorithm.trim().toUpperCase();

        if (normalized.contains("FCFS") || normalized.contains("FIRST COME")) {
            return "FCFS";
        }

        if (normalized.contains("SRTF") || normalized.contains("STRF") || normalized.contains("SHORTEST REMAINING")) {
            return "SRTF";
        }

        if (normalized.contains("SJF") || normalized.contains("SHORTEST JOB")) {
            return "SJF";
        }

        if (normalized.contains("ROUND ROBIN")) {
            return "Round Robin";
        }

        if (normalized.contains("PRIORITY") && normalized.contains("NON")) {
            return "PRIORITY_NON_PREEMPTIVE";
        }

        if (normalized.contains("PRIORITY") && normalized.contains("PREEMPTIVE")) {
            return "PRIORITY_PREEMPTIVE";
        }

        return "UNKNOWN";
    }


    // ==================================================
    //                GETTERS AND SETTERS
    // ==================================================

    public void setGUI(MainGUI gui) {
        this.gui = gui;
    }
    
    public MainGUI getGUI() {
        return gui;
    }

    public ArrayList<Process> getFinalProcesses() {
        return finalProcesses;
    }

    public void setFinalProcesses(ArrayList<Process> finalProcesses) {
        this.finalProcesses = finalProcesses;
    }

    public ArrayList<GanttChartBlocks> getGanttChartBlocks() {
        return ganttChartBlocks;
    }

    public void setGanttChartBlocks(ArrayList<GanttChartBlocks> ganttChartBlocks) {
        this.ganttChartBlocks = ganttChartBlocks;
    }

    public String getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public void setSelectedAlgorithm(String selectedAlgorithm) {
        this.selectedAlgorithm = selectedAlgorithm;
    }
}