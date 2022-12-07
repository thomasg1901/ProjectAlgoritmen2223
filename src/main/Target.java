package main;

import java.util.List;

public class Target {
    Terminal finalTerminal;
    private int maxHeight;
    private int targetHeight;
    private List<Assignment> assignments;

    public Target(Terminal finalTerminal, int maxHeight, int targetHeight, List<Assignment> assignments) {
        this.finalTerminal = finalTerminal;
        this.maxHeight = maxHeight;
        this.targetHeight = targetHeight;
        this.assignments = assignments;
    }

    public Terminal getFinalTerminal() {
        return finalTerminal;
    }

    public void setFinalTerminal(Terminal finalTerminal) {
        this.finalTerminal = finalTerminal;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
}
