package main;

import java.util.*;
import java.util.Map.Entry;
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
    Assignment[] targetAssignments;
    private int maxHeight;
    private int targetHeight;
    private List<Movement> moveAssignments;

    public Target(Terminal initialTerminal, Assignment[] targetAssignments) throws Exception {
        this.initialTerminal = initialTerminal;
        this.targetAssignments = targetAssignments;
        this.maxHeight = initialTerminal.getMaxHeight();
        this.moveAssignments = calculateToFinalTerminal();
        initialTerminal.executeMovements(moveAssignments);
        System.out.println(validateFinalTerminal());
    }

    public Target(Terminal initialTerminal) throws Exception {
        this.initialTerminal = initialTerminal;
        this.maxHeight = this.initialTerminal.getMaxHeight();
        this.targetHeight = this.initialTerminal.getTargetHeight();
        this.moveAssignments = calculateToTargetHeight();
        initialTerminal.executeMovements(moveAssignments);
        System.out.println(validateConversionTerminal());
    }

    private boolean validateFinalTerminal(){
        for(Assignment assignment: this.targetAssignments){
            if (!initialTerminal.isContainerInSlots(assignment.getContainer(), assignment.getContainerSlots())){
                return false;
            }
        }
        return true;
    }

    private boolean validateConversionTerminal(){
        for (int x = 0; x < initialTerminal.getSlotGrid().length; x++) {
            for (int y = 0; y < initialTerminal.getSlotGrid()[x].length; y++){
                Slot slot = initialTerminal.getSlotGrid()[x][y];
                if (slot.getSlotHeight() > targetHeight) {
                    return false;
                }
            }
        }
        return true;
    }



    private List<Movement> calculateToFinalTerminal(){
        List<Movement> movements = new ArrayList<>();
        Assignment[] initialAssignments = initialTerminal.getAssignments();
        Assignment[] finalAssignments = targetAssignments;
        for (int j = finalAssignments.length-1; j >= 0; j--) {
            Assignment assignment = finalAssignments[j];
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
        for (Assignment assignment: finalAssignments) {

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
                    try {
                        feasibleLeftSlots.put(container, initialTerminal.getFeasibleLeftSlots(container, initialTerminal.getWidth(), 0, new ArrayList<>(), targetHeight));
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                Set<Slot> allSlotCandidates = feasibleLeftSlots.values().stream().flatMap(List::stream).collect(Collectors.toSet());
                Map<Slot, Integer> candidatePossibilities = new HashMap<>();
                for(Slot slotCandidate : allSlotCandidates){
                    candidatePossibilities.put(slotCandidate, targetHeight-slotCandidate.getSlotHeight());
                }
                while(!feasibleLeftSlots.isEmpty()){
                    Map<Container, List<Slot>> containerSlotCandidates = findSlotWithLeastAmountOfOccurences(feasibleLeftSlots);
                    Container minPossibleLocationsContainer = Collections.min(containerSlotCandidates.entrySet(), comparingInt(entry -> entry.getValue().size())).getKey();
                    Slot leftMostSlot = getClosestFeasibleLeftSlot(minPossibleLocationsContainer, containerSlotCandidates.get(minPossibleLocationsContainer));
                    getClosestFeasibleLeftSlot(minPossibleLocationsContainer, containerSlotCandidates.get(minPossibleLocationsContainer));

                    movements.add(new Movement(minPossibleLocationsContainer.getSlots(),
                            initialTerminal.getSlotsFromLeftMostSlot(leftMostSlot, minPossibleLocationsContainer.getLength()), minPossibleLocationsContainer, initialTerminal));
                    feasibleLeftSlots.remove(minPossibleLocationsContainer);
                    candidatePossibilities.put(leftMostSlot, candidatePossibilities.get(leftMostSlot)-1);
                    for(Container container : feasibleLeftSlots.keySet()){
                        if(feasibleLeftSlots.get(container).contains(leftMostSlot) && candidatePossibilities.get(leftMostSlot) == 0){
                            feasibleLeftSlots.get(container).remove(leftMostSlot);
                        }
                    }
                }
            }
        }
        return movements;
    }

    private Map<Container, List<Slot>> findSlotWithLeastAmountOfOccurences(Map<Container, List<Slot>> feasibleLeftSlots){
        List<Slot> allSlotCandidates = feasibleLeftSlots.values().stream().flatMap(List::stream).collect(Collectors.toList());
        Map<Slot, Integer> slotOccurences = new HashMap<>();
        for(Slot slotCandidate : allSlotCandidates){
            if(!slotOccurences.containsKey(slotCandidate))
                slotOccurences.put(slotCandidate, 1);
            else
                slotOccurences.put(slotCandidate, slotOccurences.get(slotCandidate)+1);
        }
        int minValue = slotOccurences.values().stream().min(Comparator.naturalOrder()).get();
        List<Slot> possibleCandidates =  slotOccurences.entrySet().stream()
                .filter(e -> e.getValue().equals(minValue))
                .map(Entry::getKey)
                .collect(Collectors.toList());
        Map<Container, List<Slot>> containerCandidates = new LinkedHashMap<>();
        LinkedHashMap<Container, List<Slot>> filteredHashMap = feasibleLeftSlots.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(List::size)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for(Container container : filteredHashMap.keySet()){
            if(feasibleLeftSlots.get(container).stream().anyMatch(possibleCandidates::contains)){
                Slot slot = feasibleLeftSlots.get(container).stream().filter(o -> possibleCandidates.contains(o)).findFirst().get();
                if(!containerCandidates.containsKey(container))
                    containerCandidates.put(container, new ArrayList<>());
                List<Slot> slots = containerCandidates.get(container);
                slots.add(slot);
                containerCandidates.put(container, slots);
                return containerCandidates;
            }
        }
        return containerCandidates;
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
