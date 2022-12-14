package main;
import java.awt.*;
import java.util.Objects;
import java.util.Stack;

public class Slot {
    private int id;
    private Point location;

    public Stack<Container> getContainerStack() {
        return containerStack;
    }

    public void stackContainer(Container container) {
        this.containerStack.push(container);
    }

    private Stack<Container> containerStack;
    public Slot(int id, Point location) {
        this.id = id;
        this.location = location;
        this.containerStack = new Stack<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public int getSlotHeight(){
        return containerStack.size();
    }

    public int getContainerHeight(Container container){
        if(containerStack.contains(container)){
            return containerStack.search(container);
        }
        return -1;
    }

    public Container removeContainerFromSlot(){
        return this.containerStack.pop();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return id == slot.id && Objects.equals(location, slot.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location, containerStack);
    }
}
