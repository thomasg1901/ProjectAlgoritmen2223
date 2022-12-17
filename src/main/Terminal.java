package main;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Terminal {
    private String name;
    private List<Container> containers;

    private Slot[] slots;
    private List<Crane> cranes;
    private final int maxHeight;

    private int targetHeight;

    private final int width;

    private final int length;


    public Terminal(String name, List<Container> containers, Slot[] slots, List<Crane> cranes, int maxHeight, int width, int length) {
        this.name = name;
        this.containers = containers;
        this.slots = slots;
        this.cranes = cranes;
        this.maxHeight = maxHeight;
        this.width = width;
        this.length = length;
        this.targetHeight = maxHeight;
    }

    public Terminal(String name, List<Container> containers, Slot[] slots, List<Crane> cranes, int maxHeight, int targetHeight, int width, int length) {
        this.name = name;
        this.containers = containers;
        this.slots = slots;
        this.cranes = cranes;
        this.maxHeight = maxHeight;
        this.width = width;
        this.length = length;
        this.targetHeight = targetHeight;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public Slot[] getSlots() {
        return slots;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void addContainer(Container container) {
        containers.add(container);
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public void moveCrane(Crane crane, Point p, double startTime){
        double timex = ((double)(p.getX() - crane.getPosition().getX()))/crane.getSpeedx();
        double timey = ((double) (p.getY() - crane.getPosition().getY()))/crane.getSpeedy();

        // Control movement
        if(controlCollision(p, crane, 0))
            throw new IllegalArgumentException("Crane comes to close to the other cranes to move to this point");

        crane.setPosition(p);

        // Save trajectory
        HashMap<Double, Point> trajectory = crane.getTrajectory();
        trajectory.put(startTime + timex + timey, p);
        crane.setTrajectory(trajectory);
    }

    private boolean controlCollision(Point p, Crane c, int delta){
        for (Crane crane: cranes) {
            if (crane.getId() != c.getId()){
                if(c.getPosition().getX() < p.getX()) // Crane moves left
                    if (c.getPosition().getX() < crane.getPosition().getX() + delta) // Check if crane comes to close to the other cranes
                        return true;
                    else                        // Crane moves right
                        if(c.getPosition().getX() > crane.getPosition().getX() - delta) // Check if crane comes to close to the other cranes
                            return true;
            }
        }
        return false;
    }

    private boolean controlSafety(HashMap<Double, Point> trajectory, HashMap<Double, Point> trajectory2, int delta){
        // Check if the trajectories come closer than allowed delta

        return false;
    }

    public void putContainerInSlots(Container container, Slot[] slots) throws Exception {
        if(slots.length != container.getLength()){
            throw new Exception();
        }
        if(isStackable(container, slots)){
            container.setSlots(slots);
            for(Slot slot : slots){
                slot.stackContainer(container);
            }
        }
    }


    public boolean isStackable(Container container, Slot[] slots){
        // Check 1: verify if all container units are on the same height
        boolean allSame = true;
        int firstHeight = slots[0].getContainerStack().size();
        for (Slot slot : slots) {
            allSame = allSame && slot.getContainerStack().size() == firstHeight;
        }
        if(!allSame) return false;

        // Check 2: check if slots are adjacent
        boolean isAdjacent = true;
        for(int i = 0; i+1 < slots.length; i++){
            isAdjacent = isAdjacent
                    && (Math.abs(slots[i+1].getLocation().getY() - slots[i].getLocation().getY())
                    + Math.abs(slots[i+1].getLocation().getX() - slots[i].getLocation().getX())) == 1;
        }
        if(!isAdjacent) return false;

        // Check 3: check if max height is not exceeded
        if (slots[0].getContainerStack().size() + 1 > maxHeight) {
            return false;
        }

        // Check 4: Verify top-down alignment
        boolean isStackedOnSmaller = true;
        for(Slot slot: slots){
            isStackedOnSmaller = isStackedOnSmaller && slot.getContainerStack().size() == 0 || slot.getContainerStack().peek().getLength() <= container.getLength();
        }

        return isStackedOnSmaller;
    }

    public List<Crane> getCranes() {
        return cranes;
    }

    public void setCranes(List<Crane> cranes) {
        this.cranes = cranes;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    public void setSlots(Slot[] slots) {
        this.slots = slots;
    }

    public void convertTerminal(){
        if(maxHeight != targetHeight){
            List<Slot> slots = getSlotsAboveMaxHeight();
            System.out.println("test");
        }
    }

    public List<Slot> getFeasibleLeftSlots(){

    }

    public List<Slot> getSlotsAboveMaxHeight(){
        List<Slot> slotsTooHigh = new ArrayList<>();
        for(int i = 0; i < slots.length; i++){
            if(slots[i].getSlotHeight() > targetHeight){
                slotsTooHigh.add(slots[i]);
            }
        }
        return slotsTooHigh;
    }
}
