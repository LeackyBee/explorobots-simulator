package io.github.leackybee.agents.vision;

import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Point;
import io.github.leackybee.mapping.RealMap;

import java.util.List;

public class RayCast extends VisionHandler{

    public RayCast(int visualRange, double FoV) {
        super(visualRange, FoV);
    }

    @Override
    public void scan(OccupancyGrid occGrid, RealMap map, Point position, double heading) {
        // We send 2 rays for every pixel on the outer perimeter of the visual cone
        // One to the center of the pixel, and one to the edge.
        double divisions = (4*Math.PI * (FoV/2*Math.PI)*2*visualRange);

        for(double h = heading - FoV/2; h <= heading + FoV/2; h += FoV/divisions){
            double tx = Math.sin(h)*visualRange;
            double ty = -Math.cos(h)*visualRange;

            List<Point> ray = occGrid.getLinePoints(position.x,position.y,Math.round(tx), Math.round(ty));
            for(Point point : ray){
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
