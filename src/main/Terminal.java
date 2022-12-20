package main;
import java.util.*;


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
        this.lastMovingtime = 0;
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
        Set<Double> times = crane.getTrajectory().keySet();
        if(!times.isEmpty() && startTime < Collections.max(times)){
            throw new IllegalArgumentException("Invalid startTime");
        }
        double timex = ((double) Math.abs(p.getX() - crane.getPosition().getX()))/crane.getSpeedX();
        double timey = ((double) Math.abs(p.getY() - crane.getPosition().getY()))/crane.getSpeedY();
        double endTime = startTime + Math.max(timex, timey);


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

    public void executeMovements(List<Movement> movements){
        assignMovementsToCranes(movements);
        boolean movementsLeft = true;
        while (movementsLeft) {
            movementsLeft = false;
            for (Crane crane : this.cranes) {
                if (!crane.getAssignedMovements().isEmpty()) {
                    Movement movement = crane.getAssignedMovements().get(0);
                    if(isMovementFeasible(movement, crane)){
                        executeMovement(movement, crane);
                        crane.getAssignedMovements().remove(0);
                        movementsLeft = true;

                    }else{
                        crane.getAssignedMovements().remove(0);
                        crane.addMovement(movement);
                    }
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
        moveCrane(crane, craneMovingPoints.get(0), lastMovingtime);
        // 2 verwijder container uit locatie (& plaats in de nieuwe)
        try {
            putContainerInSlots(movement.getContainer(),movement.getSlotsTo(),maxHeight);
        }catch (Exception e){
            System.out.println("Could not place container");
        }

        moveCrane(crane, craneMovingPoints.get(1), lastMovingtime);
        // 3 verplaats kraan naar end slot

        // 5 als end slot in overlap zone verplaats kraan eruit

    }

    private void assignMovementsToCranes(List<Movement> movements){
        for (int i = 0; i < movements.size(); i++) {
            Movement movement = movements.get(i);
            ArrayList<Crane> possibleCranes = getPossibleCranesForMovement(movement);
            if(possibleCranes.size() == 0){
                List<Movement> overhandingMovements = splitIntoMovements(movement);
                for (Movement partialMovement: overhandingMovements) {
                    possibleCranes = getPossibleCranesForMovement(partialMovement);
                    Crane assignedCrane = assignCrane(possibleCranes);
                    assignedCrane.addMovement(partialMovement);
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

    private List<Movement> splitIntoMovements(Movement movement) {
        List<Crane> cranesFrom = getPossibleCranesForMovement(new Movement(movement.getSlotsFrom(), movement.getSlotsFrom(), movement.getContainer(), this));
        Crane fromCrane = cranesFrom.get(0);
        Slot[] destination = getCraneTransitionSlots(fromCrane, movement.getSlotsTo(), movement.getContainer());
        List<Movement> movements = new ArrayList<>();
        movements.add(new Movement(movement.getSlotsFrom(), destination, movement.getContainer(), this));

        return giveToNextCrane(movements, movement.getSlotsTo());
    }

    private List<Movement> giveToNextCrane(List<Movement> movements, Slot[] destinationSlots){
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
        movements.add(new Movement(lastMovement.getSlotsTo(), dropOffSlots, lastMovement.getContainer(), this));

        return giveToNextCrane(movements, destinationSlots);
    }

    private Slot[] getCraneTransitionSlots(Crane assignedCrane, Slot[] destinationSlots, Container container){
        if(craneHasOverlap(assignedCrane, destinationSlots[0].getLocation())){
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
        double xmin = Math.min(crane.getxMax(), assignedCrane.getxMax());
        double xmax = Math.max(crane.getxMin(), assignedCrane.getxMin());

        Slot leftMostSlot = getFeasibleLeftSlots(container,(int) Math.floor(xmin),(int) Math.ceil(xmax)).get(0);
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
            Point pointToMoveTo = new Point(collisionPoint.getX() + 2 , crane.getPosition().getY());
            if(collisionPoint.getX() > crane.getxMax()){ // kraan is links van het punt
                pointToMoveTo = new Point(collisionPoint.getX() - 2 , crane.getPosition().getY());
            }

            double earliestMovingTime = 0;
            if(!crane.getTrajectory().isEmpty()){
                earliestMovingTime = Collections.max(crane.getTrajectory().keySet());
            }

            moveCrane(crane,pointToMoveTo,earliestMovingTime);
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
//                TreeMap<Double, Point> overlaps = getOverlappingTrajectoryTimes(startTime, endTime, crane);
//                if(!overlaps.isEmpty()){
//                    if(isTravellingColliding(destination, movingCrane, crane, overlaps, startTime, endTime, delta)){
//                        collidingCranes.add(crane);
//                    }
//                }
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
/*
    public boolean isTravellingColliding(Point destinationMovingCrane,
                                      Crane movingCrane,
                                      Crane collisionCrane,
                                      TreeMap<Double, Point> overlappingTrajectory,
                                      double startTimeMoving,
                                      double endTimeMoving,
                                      int delta){
        Point startMovingCrane = movingCrane.getPosition();
        for(Double startTime : overlappingTrajectory.keySet()){
            Point start = overlappingTrajectory.get(startTime);
            Double endTime = overlappingTrajectory.higherKey(startTime);
            Point movingCraneStart = new Point(startMovingCrane.getX(), startMovingCrane.getY());
            Point movingCraneDest = new Point(destinationMovingCrane.getX(), destinationMovingCrane.getY());
            Point end = overlappingTrajectory.get(endTime);
            // Possibility 1: start & end of overlap are between interval --> boundaries interval change
            if(startTime > startTimeMoving && endTime < endTimeMoving){
                movingCraneStart.setX(movingCrane.getSpeedX() * (startTime - startTimeMoving) + startMovingCrane.getX());
                movingCraneDest.setX(destinationMovingCrane.getX() - movingCrane.getSpeedX() * (endTimeMoving - endTime));
            } // Possibility 2: start & end of overlap are at beginning of interval --> begin time overlap change
            else if (startTime < startTimeMoving && endTime < endTimeMoving){
                start.setX(collisionCrane.getSpeedX() * (startTimeMoving - startTime) + start.getX());
                movingCraneDest.setX(destinationMovingCrane.getX() - collisionCrane.getSpeedX() * (endTimeMoving - endTime));
            }// Possibility 3: start & end of overlap are at the end of interval --> end time overlap change
            else if (startTime > startTimeMoving && endTime > endTimeMoving){
                movingCraneStart.setX(movingCrane.getSpeedX() * (startTime - startTimeMoving) + startMovingCrane.getX());
                end.setX(end.getX() - collisionCrane.getSpeedX() * (endTime - endTimeMoving));
            }// Possibility 4: start & end of overlap contain the interval --> boundaries overlap change
            else if (startTime < startTimeMoving && endTime > endTimeMoving){
                start.setX(collisionCrane.getSpeedX() * (startTimeMoving - startTime) + start.getX());
                end.setX(end.getX() - collisionCrane.getSpeedX() * (endTime - endTimeMoving));
            }
            if(coordinateIntervalsOverlap(start, end, movingCraneStart, movingCraneDest, delta)){
                return true;
            }
        }
        return false;
    }
*/
    private boolean coordinateIntervalsOverlap(Point begin1, Point end1, Point begin2, Point end2, int delta){
        return begin1.getX() + delta < end2.getX() && begin2.getX() + delta < end1.getX();
    }

    public List<Slot> getFeasibleLeftSlots(Container container,int xMax, int xMin){
        List<Slot> feasibleLeftSlots = new ArrayList<>();
        for (int x = xMin; x < xMax; x++) {
            for (int y = 0; y < slotGrid[x].length; y++){
                Slot slot = slotGrid[x][y];
                if((slot.getLocation().getX() + container.getLength()) <= length
                        &&
                        isStackable(container, getSlotsFromLeftMostSlot(slot, container.getLength()), targetHeight)){
                    feasibleLeftSlots.add(slot);
                }
            }
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
        for(Double startKey : crane.getTrajectory().keySet()){
            if(crane.getTrajectory().higherKey(startKey) != null){
                Double endKey = crane.getTrajectory().higherKey(startKey);
                if(startTime < endKey && startKey < endTime){
                    overlappingTrajectory.put(startKey, crane.getTrajectory().get(startKey));
                    overlappingTrajectory.put(endKey, crane.getTrajectory().get(endKey));
                }
            }
        }
        return overlappingTrajectory;
    }

    public void putContainerInSlots(Container container, Slot[] slots, int maxHeight) throws Exception {
        if(slots.length != container.getLength()){
            throw new Exception();
        }
        if(isStackable(container, slots, maxHeight) && isContainerMovable(container)){
            container.setSlots(slots);
            for(Slot slot : slots){
                slot.stackContainer(container);
            }
        }else{
            throw new Exception();
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
