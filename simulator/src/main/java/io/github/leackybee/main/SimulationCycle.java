package io.github.leackybee.main;

import io.github.leackybee.agents.Agent;
import io.github.leackybee.mapping.RealMap;

public class SimulationCycle {

    RealMap map;
    Agent agent;

    public SimulationCycle(){
        this.map = new RealMap();
        this.agent = new Agent(10,10,Math.PI/2,1000,Math.PI, 0);
    }
    public void start(int cycles){
        for(int i = 0; i < cycles; i++){
            System.out.println("cycle");
            vision();
            moveAgents();
        }
    }

    private void moveAgents(){
        agent.takeNextStep();
    }

    private void vision(){
        agent.vision(map);
    }
}
