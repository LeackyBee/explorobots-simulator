package io.github.leackybee.mapping;

import java.util.*;

public class Path {

    private int current;

    private List<Point> pathPoints = new ArrayList();

    private Point start;
    private Point goal;
    private OccupancyGrid grid;
    private int stepSize;

    private boolean valid = false;

    public Path(Point start, Point goal, OccupancyGrid grid, int stepSize){
        this.start = start;
        this.goal = goal;
        this.grid = grid;
        this.stepSize = stepSize;

        plan();
    }

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

        while(!frontier.isEmpty()){
            Point c = frontier.poll();

            if(c == goal){
                while(traceback.get(c) != start){
                    pathPoints.add(0, c);
                }
                pathPoints.add(0,start);
                valid = true;
                return;
            }

            List<Point> neighbours = grid.getValidNeighbours(c, stepSize);

            for(Point n : neighbours){
                double tempGScore = g_scores.get(c) + Math.hypot(n.x - c.x, n.y - c.y);

                if(tempGScore < g_scores.get(n)){
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
            return goal;
        }

        return pathPoints.get(++current);

    }

}
