package io.github.leackybee.exploration.frontier;

import io.github.leackybee.mapping.Point;

import java.util.ArrayList;
import java.util.List;

public class Frontier {

    private final List<Point> points;
    private final Point center;

    public Frontier(List<Point> points){
        this.points = points;
        this.center = points.get(points.size()/2);
    }

    public Point getCenter(){
        return center;
    }

    public List<Point> getPoints(){
        return points;
    }

    public String toString(){
        return "Frontier at (" +
                center.x +
                "," +
                center.y +
                ")";
    }

}
