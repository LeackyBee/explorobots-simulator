package io.github.leackybee.agents;

import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Point;
import io.github.leackybee.mapping.RealMap;

import java.util.ArrayList;
import java.util.List;

public class Agent {

    private int x;
    private int y;
    private double heading;
    private final int visualRange;
    private final double visionCone;
    private final OccupancyGrid occGrid;


    public Agent(int x, int y, double heading, int visualRange, double visionCone){
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.visualRange = visualRange;
        this.visionCone = visionCone;
        occGrid = new OccupancyGrid();
    }

    public void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void rotate(double newheading){
        heading = newheading;
    }

    public void vision(RealMap map){

        List<Point> circle = occGrid.getCirclePoints(this.visualRange, this.x,this.y);

        double hx = Math.sin(heading);
        double hy = -Math.cos(heading);

        for(Point p : circle){
            // Only look at points that are in our visual cone
            Point vec = new Point(p.x -x, p.y-y);
            if(Math.acos((vec.x*hx + vec.y*hy)/Math.hypot(vec.x,vec.y)) <= visionCone/2){
                List<Point> points = occGrid.getLinePoints(x,y, p.x,p.y);
                for(Point point : points){
                    if(map.checkTile(point) == RealMap.TileState.Wall){
                        occGrid.setWall(point);
                        break;
                    } else{
                        occGrid.setFree(point);
                    }
                }
            }
        }

        occGrid.setWall(x,y);
        System.out.println(occGrid);

    }





}
