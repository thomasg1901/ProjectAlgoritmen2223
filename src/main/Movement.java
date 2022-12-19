package main;
public class Movement {
    private Slot[] slotsFrom;
    private Slot[] slotsTo;
    private Container container;

    private double releaseDate;

    private final Terminal terminal;

    public Movement(Slot[] slotsFrom, Slot[] slotsTo, Container container, Terminal terminal){
        this.slotsFrom = slotsFrom;
        this.slotsTo = slotsTo;
        this.container = container;
        this.terminal = terminal;
        this.releaseDate = 0;
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
}
