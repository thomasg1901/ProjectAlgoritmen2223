package main;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingInt;
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
    private List<Movement> moveAssignments;

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

    private List<Movement> calculateToFinalTerminal(){
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
        List<Movement> movements = new ArrayList<>();
        if(maxHeight > targetHeight){
            for(int i = maxHeight; i > targetHeight; i--){
                Set<Container> containers = getContainersAtHeight(initialTerminal.getSlotGrid(), i);
                Map<Container, List<Slot>> feasibleLeftSlots = new HashMap<>();
                for(Container container : containers){
                    feasibleLeftSlots.put(container, getFeasibleLeftSlots(initialTerminal.getSlotGrid(), container));
                }
                while(!feasibleLeftSlots.isEmpty()){
                    Container minPossibleLocationsContainer = Collections.min(feasibleLeftSlots.entrySet(), comparingInt(entry -> entry.getValue().size())).getKey();
                    Slot leftMostSlot = feasibleLeftSlots.get(minPossibleLocationsContainer).get(0);
                    movements.add(new Movement(minPossibleLocationsContainer.getSlots(),
                            getSlotsFromLeftMostSlot(leftMostSlot, initialTerminal.getSlotGrid(), minPossibleLocationsContainer.getLength()), minPossibleLocationsContainer, initialTerminal));
                    feasibleLeftSlots.remove(minPossibleLocationsContainer);
                    for(Container container : feasibleLeftSlots.keySet()){
                        if(feasibleLeftSlots.get(container).contains(leftMostSlot)){
                            // TODO - keep feasible slot if it is still stackable on that slot
                        }
                        feasibleLeftSlots.get(container).remove(leftMostSlot);
                    }
                }
            }
        }
        return movements;
    }

    public Slot[] getSlotsFromLeftMostSlot(Slot leftMostSlot, Slot[][] slotGrid, int length){
        Slot[] slots = new Slot[length];
        slots[0] = slotGrid[(int) leftMostSlot.getLocation().getX()][(int) leftMostSlot.getLocation().getY()];
        for(int i = 1; i < length; i++){
            slots[i] = slotGrid[(int) leftMostSlot.getLocation().getX()+i][(int) leftMostSlot.getLocation().getY()];
        }
        return slots;
    }

    public List<Slot> getFeasibleLeftSlots(Slot[][] slotGrid, Container container){
        List<Slot> feasibleLeftSlots = new ArrayList<>();
        for (int x = 0; x < slotGrid.length; x++) {
            for (int y = 0; y < slotGrid[x].length; y++){
                Slot slot = slotGrid[x][y];
                if((slot.getLocation().getX() + container.getLength()) <= initialTerminal.getLength()
                        &&
                        initialTerminal.isStackable(container, getSlotsFromLeftMostSlot(slot, initialTerminal.getSlotGrid(), container.getLength()), initialTerminal.getTargetHeight())){
                    feasibleLeftSlots.add(slot);
                }
            }
        }
        return feasibleLeftSlots;
    }

    public Set<Container> getContainersAtHeight(Slot[][] slotGrid, int height){
        Set<Container> containersAtHeight = new HashSet<>();
        for (int x = 0; x < slotGrid.length; x++) {
            for (int y = 0; y < slotGrid[x].length; y++){
                Slot slot = slotGrid[x][y];
                if (slot.getSlotHeight() == height) {
                    containersAtHeight.add(slot.getContainerStack().peek());
                }
            }
        }
        return containersAtHeight;
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

    public List<Movement> getMoveAssignments() {
        return moveAssignments;
    }

    public void setMoveAssignments(List<Movement> moveAssignments) {
        this.moveAssignments = moveAssignments;
    }
}
