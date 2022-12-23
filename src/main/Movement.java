package main;

import java.util.Arrays;
import java.util.Objects;

public class Movement implements Comparable{
    private Slot[] slotsFrom;
    private Slot[] slotsTo;
    private Container container;

    private double startTime;
    private double endTime;

    private double releaseDate;

    private Movement dependentPrevMovement;

    private final Terminal terminal;

    public Movement(Slot[] slotsFrom, Slot[] slotsTo, Container container, Terminal terminal){
        this.slotsFrom = slotsFrom;
        this.slotsTo = slotsTo;
        this.container = container;
        this.terminal = terminal;
        this.releaseDate = 0;
        this.startTime = -1;
        this.endTime = -1;
        this.dependentPrevMovement = null;
    }

    public void setDependentPrevMovement(Movement dependentPrevMovement) {
        this.dependentPrevMovement = dependentPrevMovement;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public double getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(double releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Slot[] getSlotsFrom() {
        return slotsFrom;
    }

    public Slot[] getSlotsTo() {
        return slotsTo;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public boolean movementFeasible(){
        // Check if container is on top of stack at the start slot
        if(!this.slotsFrom[0].getContainerStack().peek().equals(container)){
            return false;
        }

        // Check if container can be put on top of the destination slot
        if(!terminal.isStackable(container, slotsTo, terminal.getMaxHeight())){
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(Object o) {
        Movement m = (Movement)o;
        return this.getContainer().getLength() - m.getContainer().getLength();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement movement = (Movement) o;
        return Double.compare(movement.startTime, startTime) == 0 && Double.compare(movement.endTime, endTime) == 0 && Double.compare(movement.releaseDate, releaseDate) == 0 && Arrays.equals(slotsFrom, movement.slotsFrom) && Arrays.equals(slotsTo, movement.slotsTo) && container.equals(movement.container) && Objects.equals(dependentPrevMovement, movement.dependentPrevMovement) && terminal.equals(movement.terminal);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(container, startTime, endTime, releaseDate, dependentPrevMovement, terminal);
        result = 31 * result + Arrays.hashCode(slotsFrom);
        result = 31 * result + Arrays.hashCode(slotsTo);
        return result;
    }
}
