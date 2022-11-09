import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Crane {

    private int id;
    private Point position;

    private HashMap<Double, Point> trajectory;
    private int speedx;
    private int speedy;
    public Crane(Point startPoint, int speedx, int speedy){
        this.position = startPoint;
        this.speedx = speedx;
        this.speedy = speedy;
        this.trajectory = new HashMap<Double, Point>();
    }


    public HashMap<Double, Point> getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(HashMap<Double, Point> trajectory) {
        this.trajectory = trajectory;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getSpeedx() {
        return speedx;
    }

    public void setSpeedx(int speedx) {
        this.speedx = speedx;
    }

    public int getSpeedy() {
        return speedy;
    }

    public void setSpeedy(int speedy) {
        this.speedy = speedy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
