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

    public void putContainerInSlots(Slot[] slots) throws Exception {
        if(slots.length != this.length){
            throw new Exception();
        }
        this.slots = slots;
    }

    public boolean containerIsStackedCorrectly(){
        boolean allSame = true;
        int firstHeight = slots[0].getContainerHeight(this);
        if(firstHeight == -1) return false;
        for(int i = 1; i < slots.length; i++){
            allSame = allSame && slots[i].getContainerHeight(this) == firstHeight;
        }
        return allSame;
    }
}
