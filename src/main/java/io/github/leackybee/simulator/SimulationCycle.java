package io.github.leackybee.simulator;

import com.vaadin.flow.component.UI;
import io.github.leackybee.agents.Agent;
import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.RealMap;
import io.github.leackybee.views.simulator.SimulatorView;
import org.apache.tomcat.util.bcel.Const;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SimulationCycle {

    RealMap map;
    Agent[] agents;
    UI ui;

    int timestep = 0;

    public SimulationCycle(){
        this.map = new RealMap();
        this.agents = new Agent[Constants.NUM_AGENTS];
        agents[0] = new Agent(10,10,Math.PI/2,100,Math.PI, 0);
    }

    public void start(int cycles){
        for(int i = 0; i < cycles; i++){
            timestep++;
            updateCanvas();
            vision();
            moveAgents();
        }
    }

    private void moveAgents(){
        for(Agent a : agents){
            a.takeNextStep();
        }
    }

    private void vision(){
        for(Agent a : agents){
            a.vision(map);
        }
    }

    private void updateCanvas(){
        System.out.println("Updating canvas");
        BufferedImage image = new BufferedImage(Constants.MAP_WIDTH, Constants.MAP_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Draw a rectangle on the image
        if(timestep <= 5){
            g2d.setColor(Color.LIGHT_GRAY);
        } else{
            g2d.setColor(Color.cyan);
        }
        g2d.fillRect(0,0,Constants.MAP_WIDTH, Constants.MAP_HEIGHT);


        for(Agent a : agents){
            OccupancyGrid occ = a.getOccGrid();

            for(int x = 0; x <= Constants.MAP_WIDTH; x++){
                for(int y = 0; y <= Constants.MAP_HEIGHT; y++){
                    switch(occ.checkTile(x,y)){
                        case Wall -> {
                            g2d.setColor(Color.BLACK);
                            g2d.drawRect(x,y,1,1);
                        }
                        case Free -> {
                            g2d.setColor(Color.WHITE);
                            g2d.drawRect(x,y,1,1);
                        }
                    }
                }
            }
        }

        g2d.setColor(Color.RED);
        for(Agent a : agents){
            g2d.fillRect(a.getX()-1, a.getY()-1, 3,3);
        }

        // Dispose the Graphics2D object to free up resources
        g2d.dispose();

        try {
            File output = new File(Constants.CANVAS_DIRECTORY + "canvas" + timestep + ".png");
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getTimestep(){
        return timestep;
    }
}
