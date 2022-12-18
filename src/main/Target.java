package main;

import java.util.List;

public class Target {
    Terminal initialTerminal;
    Terminal finalTerminal;
    private int maxHeight;
    private int targetHeight;
    private List<Assignment> moveAssignments;

    public Target(Terminal intialTerminal, Terminal finalTerminal, int maxHeight) {
        this.initialTerminal = intialTerminal;
        this.finalTerminal = finalTerminal;

        this.maxHeight = maxHeight;
        this.moveAssignments = calculateToFinialTerminal();
    }

    public Target(Terminal initialTerminal, int maxHeight, int targetHeight){
        this.initialTerminal = initialTerminal;
        this.maxHeight = maxHeight;
        this.targetHeight = targetHeight;

        this.moveAssignments = calculateToTargetHeight();
    }

    private List<Assignment> calculateToFinialTerminal(){

    }

    private List<Assignment> calculateToTargetHeight(){

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

    public List<Assignment> getMoveAssignments() {
        return moveAssignments;
    }

    public void setMoveAssignments(List<Assignment> moveAssignments) {
        this.moveAssignments = moveAssignments;
    }
}
