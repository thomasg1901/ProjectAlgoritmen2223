package main;
public class Container {
    private int id;
    private int length;
    private Slot[] slots;

    public Container(int id, int length) {
        this.id = id;
        this.length = length;
        this.slots = new Slot[this.length];
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Slot[] getSlots() {
        return slots;
    }

    public void setSlots(Slot[] slots) {
        this.slots = slots;
    }
}
