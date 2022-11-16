package main;
public class Movement {
    private Slot slotFrom;
    private Slot slotTo;
    private Container container;

    public Movement(Slot slotFrom, Slot slotTo, Container container){
        this.slotFrom = slotFrom;
        this.slotTo = slotTo;
        this.container = container;
    }

    public Slot getSlotFrom() {
        return slotFrom;
    }

    public void setSlotFrom(Slot slotFrom) {
        this.slotFrom = slotFrom;
    }

    public Slot getSlotTo() {
        return slotTo;
    }

    public void setSlotTo(Slot slotTo) {
        this.slotTo = slotTo;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }
}
