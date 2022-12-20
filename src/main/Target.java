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
        initialTerminal.executeMovements(moveAssignments);
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
                Assignment initialAssignment = Arrays.stream(initialAssignments).filter(assignment1 -> assignment1.getContainer().getId() == assignment.getContainer().getId()).findFirst().get();
                Slot[] toSlots = new Slot[assignment.getContainer().getLength()];
                for (int i = 0; i < assignment.getContainerSlots().length; i++) {
                    Point slotLocation = assignment.getContainerSlots()[i].getLocation();
                    toSlots[i] = initialTerminal.getSlotGrid()[(int) slotLocation.getX()][(int) slotLocation.getY()];
                }
                movements.add(new Movement(initialAssignment.getContainerSlots(),toSlots, assignment.getContainer(), initialTerminal));
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
                    feasibleLeftSlots.put(container, initialTerminal.getFeasibleLeftSlots(container, 0, initialTerminal.getWidth()));
                }
                while(!feasibleLeftSlots.isEmpty()){
                    Container minPossibleLocationsContainer = Collections.min(feasibleLeftSlots.entrySet(), comparingInt(entry -> entry.getValue().size())).getKey();
                    Slot leftMostSlot = feasibleLeftSlots.get(minPossibleLocationsContainer).get(0);
                    movements.add(new Movement(minPossibleLocationsContainer.getSlots(),
                            initialTerminal.getSlotsFromLeftMostSlot(leftMostSlot, minPossibleLocationsContainer.getLength()), minPossibleLocationsContainer, initialTerminal));
                    feasibleLeftSlots.remove(minPossibleLocationsContainer);
                    for(Container container : feasibleLeftSlots.keySet()){
                        if(feasibleLeftSlots.get(container).contains(leftMostSlot)){
                            if(!initialTerminal.isStackable(container, initialTerminal.getSlotsFromLeftMostSlot(leftMostSlot, container.getLength()), initialTerminal.getTargetHeight())){
                                feasibleLeftSlots.get(container).remove(leftMostSlot);
                            }
                        }
                    }
                }
            }
        }
        return movements;
    }

    private Slot getClosestFeasibleLeftSlot(Container container, List<Slot> feasibleLeftSlots){
        Point initialContainerCenter = initialTerminal.getCenterLocationForCrane(container.getSlots(), container);
        Slot closestSlot = null;
        double minDistance = Double.MAX_VALUE;
        for (Slot slot : feasibleLeftSlots) {
            Point slotLocation = slot.getLocation();
            double distance = initialContainerCenter.distance(slotLocation);
            if (distance < minDistance) {
                minDistance = distance;
                closestSlot = slot;
            }
        }
        return closestSlot;
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
