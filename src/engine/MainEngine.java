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

    public MainEngine() {
        finalProcesses = new ArrayList<>();
        ganttChartBlocks = new ArrayList<>();
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
        ganttChartBlocks.clear();

        if (processes == null || processes.isEmpty()) {
            System.out.println("No processes to simulate.");
            return;
        }

        System.out.println("Running algorithm: " + algorithm);

        switch (algorithm) {
            case "FCFS (First Come First Serve)":
                finalProcesses = fcfs.run(processes);
                ganttChartBlocks = fcfs.getGanttChartBlocks();
                break;

            case "SJF (Shortest Job First)":
                finalProcesses = sjf.run(processes);
                ganttChartBlocks = sjf.getGanttChartBlocks();
                break;

            case "SRTF (Shortest Remaining Time First)":
                finalProcesses = srtf.run(processes);
                ganttChartBlocks = srtf.getGanttChartBlocks();
                break;

            case "Round Robin":
                roundRobin.setQuantumTime(quantum);
                finalProcesses = roundRobin.run(processes);
                ganttChartBlocks = roundRobin.getGanttChartBlocks();
                break;

            case "Priority (Preemptive)":
                finalProcesses = priorityPreemptive.run(processes);
                ganttChartBlocks = priorityPreemptive.getGanttChartBlocks();
                break;

            case "Priority (Non-Preemptive)":
                finalProcesses = priorityNonPreemptive.run(processes);
                ganttChartBlocks = priorityNonPreemptive.getGanttChartBlocks();
                break;

            default:
                System.out.println("Unknown algorithm: " + algorithm);
                break;
        }

        gui.getSimulatorOutput().setAlgorithmNameLabel(algorithm);
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
}