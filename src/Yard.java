import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Yard {
    private Map<Container, List<Slot>> containerSlots;
    private List<Crane> cranes;


    public Yard(Map<Container, List<Slot>> containerSlots, List<Crane> cranes) {
        this.containerSlots = containerSlots;
        this.cranes = cranes;
    }

    public void moveCrane(Crane crane, Point p, double startTime){
        double timex = ((double)(p.x - crane.getPosition().x))/crane.getSpeedx();
        double timey = ((double) (p.y - crane.getPosition().y))/crane.getSpeedy();

        // Control movement
        if(controlCollision(p, crane, 0))
            throw new IllegalArgumentException("Crane comes to close to the other cranes to move to this point");

        crane.setPosition(p);

        // Save trajectory
        HashMap<Double, Point> trajectory = crane.getTrajectory();
        trajectory.put(startTime + timex + timey, p);
        crane.setTrajectory(trajectory);
    }

    private boolean controlCollision(Point p, Crane c, int delta){
        for (Crane crane: cranes) {
            if (crane.getId() == c.getId()) break;

            if(c.getPosition().x < p.x) // Crane moves left
                if (c.getPosition().x < crane.getPosition().x + delta) // Check if crane comes to close to the other cranes
                    return true;
            else                        // Crane moves right
                if(c.getPosition().x > crane.getPosition().x - delta) // Check if crane comes to close to the other cranes
                    return true;
        }

        return false;
    }

    private boolean controlSafty(HashMap<Double, Point> trajectory, HashMap<Double, Point> trajectory2, int delta){
        // Check if the trajectories come closer than allowed delta

        return false;
    }

    public Map<Container, List<Slot>> getContainerSlots() {
        return containerSlots;
    }

    public void setContainerSlots(Map<Container, List<Slot>> containerSlots) {
        this.containerSlots = containerSlots;
    }

    public List<Crane> getCranes() {
        return cranes;
    }

    public void setCranes(List<Crane> cranes) {
        this.cranes = cranes;
    }
}
