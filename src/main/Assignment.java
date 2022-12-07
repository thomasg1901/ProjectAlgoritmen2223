package main;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Assignment implements Comparable {
    private Container container;
    private Slot[] containerSlots;

    public Assignment(Container container, Slot[] containerSlots) {
        this.container = container;
        this.container.getLength();
        this.containerSlots = containerSlots;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public Slot[] getContainerSlots() {
        return containerSlots;
    }

    public void setContainerSlots(Slot[] containerSlots) {
        this.containerSlots = containerSlots;
    }

    @Override
    public int compareTo(Object o) {
        return ((Assignment)o).getContainer().getLength() - this.getContainer().getLength();
    }
}
