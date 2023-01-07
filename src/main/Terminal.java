package main;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    private double lastMovingtime;

    private Map<Integer , Point> slotLocations;

    public Terminal(String name, List<Container> containers, Map<Integer, Point> slotLocations, Slot[][] slotGrid, Assignment[] assignments, List<Crane> cranes, int maxHeight, int targetHeight, int width, int length) {
        this.name = name;
        this.containers = containers;
        this.slotGrid = slotGrid;
        this.assignments = assignments;
        this.cranes = cranes;
        this.maxHeight = maxHeight;
        this.width = width;
        this.length = length;
        this.targetHeight = targetHeight;
        this.lastMovingtime = 0;
        this.slotLocations = slotLocations;
    }

    public Map<Integer, Point> getSlotLocations() {
        return slotLocations;
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

    public void moveCrane(Crane crane, Point p, double startTime, Container container){

        Set<Double> times = crane.getTrajectory().keySet();
        if(!times.isEmpty() && startTime < Collections.max(times)){
            throw new IllegalArgumentException("Invalid startTime");
        }
        double timex = ((double) Math.abs(p.getX() - crane.getPosition().getX()))/crane.getSpeedX();
        double timey = ((double) Math.abs(p.getY() - crane.getPosition().getY()))/crane.getSpeedY();
        double endTime = startTime + Math.max(timex, timey);

        System.out.println("Move crane with id: "+ crane.getId()+" from "+ crane.getPosition()+ " to location " + p.toString() + " starting at "+ startTime + " ending at " + endTime);
        lastMovingtime = Math.max(endTime, lastMovingtime);

        // Control movement
        List<Crane> collidingCranes = getCollidingCranes(p, crane, 1, startTime, endTime);
        if(collidingCranes.size()>0){
            moveCranesOutTheWay(p,collidingCranes);
            if(getCollidingCranes(p, crane, 1, startTime, endTime).size() > 0)
                throw new IllegalArgumentException("Crane comes to close to the other cranes to move to this point");
        }

        TreeMap<Double, Point> trajectory = crane.getTrajectory();
        trajectory.put(startTime, crane.getPosition());
        crane.setPosition(p);

        // Save trajectory
        trajectory.put(endTime, p);
        crane.setTrajectory(trajectory);
    }

    public void executeMovements(List<Movement> movements) throws Exception {
        assignMovementsToCranes(movements);
        boolean movementsLeft = true;
        double start = System.currentTimeMillis();
        while (movementsLeft) {
            movementsLeft = false;
            for (Crane crane : this.cranes) {
                if (!crane.getAssignedMovements().isEmpty()) {
                    Movement movement = crane.getAssignedMovements().get(0);
                    if(isMovementFeasible(movement, crane)){
                        executeMovement(movement, crane);
                        crane.getAssignedMovements().remove(0);
                    }else{
                        crane.getAssignedMovements().remove(0);
                        crane.addMovement(movement);
                    }
                    movementsLeft = true;
                }
            }
        }
    }

    private boolean isMovementFeasible(Movement movement, Crane crane){
        return isContainerMovableByCrane(movement.getContainer(), crane) && isStackable(movement.getContainer(), movement.getSlotsTo(), this.maxHeight);
    }

    private void executeMovement(Movement movement, Crane crane){
        System.out.println("Move crane with id: "+ crane.getId()+" to location " + movement.getSlotsFrom()[0].getLocation().toString()+ " to get container" + movement.getContainer().getId()+ " and place it at "+ movement.getSlotsTo()[0].getLocation());
        System.out.println("the container is grepable: "+ isContainerMovableByCrane(movement.getContainer(), crane));
        System.out.println("the container is placeable: "+isStackable(movement.getContainer(), movement.getSlotsTo(), this.maxHeight));

        List<Point> craneMovingPoints = generatePointsFromMovement(movement, crane);
        // 1 Verplaats kraan naar container locatie
        moveCrane(crane, craneMovingPoints.get(0), lastMovingtime, null);
        // 2 verwijder container uit locatie (& plaats in de nieuwe)

        // 3 verplaats kraan naar end slot
        moveCrane(crane, craneMovingPoints.get(1), lastMovingtime, movement.getContainer());

        // 4 plaats container op end slot
        try {
            transferContainerToSlots(movement.getContainer(),movement.getSlotsTo(),maxHeight);
        }catch (Exception e){
            System.out.println("Could not place container");
        }

        // 5 als end slot in overlap zone verplaats kraan eruit
        System.out.println("\n");
    }

    private void assignMovementsToCranes(List<Movement> movements) throws Exception {
        for (int i = 0; i < movements.size(); i++) {
            Movement movement = movements.get(i);
            ArrayList<Crane> possibleCranes = getPossibleCranesForMovement(movement);
            if(possibleCranes.size() == 0){
                List<Movement> overhandingMovements = splitIntoMovements(movement);
                for (Movement partialMovement: overhandingMovements) {
                    possibleCranes = getPossibleCranesForMovement(partialMovement);
                    Crane assignedCrane = assignCrane(possibleCranes);
                    assignedCrane.addMovement(partialMovement);
                    System.out.println("Dubble movement for container with id: "+ movement.getContainer().getId());
                }
            }else{
                Crane assignedCrane = assignCrane(possibleCranes);
                assignedCrane.addMovement(movement);
            }
        }
    }

    private double calculateTimeForCraneMovement(Crane crane, Point point, double startTime){
        double timex = (Math.abs(point.getX() - crane.getPosition().getX()))/crane.getSpeedX();
        double timey = (Math.abs(point.getY() - crane.getPosition().getY()))/crane.getSpeedY();

        return startTime + Math.max(timex,timey);
    }

    private List<Movement> splitIntoMovements(Movement movement) throws Exception {
        List<Crane> cranesFrom = getPossibleCranesForMovement(new Movement(movement.getSlotsFrom(), movement.getSlotsFrom(), movement.getContainer(), this));
        Crane fromCrane = cranesFrom.get(0);
        Slot[] destination = getCraneTransitionSlots(fromCrane, movement.getSlotsTo(), movement.getContainer());
        List<Movement> movements = new ArrayList<>();
        Movement partialMovement = new Movement(movement.getSlotsFrom(), destination, movement.getContainer(), this);
        movements.add(partialMovement);

        return giveToNextCrane(movements, movement.getSlotsTo());
    }

    private List<Movement> giveToNextCrane(List<Movement> movements, Slot[] destinationSlots) throws Exception {
        // Get cranes that can get the container
        Movement lastMovement = movements.get(movements.size()-1);
        Slot[] startingPosition = lastMovement.getSlotsTo();
        if(Arrays.equals(startingPosition, destinationSlots)){
            return movements;
        }

        // Assign one that could get (closer) to the end
        List<Crane> possibleCranes = getPossibleCranesForMovement(new Movement(lastMovement.getSlotsTo(), lastMovement.getSlotsTo(), lastMovement.getContainer(), this));
        Crane assignedCrane = possibleCranes.get(0);
        for (Crane possibleCrane:possibleCranes) {
            Crane closestCrane = getCraneClosestToPoint(assignedCrane, possibleCrane, destinationSlots[0].getLocation());
            if(!assignedCrane.equals(closestCrane))
                assignedCrane = closestCrane;
        }

        // Get the slots for movement
        Slot[] dropOffSlots = getCraneTransitionSlots(assignedCrane, destinationSlots, lastMovement.getContainer());
        Movement partialMovement = new Movement(lastMovement.getSlotsTo(), dropOffSlots, lastMovement.getContainer(), this);
        partialMovement.setDependentPrevMovement(lastMovement);
        movements.add(partialMovement);

        return giveToNextCrane(movements, destinationSlots);
    }

    private Slot[] getCraneTransitionSlots(Crane assignedCrane, Slot[] destinationSlots, Container container) throws Exception {
        if(craneHasOverlap(assignedCrane, getCenterLocationForCrane(destinationSlots, container))){
            return destinationSlots;
        }
        List<Crane> cranesWithOverlap = new ArrayList<>();
        // get the zone of possible transition
        for (Crane crane: cranes) {
            if (!crane.equals(assignedCrane)){
                if(getCraneClosestToPoint(crane, assignedCrane, destinationSlots[0].getLocation()).equals(crane) &&
                        (craneHasOverlap(crane, new Point(assignedCrane.getxMax(),0)) ||  craneHasOverlap(crane, new Point(assignedCrane.getxMin(),0)))){
                    cranesWithOverlap.add(crane);
                }
            }
        }
        if(cranesWithOverlap.size() == 0)
            throw new IllegalArgumentException("No crane can get to this location");
        Crane crane = cranesWithOverlap.get(0);
        double xmax = Math.min(crane.getxMax(), assignedCrane.getxMax()) - Math.floor(container.getLength()/2.0);
        double xmin = Math.max(crane.getxMin(), assignedCrane.getxMin()) - Math.floor(container.getLength()/2.0);
        Slot leftMostSlot = getFeasibleLeftSlots(container,(int) Math.floor(xmax),(int) Math.floor(xmin), crane.getAssignedMovements()).get(0);
        return getSlotsFromLeftMostSlot(leftMostSlot, container.getLength());
    }

    private Crane getCraneClosestToPoint(Crane crane1, Crane crane2, Point destination){
        if(craneHasOverlap(crane1, destination))
            return crane1;
        if (craneHasOverlap(crane2, destination))
            return crane2;

        double diff1 = Math.abs(destination.getX() - crane1.getxMin());
        double diff2 = Math.abs(destination.getX() - crane2.getxMin());

        if(diff1 > diff2)
            return crane2;

        return crane1;
    }

    private boolean craneHasOverlap(Crane crane, Point destination){
        if((destination.getX() >= crane.getxMin()) && (destination.getX() <= crane.getxMax())){
            return true;
        }

        return false;
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
            if(assignedCrane == null || assignedCrane.getAssignedMovements().size() > crane.getAssignedMovements().size())
                assignedCrane = crane;
        }

        return assignedCrane;
    }

    private void moveCranesOutTheWay(Point collisionPoint, List<Crane> collidingCranes){
        for (Crane crane: collidingCranes) {

            // If it's closer to the min then the max move to the right
            Point pointToMoveTo = new Point(collisionPoint.getX() + 2 , crane.getPosition().getY());

            // If it's closer to the max then move to the left
            if(crane.getxMax() - collisionPoint.getX() < collisionPoint.getX() - crane.getxMin()){
                pointToMoveTo = new Point(collisionPoint.getX() - 2 , crane.getPosition().getY());
            }

            double earliestMovingTime = 0;
            if(!crane.getTrajectory().isEmpty()){
                earliestMovingTime = Collections.max(crane.getTrajectory().keySet());
            }

            moveCrane(crane,pointToMoveTo,earliestMovingTime, null);
        }
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

    private boolean isPointAccessibleByCrane(Point point, Crane crane){
        return Math.max(crane.getxMax(), point.getX()) == crane.getxMax() && Math.min(point.getX(), crane.getxMin()) == crane.getxMin();
    }

    public Point getCenterLocationForCrane(Slot[] slots, Container container){
        double x = slots[0].getLocation().getX() + container.getLength()/2.0;
        double y = slots[0].getLocation().getY() + 0.5;

        return new Point(x,y);
    }

//    C1:   @t5=(0,8) --> @t9=(7,9)
//    C2: 	@t4=(0,7) --> @t6=(6,4)
//          @t7=(6,4) --> @t9=(5,3)
    private List<Crane> getCollidingCranes(Point destination, Crane movingCrane, int delta, double startTime, double endTime){
        List<Crane> collidingCranes = new ArrayList<>();
        for (Crane crane : cranes) {
            //Check for overlap between movingcrane.position-->destination & other crane-->his destination
            if (crane.getId() != movingCrane.getId()) {
                if(movingCrane.getPosition().getX() < destination.getX()){ // move right
                    if (movingCrane.getPosition().getX() < crane.getPosition().getX() && crane.getPosition().getX() - delta < destination.getX()) // Check if crane comes to close to the other cranes
                        collidingCranes.add(crane);
                }else { // move left
                    if(movingCrane.getPosition().getX() > crane.getPosition().getX()  && destination.getX() < crane.getPosition().getX() + delta)
                        collidingCranes.add(crane);
                }
            }
        }
        return collidingCranes;
    }

    private boolean coordinateIntervalsOverlap(Point begin1, Point end1, Point begin2, Point end2, int delta){
        return begin1.getX() + delta < end2.getX() && begin2.getX() + delta < end1.getX();
    }

    public List<Slot> getFeasibleLeftSlots(Container container,int xMax, int xMin, List<Movement> ambetanteMovements) throws Exception {
        List<Slot> feasibleLeftSlots = new ArrayList<>();
        List<Slot> allInterferingSlots = ambetanteMovements.stream().map(Movement::getSlotsTo).flatMap(Stream::of).toList();
        for (int x = xMin; x < xMax; x++) {
            for (int y = 0; y < slotGrid[x].length; y++){
                Slot slot = slotGrid[x][y];
                if((slot.getLocation().getX() + container.getLength()) <= length
                        &&
                        isStackable(container, getSlotsFromLeftMostSlot(slot, container.getLength()), targetHeight)
                && !allInterferingSlots.contains(slot)){
                    feasibleLeftSlots.add(slot);
                }
            }
        }
        if(feasibleLeftSlots.size() == 0){
            throw new Exception();
        }
        return feasibleLeftSlots;
    }

    public Slot[] getSlotsFromLeftMostSlot(Slot leftMostSlot, int length){
        Slot[] slots = new Slot[length];
        slots[0] = slotGrid[(int) leftMostSlot.getLocation().getX()][(int) leftMostSlot.getLocation().getY()];
        for(int i = 1; i < length; i++){
            slots[i] = slotGrid[(int) leftMostSlot.getLocation().getX()+i][(int) leftMostSlot.getLocation().getY()];
        }
        return slots;
    }

    private TreeMap<Double, Point> getOverlappingTrajectoryTimes(double startTime, double endTime, Crane crane){
        TreeMap<Double, Point> overlappingTrajectory = new TreeMap<>();
        for(Double timeStart : crane.getTrajectory().keySet()){
            if(crane.getTrajectory().higherKey(timeStart) != null){
                Double timeEnd = crane.getTrajectory().higherKey(timeStart);
                if(startTime < timeEnd && timeStart < endTime){
                    Point start = crane.getTrajectory().get(timeStart);
                    Point end = crane.getTrajectory().get(timeEnd);
                    if(crane.getSpeedX() > (end.getX() - start.getX()) / (timeEnd - timeStart)){
                        double timeEffectiveStart = timeEnd - (end.getX()-start.getX())/crane.getSpeedX();
                        overlappingTrajectory.put(timeStart, crane.getTrajectory().get(timeStart));
                        overlappingTrajectory.put(timeEffectiveStart, crane.getTrajectory().get(timeStart));
                        overlappingTrajectory.put(timeEnd, crane.getTrajectory().get(timeEnd));
                    } else {
                        overlappingTrajectory.put(timeStart, crane.getTrajectory().get(timeStart));
                        overlappingTrajectory.put(timeEnd, crane.getTrajectory().get(timeEnd));
                    }
                }
            }
        }
        return overlappingTrajectory;
    }

    public void transferContainerToSlots(Container container, Slot[] slots, int maxHeight) throws Exception {
        if(slots.length != container.getLength()){
            throw new Exception();
        }
        if(isStackable(container, slots, maxHeight) && isContainerMovable(container)){
            for (Slot slot: container.getSlots()){
                slot.removeContainerFromSlot();
            }
            container.setSlots(slots);
            for(Slot slot : slots){
                slot.stackContainer(container);
            }
        }else{
            throw new Exception();
        }
    }

    public void initializeSlots(Container container, Slot[] slots) {
        for(Slot slot : slots){
            slot.stackContainer(container);
        }
    }

    public boolean isContainerMovableByCrane(Container container, Crane crane){
        Slot[] containerSlots = container.getSlots();

        boolean isMovable = isPointAccessibleByCrane(getCenterLocationForCrane(containerSlots, container), crane);
        return isMovable && isContainerMovable(container);
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

    public boolean isContainerInSlots(Container container, Slot[] slots){
        boolean containerSlottedCorrectly = true;
        for(Slot slot : slots){
            if(!slot.getContainerStack().contains(container))
                return false;
        }
        return true;
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
