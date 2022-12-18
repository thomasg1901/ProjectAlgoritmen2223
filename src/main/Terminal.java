package main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Terminal {
    private String name;
    private List<Container> containers;

    private Assignment[] assignments;

    private Slot[][] slotGrid;
    private List<Crane> cranes;
    private final int maxHeight;

    private int targetHeight;

    private final int width;

    private final int length;

    public Terminal(String name, List<Container> containers, Slot[][] slotGrid, Assignment[] assignments, List<Crane> cranes, int maxHeight, int targetHeight, int width, int length) {
        this.name = name;
        this.containers = containers;
        this.slotGrid = slotGrid;
        this.assignments = assignments;
        this.cranes = cranes;
        this.maxHeight = maxHeight;
        this.width = width;
        this.length = length;
        this.targetHeight = targetHeight;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public Assignment[] getAssignments() {
        return assignments;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public Slot[][] getSlotGrid() {
        return slotGrid;
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
        double timex = ((double) Math.abs(p.getX() - crane.getPosition().getX()))/crane.getSpeedx();
        double timey = ((double) Math.abs(p.getY() - crane.getPosition().getY()))/crane.getSpeedy();

        // Control movement
        if(willCollideWithOtherCranes(p, crane, 0))
            throw new IllegalArgumentException("Crane comes to close to the other cranes to move to this point");

        crane.setPosition(p);

        // Save trajectory
        HashMap<Double, Point> trajectory = crane.getTrajectory();
        trajectory.put(startTime + Math.max(timex,timey), p);
        crane.setTrajectory(trajectory);
    }

    public void executeMovements(List<Movement> movements){
        for (Movement movement : movements) {


            ArrayList<Crane> possibleCranes = getPossibleCranesForMovement(movement);
            Crane assignedCrane = assignCrane(possibleCranes);


            // generate points to move the crane
            List<Point> movementPoints = generatePointsFromMovement(movement, assignedCrane);
            System.out.println(possibleCranes.size());
            System.out.println(isStackable(movement.getContainer(),movement.getSlotsTo(),this.maxHeight));

            for (Point p : movementPoints) {
                System.out.println(!willCollideWithOtherCranes(p,assignedCrane,1));
                if(assignedCrane.getTrajectory().isEmpty())
                    moveCrane(assignedCrane, p, 0);
                else
                    moveCrane(assignedCrane, p, Collections.max(assignedCrane.getTrajectory().keySet()));
            }
        }
    }

    private List<Point> generatePointsFromMovement(Movement movement, Crane crane){
        ArrayList<Point> moveCraneTo = new ArrayList<>();
        moveCraneTo.add(getCenterLocationForCrane(movement.getSlotsFrom(), movement.getContainer()));
        moveCraneTo.add(getCenterLocationForCrane(movement.getSlotsTo(), movement.getContainer()));

        return moveCraneTo;
    }

    private Crane assignCrane(List<Crane> cranes){
        Crane assignedCrane = null;
        for (Crane crane : cranes) {
            if(assignedCrane == null || assignedCrane.getTrajectory().size() > crane.getTrajectory().size())
                assignedCrane = crane;
        }

        return assignedCrane;
    }

    private ArrayList<Crane> getPossibleCranesForMovement(Movement movement){
        ArrayList<Crane> cranes = new ArrayList<>();
        for (Crane crane: this.cranes) {
            Point from = getCenterLocationForCrane(movement.getSlotsFrom(), movement.getContainer());
            Point to = getCenterLocationForCrane(movement.getSlotsTo(), movement.getContainer());

            double minX = Math.min(from.getX(), to.getX());
            double maxX = Math.max(from.getX(), to.getX());

            if (Math.max(crane.getxMax(), maxX) == crane.getxMax() && Math.min(minX, crane.getxMin()) == crane.getxMin()){
                cranes.add(crane);
            }
        }

        return cranes;
    }

    private Point getCenterLocationForCrane(Slot[] slots, Container container){
        double x = slots[0].getLocation().getX() + container.getLength()/2.0;
        double y = slots[0].getLocation().getY() + 0.5;

        return new Point(x,y);
    }

    private boolean willCollideWithOtherCranes(Point desitnation, Crane movingCrane, int delta){
        for (Crane crane : cranes) {
            if (crane.getId() == movingCrane.getId()) {
                if(movingCrane.getPosition().getX() < desitnation.getX()){ // move right
                    if (movingCrane.getPosition().getX() < crane.getPosition().getX() && crane.getPosition().getX() + delta < desitnation.getX()) // Check if crane comes to close to the other cranes
                        return true;
                }else { // move left
                    if(movingCrane.getPosition().getX() > crane.getPosition().getX()  && desitnation.getX() < crane.getPosition().getX() - delta)
                        return true;
                }

            }
        }
        return false;
    }

    public void putContainerInSlots(Container container, Slot[] slots, int maxHeight) throws Exception {
        if(slots.length != container.getLength()){
            throw new Exception();
        }
        if(isStackable(container, slots, maxHeight)){
            container.setSlots(slots);
            for(Slot slot : slots){
                slot.stackContainer(container);
            }
        }
    }

    public boolean isContainerMovable(Container container){
        Slot[] containerSlots = container.getSlots();
        boolean isMovable = true;
        for(Slot containerSlot : containerSlots){
            isMovable = isMovable && containerSlot.getContainerStack().peek() == container;
        }
        return isMovable;
    }


    public boolean isStackable(Container container, Slot[] slots, int maxHeight){
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

    public void setSlotGrid(Slot[][] slotGrid) {
        this.slotGrid = slotGrid;
    }
}
