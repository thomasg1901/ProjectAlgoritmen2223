package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Target {
    Terminal initialTerminal;
    Terminal finalTerminal;
    private int maxHeight;
    private int targetHeight;
    private List<Assignment> moveAssignments;

    public Target(Terminal initialTerminal, Terminal finalTerminal) {
        this.initialTerminal = initialTerminal;
        this.finalTerminal = finalTerminal;
        this.maxHeight = initialTerminal.getMaxHeight();
        this.moveAssignments = calculateToFinalTerminal();
    }

    public Target(Terminal initialTerminal){
        this.initialTerminal = initialTerminal;
        this.maxHeight = this.initialTerminal.getMaxHeight();
        this.targetHeight = this.initialTerminal.getTargetHeight();
        this.moveAssignments = calculateToTargetHeight();
    }


    private List<Assignment> calculateToFinalTerminal(){
        return null;
    }

    private List<Assignment> calculateToTargetHeight(){
        return null;
    }

    public void convertTerminal(){
        if(maxHeight > targetHeight){
            Map<Integer, List<Slot>> slots = getSlotsAboveTargetHeight(initialTerminal.getSlotGrid());
//            for(Slot slot : slots){
//                List<Slot> feasibleLeftSlots = getFeasibleLeftSlots()
//            }
        }
    }

    public List<Slot> getFeasibleLeftSlots(Container container){
        return null;
    }

    public Map<Integer, List<Slot>> getSlotsAboveTargetHeight(Slot[][] slotGrid){
        Map<Integer, List<Slot>> slotsTooHigh = new HashMap<>();
        for (int x = 0; x < slotGrid.length; x++) {
            for (int y = 0; y < slotGrid[x].length; y++){
                Slot slot = slotGrid[x][y];
                if (slot.getSlotHeight() > targetHeight) {
                    if(slotsTooHigh.containsKey(slot.getSlotHeight()))
                        slotsTooHigh.put(slot.getSlotHeight(), new ArrayList<>());
                    List<Slot> slotsList = slotsTooHigh.get(slot.getSlotHeight());
                    slotsList.add(slot);
                    slotsTooHigh.put(slot.getSlotHeight(), slotsList);
                }
            }
        }
        return slotsTooHigh;
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
