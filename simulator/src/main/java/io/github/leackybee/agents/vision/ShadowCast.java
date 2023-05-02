package io.github.leackybee.agents.vision;

import io.github.leackybee.main.Constants;
import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Point;
import io.github.leackybee.mapping.RealMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Symmetric ShadowCast
 * Based on https://www.albertford.com/shadowcasting
 * with some modifications to allow for FOV and limited range
 */
public class ShadowCast extends VisionHandler{

    OccupancyGrid occGrid;
    RealMap map;
    double heading;
    Point o;

    public ShadowCast(int visualRange, double FoV){
        super(visualRange, FoV);
    }

    @Override
    public void scan(OccupancyGrid occGrid, RealMap map, Point o, double heading) {
        this.heading = heading;
        this.o = o;
        this.occGrid = occGrid;
        this.map = map;
        occGrid.setFree(o);


        for(int i = 0; i < 4; i++){
            Quadrant q = new Quadrant(o, i);

            Row first = new Row(1,new Slope(-1,1),new Slope(1,1));
            scanRow(first, q);
        }
        System.out.println(occGrid);
    }

    private Slope slope(Tile tile){
        return new Slope(2 * (tile.col - 1), 2*tile.row);
    }

    private boolean isSymmetric(Row row, Tile tile){
        return (tile.col >= row.depth*row.sSlope.value() && tile.col <= row.depth*row.eSlope.value());
    }

    private void reveal(Quadrant q, Tile t){
        Point p = q.transform(t);

        switch(map.checkTile(p)){
            case Wall -> occGrid.setWall(p);
            case Free -> occGrid.setFree(p);
        }
    }

    private boolean is_Floor(Quadrant q, Tile t){
        if(t == null){
            return false;
        }
        return map.checkTile(q.transform(t)) == RealMap.TileState.Free;
    }

    private boolean is_Wall(Quadrant q, Tile t){
        if(t == null){
            return false;
        }
        return map.checkTile(q.transform(t)) == RealMap.TileState.Wall;
    }

    private boolean inRange(Tile t){
        return Math.hypot(t.col, t.row) <= visualRange;
    }

    private boolean inCone(Quadrant q, Tile t){
        Point p = q.transform(t);
        double hx = Math.sin(heading);
        double hy = -Math.cos(heading);

        return Math.acos(((p.x-o.x)*hx + (p.y-o.y)*hy)/Math.hypot((p.x-o.x),(p.y-o.y))) < FoV/2;
    }

    private void scanRow(Row r, Quadrant q){
        Tile prev = null;
        for(Tile t : r.tiles()){
            if(!inRange(t) || !inCone(q,t)){
                continue;
            }
            if(Constants.VISION_DEBUG){
                occGrid.setFocus(q.transform(t));
                System.out.println(occGrid);
            }

            if(is_Wall(q, t) || isSymmetric(r,t)){
                reveal(q, t);
            }
            if(is_Wall(q, prev) && is_Floor(q, t)){
                r.sSlope = slope(t);
            }
            if(is_Floor(q, prev) && is_Wall(q, t)){
                System.out.println("Split");
                Row next = r.next();
                next.eSlope = slope(t);
                scanRow(next, q);
            }
            prev = t;
        }
        if(r.depth <= visualRange && is_Floor(q, prev)){
            scanRow(r.next(), q);
        }
    }
}

class Row {

    int depth;
    Slope sSlope;
    Slope eSlope;

    Row(int d, Slope sH, Slope eH) {
        this.depth = d;
        this.sSlope = sH;
        this.eSlope = eH;
    }

    List<Tile> tiles() {
        List<Tile> output = new ArrayList<>();
        int minCol = (int) Math.ceil(depth * sSlope.value());
        int maxCol = (int) Math.floor(depth * eSlope.value());
        for (int i = minCol; i <= maxCol; i++) {
            output.add(new Tile(depth, i));
        }
        return output;
    }

    Row next() {
        return new Row(depth + 1, sSlope, eSlope);
    }
}

class Quadrant{

    Point origin;
    int cardinal;

    // cardinal (0,1,2,3) == (north east south west)
    Quadrant(Point origin, int cardinal){
        this.origin = origin;
        this.cardinal = cardinal;
    }

    Point transform(Tile tile){
        // tile is a 2-tuple of (row,col)
        switch(cardinal){
            case 0 -> {
                return new Point(origin.x + tile.col, origin.y - tile.row);
            }
            case 1 -> {
                return new Point(origin.x + tile.row, origin.y + tile.col);
            }
            case 2 -> {
                return new Point(origin.x + tile.col, origin.y + tile.row);
            }
            case 3 -> {
                return new Point(origin.x - tile.row, origin.y + tile.col);
            }
            default -> throw new RuntimeException("Incorrect Cardinal : " + cardinal);
        }
    }
}
class Tile{
    int col;
    int row;

    Tile(int r, int c){
        this.col = c;
        this.row = r;
    }
}
class Slope{
    int num;
    int denom;

    Slope(int n, int d){
        this.num = n;
        this.denom = d;
    }

    double value(){
        return (double) num/denom;
    }
}