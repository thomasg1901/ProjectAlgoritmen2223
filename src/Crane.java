import jdk.jshell.spi.ExecutionControl;

import java.awt.*;

public class Crane {
    private Point possition;
    private int speedx;
    private int speedy;
    public Crane(Point startPoint, int speedx, int speedy){
        this.possition = startPoint;
        this.speedx = speedx;
        this.speedy = speedy;
    }

    public double moveCrane(Point p){
        double timex = ((double)(p.x - possition.x))/speedx;
        double timey = ((double) (p.y - possition.y))/speedy;
        return 0.0;
    }
}
