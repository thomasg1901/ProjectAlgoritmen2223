package main;
import java.util.List;
import java.util.Map;

public class Assignment implements Comparable {
    private Container container;
    private List<Slot> containerSlots;

    public Assignment(Container container, List<Slot> containerSlots) {
        this.container = container;
        this.containerSlots = containerSlots;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public List<Slot> getContainerSlots() {
        return containerSlots;
    }

    public void setContainerSlots(List<Slot> containerSlots) {
        this.containerSlots = containerSlots;
    }

    @Override
    public int compareTo(Object o) {
        return ((Assignment)o).getContainer().getLength() - this.getContainer().getLength();
    }
}
