package io.github.leackybee.agents;

import io.github.leackybee.main.Constants;
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
        switch (Constants.AGENT_VISION_TYPE){
            case "raycast" -> rayCastVision(map);
            case "shadowcast" -> shadowCastVision(map);
            case "inverseraycast" -> inverseRayCast(map);
        }
    }


    public void rayCastVision(RealMap map){
        // We send 2 rays for every pixel on the outer perimeter of the visual cone
        // One to the center of the pixel, and one to the edge.
        double divisions = (4*Math.PI * (visionCone/2*Math.PI)*2*visualRange);

        for(double h = heading - visionCone/2; h <= heading + visionCone/2; h += visionCone/divisions){
            double tx = Math.sin(h)*visualRange;
            double ty = -Math.cos(h)*visualRange;

            List<Point> ray = occGrid.getLinePoints(x,y,Math.round(tx), Math.round(ty));
            for(Point point : ray){
                if(map.checkTile(point) == RealMap.TileState.Wall){
                    occGrid.setWall(point);
                    break;
                } else{
                    occGrid.setFree(point);
                }
            }

        }
        System.out.println(occGrid);
    }

    public void shadowCastVision(RealMap map) {
        occGrid.setWall(x,y);

        for(int i = 1; i < 2; i++){
            Quadrant q = new Quadrant(new Point(x,y), i);

            Row first = new Row(1,new Slope(-1,1),new Slope(1,1));
            scan(map, first, q);
        }
        System.out.println(occGrid);
    }


    public void inverseRayCast(RealMap map){

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

    private Slope slope(Tile tile){
        return new Slope(2 * tile.col - 1, 2*tile.row);
    }

    private boolean isSymmetric(Row row, Tile tile){
        return (tile.col >= row.depth*row.sSlope.value() && tile.col <= row.depth*row.eSlope.value());
    }

    private void reveal(RealMap m, Quadrant q, Tile t){
        Point p = q.transform(t);

        switch(m.checkTile(p)){
            case Wall -> occGrid.setWall(p);
            case Free -> occGrid.setFree(p);
        }
    }

    private boolean is_Floor(RealMap map, Quadrant q, Tile t){
        if(t == null){
            return false;
        }
        return map.checkTile(q.transform(t)) == RealMap.TileState.Free;
    }

    private boolean is_Wall(RealMap map, Quadrant q, Tile t){
        if(t == null){
            return false;
        }
        return map.checkTile(q.transform(t)) == RealMap.TileState.Wall;
    }

    private void scan(RealMap map, Row r, Quadrant q){
        Tile prev = null;
        for(Tile t : r.tiles()){
            if(Constants.VISION_DEBUG){
                occGrid.setFocus(q.transform(t));
                System.out.println(occGrid);
            }
            if(is_Wall(map, q, t) || isSymmetric(r,t)){
                reveal(map, q, t);
            }
            if(is_Wall(map, q, prev) && is_Floor(map, q, t)){
                r.sSlope = slope(t);
            }
            if(is_Floor(map, q, prev) && is_Wall(map, q, t)){
                System.out.println("Split");
                Row next = r.next();
                next.eSlope = slope(t);
                scan(map, next, q);
            }
            prev = t;
        }
        if(is_Floor(map, q, prev)){
            System.out.println("new row");
            scan(map, r.next(), q);
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