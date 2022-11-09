package main;
import java.awt.*;
import java.util.Stack;

public class Slot {
    private int id;
    private Point location;

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

    public int getContainerHeight(Container container){
        if(containerStack.contains(container)){
            return containerStack.search(container);
        }
        return -1;
    }

    public void putContainerInSlot(Container container){
        this.containerStack.push(container);
    }

    public Container removeContainerFromSlot(){
        return this.containerStack.pop();
    }
}
