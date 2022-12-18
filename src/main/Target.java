package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Target {
    Terminal initialTerminal;
    Terminal finalTerminal;
    private int maxHeight;
    private int targetHeight;
    private List<Movement> movements;

    public Target(Terminal initialTerminal, Terminal finalTerminal) {
        this.initialTerminal = initialTerminal;
        this.finalTerminal = finalTerminal;
        this.maxHeight = initialTerminal.getMaxHeight();
        this.movements = calculateToFinialTerminal();
    }

    public Target(Terminal initialTerminal){
        this.initialTerminal = initialTerminal;
        this.maxHeight = this.initialTerminal.getMaxHeight();
        this.targetHeight = this.initialTerminal.getTargetHeight();
        this.movements = calculateToTargetHeight();
    }

    private List<Movement> calculateToFinialTerminal(){
        List<Movement> movements = new ArrayList<>();
        Assignment[] initialAssignments = initialTerminal.getAssignments();
        Assignment[] finalAssignments = finalTerminal.getAssignments();

        for (Assignment assignment: finalAssignments) {
            if(Arrays.stream(initialAssignments).noneMatch(assignment::equals)){
                Assignment initialAsignment = Arrays.stream(initialAssignments).filter(assignment1 -> assignment1.getContainer().getId() == assignment.getContainer().getId()).findFirst().get();
                movements.add(new Movement(initialAsignment.getContainerSlots(),assignment.getContainerSlots(), assignment.getContainer(), initialTerminal));
            }
        }

        return movements;
    }

    private List<Movement> calculateToTargetHeight(){
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

    public List<Movement> getMovements() {
        return this.movements;
    }

    public void setMoveAssignments(List<Movement> movements) {
        this.movements = movements;
    }
}
