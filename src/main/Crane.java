package main;
import java.util.*;

public class Crane {

    private int id;
    private Point position;
    private TreeMap<Double, Point> trajectory;

    private ArrayList<Movement> assignedMovements;
    private double speedx;
    private double speedy;

    private double xMin;

    private double xMax;

    private double yMin;

    private double yMax;

    public Crane(int id, Point startPoint, double speedx, double speedy, double xMin, double xMax, double yMin, double yMax){
        this.id = id;
        this.position = startPoint;
        this.speedx = speedx;
        this.speedy = speedy;
        this.trajectory = new TreeMap<Double, Point>();
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.assignedMovements = new ArrayList<>();
    }

    public ArrayList<Movement> getAssignedMovements() {
        return assignedMovements;
    }

    public void addMovement(Movement assignedMovement) {
        this.assignedMovements.add(assignedMovement);
    }

    public TreeMap<Double, Point> getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(TreeMap<Double, Point> trajectory) {
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

    public double getxMin() {
        return xMin;
    }

    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public void setxMax(double xMax) {
        this.xMax = xMax;
    }

    public double getyMin() {
        return yMin;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public double getyMax() {
        return yMax;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
    }
}
