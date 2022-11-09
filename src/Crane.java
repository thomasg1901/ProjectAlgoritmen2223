import java.awt.*;

public class Crane {
    private Point position;
    private int speedx;
    private int speedy;
    public Crane(Point startPoint, int speedx, int speedy){
        this.position = startPoint;
        this.speedx = speedx;
        this.speedy = speedy;
    }

    public double moveCrane(Point p){
        double timex = ((double)(p.x - position.x))/speedx;
        double timey = ((double) (p.y - position.y))/speedy;
        return 0.0;
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
}
