package io.github.leackybee.exploration;

import io.github.leackybee.agents.Agent;
import io.github.leackybee.exploration.frontier.Controller;
import io.github.leackybee.exploration.frontier.Frontier;
import io.github.leackybee.mapping.Path;
import io.github.leackybee.mapping.Point;

import java.util.Comparator;
import java.util.List;

/**
 * Yamauchi's Frontier-Based Algorithm
 */
public class FrontierBasedExploration implements Controller {

    Agent agent;

    Point target;
    Path tPath;

    @Override
    public Point nextPosition() {
        if(tPath == null || tPath.isFinished()) {
            List<Frontier> frontiers = agent.getOccGrid().findFrontiers(agent.getPosition());
            Frontier f = frontiers.stream().max(Comparator.comparingInt(Frontier::getPerimeter)).get();

            target = f.getCenter();
            System.out.println(target);
            tPath = new Path(agent.getPosition(), target, agent.getOccGrid(), 1);
        }

        return tPath.getNextPoint();
    }

    public FrontierBasedExploration(Agent a){
        this.agent = a;
    }
}
