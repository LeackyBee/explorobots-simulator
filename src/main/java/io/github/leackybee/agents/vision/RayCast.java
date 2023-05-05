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
        int divisions = (int) (0.15*Math.PI * (FoV/2*Math.PI)*2*visualRange);

        for(double h = heading - FoV/2; h <= heading + FoV/2; h += FoV/(divisions-1)){
            double tx = Math.sin(h)*visualRange;
            double ty = -Math.cos(h)*visualRange;

            List<Point> ray = occGrid.getLinePoints(position.x, position.y, (int) Math.round(tx), (int) Math.round(ty));
            for(Point point : ray){
                if(Math.hypot(point.x - position.x, point.y - position.y) > visualRange){
                    break;
                }
                occGrid.setFocus(point);
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
