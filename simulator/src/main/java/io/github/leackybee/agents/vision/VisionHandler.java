package io.github.leackybee.agents.vision;

import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Point;
import io.github.leackybee.mapping.RealMap;

public abstract class VisionHandler {

    int visualRange;
    double FoV;

    public VisionHandler(int visualRange, double FoV) {
        this.visualRange = visualRange;
        this.FoV = FoV;
    }


    public abstract void scan(OccupancyGrid occGrid, RealMap map, Point position, double heading);
}
