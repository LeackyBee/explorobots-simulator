package io.github.leackybee.mapping;

import io.github.leackybee.simulator.Constants;

import java.util.*;

public class Path {

    private int current;
    private final List<Point> pathPoints = new ArrayList<>();
    private final Point start;
    private final Point goal;
    private final OccupancyGrid grid;
    private final int stepSize;

    private boolean valid = false;
    private boolean finished = false;

    public Path(Point start, Point goal, OccupancyGrid grid, int stepSize){
        this.start = start;
        this.goal = goal;
        this.grid = grid;
        this.stepSize = stepSize;

        plan();
    }

    public boolean isFinished(){return finished;}

    private void plan(){
        Map<Point, Double> f_scores = new HashMap<>();
        f_scores.put(start, h(start));

        Map<Point, Double> g_scores = new HashMap<>();
        g_scores.put(start, 0d);

        Map<Point, Point> traceback = new HashMap<>();

        PriorityQueue<Point> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(a -> f_scores.getOrDefault((Point) a, Double.POSITIVE_INFINITY))
                .thenComparingDouble(a ->  Math.hypot(((Point) a).x, ((Point) a).y)));

        frontier.add(start);
        if(Constants.PATH_DEBUG){
            grid.setFocus(goal.x,goal.y);
        }
        while(!frontier.isEmpty()){

            Point c = frontier.poll();
            if(Constants.PATH_DEBUG){
                grid.setFocus(c.x,c.y);
                System.out.println(grid);
            }

            if(c.equals(goal)){
                while(traceback.get(c) != start){
                    pathPoints.add(0, c);
                    c = traceback.get(c);
                }
                pathPoints.add(0,start);
                valid = true;
                return;
            }

            List<Point> neighbours = grid.getValidNeighbours(c, stepSize);
            //System.out.println(neighbours);

            for(Point n : neighbours){
                double tempGScore = g_scores.get(c) + Math.hypot(n.x - c.x, n.y - c.y);

                if(tempGScore < g_scores.getOrDefault(n, Double.POSITIVE_INFINITY)){
                    traceback.put(n,c);
                    g_scores.put(n, tempGScore);
                    f_scores.put(n, tempGScore + h(n));
                    if(!frontier.contains(n)){
                        frontier.add(n);
                    }
                }
            }
        }
    }

    private double h(Point c){
        return Math.hypot(c.x-goal.x,c.y-goal.y);
    }

    public Point getNextPoint(){
        if(!valid){
            return null;
        }
        if(current >= pathPoints.size()){
            finished = true;
            return goal;
        }

        return pathPoints.get(current++);

    }

}
