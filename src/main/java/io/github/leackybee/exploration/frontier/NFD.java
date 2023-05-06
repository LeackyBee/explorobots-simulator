package io.github.leackybee.exploration.frontier;

import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Point;
import io.github.leackybee.simulator.Constants;

import java.util.ArrayList;
import java.util.List;

// Naive Frontier Detection
public class NFD implements FrontierDetection{

    @Override
    public List<Frontier> findFrontiers(Point o, OccupancyGrid map) {
        List<Frontier> frontiers = new ArrayList<>();
        List<Point> closed = new ArrayList<>();

        for(int x = 0; x < Constants.MAP_WIDTH; x++){
            for(int y = 0; y < Constants.MAP_HEIGHT; y++){
                Point p = new Point(x,y);
                if(!closed.contains(p)){
                    if(map.checkTile(p) == OccupancyGrid.occTileState.Free){

                        boolean candidate = false;

                        for(Point n : map.getAllNeighbours(p)){
                            if(map.isUnknown(n)){
                                candidate = true;
                                break;
                            }
                        }

                        if(candidate){
                            Frontier f = stretchFrontier(map, p);
                            frontiers.add(f);
                            closed.addAll(f.getPoints());
                        }

                    }
                }
                closed.add(p);
            }
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
                for(Point n : map.getAllNeighbours(c)){
                    for(Point nn : map.getAllNeighbours(n)){
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
