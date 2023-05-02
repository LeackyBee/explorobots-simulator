package io.github.leackybee.agents;

import io.github.leackybee.agents.vision.InverseRayCast;
import io.github.leackybee.agents.vision.RayCast;
import io.github.leackybee.agents.vision.ShadowCast;
import io.github.leackybee.agents.vision.VisionHandler;
import io.github.leackybee.main.Constants;
import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Point;
import io.github.leackybee.mapping.RealMap;

public class Agent {

    private int x;
    private int y;
    private double heading;
    private final OccupancyGrid occGrid;
    private final VisionHandler vision;

    public Agent(int x, int y, double heading, int visualRange, double visionCone){
        this.x = x;
        this.y = y;
        this.heading = heading;
        occGrid = new OccupancyGrid();

        switch (Constants.AGENT_VISION_TYPE){
            case RAY-> vision = new RayCast(visualRange, visionCone);
            case SHADOW -> vision = new ShadowCast(visualRange, visionCone);
            case INVERSE_RAY -> vision = new InverseRayCast(visualRange,visionCone);
            default -> throw new RuntimeException("Invalid Vision Handler : " + Constants.AGENT_VISION_TYPE);
        }
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



