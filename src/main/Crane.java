package main;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Crane {

    private int id;
    private Point position;

    private HashMap<Double, Point> trajectory;
    private double speedx;
    private double speedy;

    private double xMin;

    private double xMax;

    private double yMin;

    private double yMax;

    public Crane(Point startPoint, double speedx, double speedy, double xMin, double xMax, double yMin, double yMax){
        this.position = startPoint;
        this.speedx = speedx;
        this.speedy = speedy;
        this.trajectory = new HashMap<Double, Point>();
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
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

    public double getSpeedx() {
        return speedx;
    }

    public void setSpeedx(double speedx) {
        this.speedx = speedx;
    }

    public double getSpeedy() {
        return speedy;
    }

    public void setSpeedy(double speedy) {
        this.speedy = speedy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
