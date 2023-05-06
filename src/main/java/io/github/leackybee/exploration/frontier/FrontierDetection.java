package io.github.leackybee.exploration.frontier;

import io.github.leackybee.mapping.OccupancyGrid;

import java.util.List;

// Once we've discovered a point on the frontier, we expand left and right
// For each node, we get the list of immediate neighbours, remove all of them that don't border unknown
// Stretch all
public interface FrontierDetection {

    List<Frontier> findFrontiers(OccupancyGrid map);
}
