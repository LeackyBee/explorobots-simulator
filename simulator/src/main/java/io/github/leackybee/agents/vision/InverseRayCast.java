package io.github.leackybee.agents.vision;

import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Point;
import io.github.leackybee.mapping.RealMap;

import java.util.List;

public class InverseRayCast extends VisionHandler{

    public InverseRayCast(int visualRange, double FoV) {
        super(visualRange, FoV);
    }

    @Override
    public void scan(OccupancyGrid occGrid, RealMap map, Point position, double heading) {
        List<Point> circle = occGrid.getCirclePoints(this.visualRange, position.x,position.y);

        double hx = Math.sin(heading);
        double hy = -Math.cos(heading);

        for(Point p : circle){
            // Only look at points that are in our visual cone
            Point vec = new Point(p.x -position.x, p.y-position.y);
            if(Math.acos((vec.x*hx + vec.y*hy)/Math.hypot(vec.x,vec.y)) <= FoV/2){
                List<Point> points = occGrid.getLinePoints(position.x,position.y, p.x,p.y);
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
    }
}
