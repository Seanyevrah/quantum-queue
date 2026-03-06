package scheduler;

import model.Process;
import util.GanttChartBlocks;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class RoundRobin {
    private ArrayList<GanttChartBlocks> ganttChartBlocks;
    private int quantumTime;

    public RoundRobin() {
        ganttChartBlocks = new ArrayList<>();
    }

    public ArrayList<Process> run(ArrayList<Process> process) {
        ArrayList<Process> processResult = new ArrayList<>();

        ganttChartBlocks.clear();

        if (process == null || process.isEmpty()) {
            return processResult;
        }

        for (Process p : process) {
            p.setRemainingTime(p.getBurstTime());
            p.setCompletionTime(0);
            p.setTurnaroundTime(0);
            p.setWaitingTime(0);
        }

        int completed = 0;
        int currentTime = 0;
        Queue<Process> readyQueue = new LinkedList<>();
        ArrayList<Process> arrivedProcesses = new ArrayList<>();

        while (completed < process.size()) {
            for (Process p : process) {
                if (p.getArrivalTime() == currentTime && !arrivedProcesses.contains(p)) {
                    readyQueue.add(p);
                    arrivedProcesses.add(p);
                }
            }

            if (readyQueue.isEmpty()) {
                addOrExtendGanttBlock("IDLE", Color.GRAY, currentTime, currentTime + 1);
                currentTime++;
                continue;
            }

            Process currentProcess = readyQueue.poll();
            int executionTime = Math.min(quantumTime, currentProcess.getRemainingTime());

            addOrExtendGanttBlock(
                currentProcess.getID(),
                currentProcess.getColor(),
                currentTime,
                currentTime + executionTime
            );

            currentTime += executionTime;
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - executionTime);
            
            for (Process p : process) {
                if (p.getArrivalTime() > (currentTime - executionTime) 
                    && p.getArrivalTime() <= currentTime 
                    && !arrivedProcesses.contains(p)) {
                    readyQueue.add(p);
                    arrivedProcesses.add(p);
                }
            }

            if (currentProcess.getRemainingTime() == 0) {
                completed++;
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnaroundTime(currentTime - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
            } else {
                readyQueue.add(currentProcess);
            }
        }

        processResult.addAll(process);

        return processResult;
    }

    private void addOrExtendGanttBlock(String processId, Color color, int startTime, int endTime) {
        if (!ganttChartBlocks.isEmpty()) {
            GanttChartBlocks lastBlock = ganttChartBlocks.get(ganttChartBlocks.size() - 1);
            if (lastBlock.getProcessID().equals(processId) && lastBlock.getEndTime() == startTime) {
                lastBlock.setEndTime(endTime);
                return;
            }
        }

        ganttChartBlocks.add(new GanttChartBlocks(processId, color, startTime, endTime));
    }


    // ==================================================
    //                GETTERS AND SETTERS
    // ==================================================

    public ArrayList<GanttChartBlocks> getGanttChartBlocks() {
        return ganttChartBlocks;
    }

    public void setGanttChartBlocks(ArrayList<GanttChartBlocks> ganttChartBlocks) {
        this.ganttChartBlocks = ganttChartBlocks;
    }

    public int getQuantumTime() {
        return quantumTime;
    }

    public void setQuantumTime(int quantumTime) {
        this.quantumTime = quantumTime;
    }
}