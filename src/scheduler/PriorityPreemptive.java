package scheduler;

import model.Process;
import util.GanttChartBlocks;
import java.awt.Color;
import java.util.ArrayList;

public class PriorityPreemptive {
    private ArrayList<GanttChartBlocks> ganttChartBlocks;

    public PriorityPreemptive() {
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

                // Priority Preemptive: lower priority number = higher priority
                if (currentProcess == null
                    || p.getPriority() < currentProcess.getPriority()
                    || (p.getPriority() == currentProcess.getPriority()
                        && p.getArrivalTime() < currentProcess.getArrivalTime())
                    || (p.getPriority() == currentProcess.getPriority()
                        && p.getArrivalTime() == currentProcess.getArrivalTime()
                        && p.getID().compareTo(currentProcess.getID()) < 0)) {
                    currentProcess = p;
                }
            }

            if (currentProcess == null) {
                addOrExtendGanttBlock("IDLE", Color.GRAY, currentTime, currentTime + 1);
                currentTime++;
                continue;
            }

            addOrExtendGanttBlock(
                currentProcess.getID(),
                currentProcess.getColor(),
                currentTime,
                currentTime + 1
            );

            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
            currentTime++;

            if (currentProcess.getRemainingTime() == 0) {
                completed++;

                int completionTime = currentTime;
                int turnaroundTime = completionTime - currentProcess.getArrivalTime();
                int waitingTime = turnaroundTime - currentProcess.getBurstTime();

                currentProcess.setCompletionTime(completionTime);
                currentProcess.setTurnaroundTime(turnaroundTime);
                currentProcess.setWaitingTime(waitingTime);
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
}
