package io.github.leackybee.exploration.frontier;

import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Point;
import jakarta.servlet.ServletOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Wavefront Frontier Detection
public class WFD implements FrontierDetection{
    @Override
    public List<Frontier> findFrontiers(Point o, OccupancyGrid map) {
        List<Point> open = new ArrayList<>(List.of(o));
        List<Point> closed = new ArrayList<>();
        List<Frontier> frontiers = new ArrayList<>();

        while(!open.isEmpty()){
            Point p = open.remove(0);
            if (!closed.contains(p) && map.checkTile(p) == OccupancyGrid.occTileState.Free) {
                boolean candidate = false;

                // If one of p's neighbours are unknown, it is a frontier node
                for (Point n : map.getAllNeighbours(p,false)) {
                    if (map.isUnknown(n)) {
                        candidate = true;
                        break;
                    }
                }
                // Add all of p's neighbours that we've not been to, and that are free
                List<Point> neighbours = map.getAllNeighbours(p,true);
                neighbours.removeIf(b -> closed.contains(b) || !map.isFree(b));
                open.addAll(neighbours);

                if (candidate) {
                    Frontier f = stretchFrontier(map, p);
                    frontiers.add(f);
                    //closed.addAll(f.getPoints());
                }
            }
            closed.add(p);
        }

        return frontiers;
    }

    private Frontier stretchFrontier(OccupancyGrid map, Point p){
        List<Point> frontier = new ArrayList<>();
        List<Point> open = new ArrayList<>();
        open.add(p);

        while(!open.isEmpty()){
            Point c = open.remove(0);
            if(!frontier.contains(c) && map.isFree(c)){
                frontier.add(c);

                List<Point> frontierNeighbours = new ArrayList<>();
                for(Point n : map.getAllNeighbours(c,true)){
                    for(Point nn : map.getAllNeighbours(n,false)){
                        if(map.isUnknown(nn)){
                            frontierNeighbours.add(n);
                            break;
                        }
                    }
                }

                for(Point fn : frontierNeighbours){
                    if(!frontier.contains(fn)){
                        open.add(fn);
                    }
                }
            }
        }
        return new Frontier(frontier);
    }
}
