package io.github.leackybee.agents;

import io.github.leackybee.agents.vision.InverseRayCast;
import io.github.leackybee.agents.vision.RayCast;
import io.github.leackybee.agents.vision.ShadowCast;
import io.github.leackybee.agents.vision.VisionHandler;
import io.github.leackybee.exploration.FrontierBasedExploration;
import io.github.leackybee.exploration.frontier.Controller;
import io.github.leackybee.simulator.Constants;
import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Path;
import io.github.leackybee.mapping.Point;
import io.github.leackybee.mapping.RealMap;

public class Agent {

    private Path path;
    private int x;
    private int y;
    private double heading;
    private final OccupancyGrid occGrid;
    private final VisionHandler vision;
    private final int commRange;
    private final Controller controller;

    public Agent(int x, int y, double heading, int visualRange, double fov, int commRange){
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.commRange = commRange;
        occGrid = new OccupancyGrid();

        switch (Constants.AGENT_VISION_TYPE){
            case RAY-> vision = new RayCast(visualRange, fov);
            case SHADOW -> vision = new ShadowCast(visualRange, fov);
            case INVERSE_RAY -> vision = new InverseRayCast(visualRange,fov);
            default -> throw new RuntimeException("Invalid Vision Handler : " + Constants.AGENT_VISION_TYPE);
        }

        switch (Constants.EXPLORATION_ALGORITHM){
            case FrontierBased -> controller = new FrontierBasedExploration(this);
            default -> throw new RuntimeException("Invalid Exploration Algorithm : " + Constants.EXPLORATION_ALGORITHM);

        }
    }

    public void setPath(Path p){
        this.path = p;
    }

    public OccupancyGrid getOccGrid(){
        return this.occGrid;
    }
    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }
    public Point getPosition(){
        return new Point(x,y);
    }
    public int getCommRange(){return this.commRange;}

    public void takeNextStep(){
        move(controller.nextPosition());
    }

    public void move(Point p){
        move(p.x,p.y);
    }

    public void move(int x, int y){
        rotate(Math.atan2(x-this.x,y-this.y));
        this.x = x;
        this.y = y;
    }

    public void rotate(double newheading){
        heading = newheading % (2*Math.PI);
    }

    public void vision(RealMap map){
        vision.scan(occGrid, map, new Point(x,y), heading);
    }

}



