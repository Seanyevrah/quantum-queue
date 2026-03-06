package scheduler;

import java.awt.Color;
import java.util.ArrayList;
import model.Process;
import util.GanttChartBlocks;

public class FCFS {
    private ArrayList<GanttChartBlocks> ganttChartBlocks;

    public FCFS() {
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

        while (completed < process.size()) {
            Process currentProcess = null;

            for (Process p : process) {
                if (p.getArrivalTime() > currentTime || p.getRemainingTime() <= 0) {
                    continue;
                }

                // FCFS: pick earliest arrival time; break ties by process ID
                if (currentProcess == null
                    || p.getArrivalTime() < currentProcess.getArrivalTime()
                    || (p.getArrivalTime() == currentProcess.getArrivalTime()
                        && p.getID().compareTo(currentProcess.getID()) < 0)) {
                    currentProcess = p;
                }
            }

            if (currentProcess == null) {
                addOrExtendGanttBlock("IDLE", Color.GRAY, currentTime, currentTime + 1);
                currentTime++;
                continue;
            }

            int startTime = currentTime;
            int endTime = currentTime + currentProcess.getBurstTime();

            addOrExtendGanttBlock(
                currentProcess.getID(),
                currentProcess.getColor(),
                startTime,
                endTime
            );

            currentProcess.setRemainingTime(0);
            currentProcess.setCompletionTime(endTime);
            currentProcess.setTurnaroundTime(endTime - currentProcess.getArrivalTime());
            currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());

            currentTime = endTime;
            completed++;
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
}