package main;
import java.util.*;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(container, that.container) && Arrays.equals(containerSlots, that.containerSlots);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(container);
        result = 31 * result + Arrays.hashCode(containerSlots);
        return result;
    }
}
