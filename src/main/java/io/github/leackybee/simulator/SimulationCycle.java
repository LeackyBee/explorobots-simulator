package io.github.leackybee.simulator;

import com.vaadin.flow.component.UI;
import io.github.leackybee.agents.Agent;
import io.github.leackybee.exploration.frontier.Frontier;
import io.github.leackybee.mapping.OccupancyGrid;
import io.github.leackybee.mapping.Point;
import io.github.leackybee.mapping.RealMap;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SimulationCycle {

    RealMap map;
    Agent[] agents;

    int timestep = 0;

    public SimulationCycle(){
        this.map = new RealMap();
        this.agents = new Agent[Constants.NUM_AGENTS];
        agents[0] = new Agent(10,10,Math.PI/2,100,Math.PI, 0);
    }

    public void start(int cycles){
        for(int i = 0; i < cycles; i++){
            timestep++;
            vision();
            updateCanvas();
            communicate();
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

    private void communicate(){
        for(Agent a : agents){
            for(Agent b : agents){
                if (!a.equals(b)) {
                    // We can only communicate map information if both agents are in range of eachother
                    int commRange = Math.min(a.getCommRange(), b.getCommRange());

                    if(Math.hypot(a.getX()-b.getX(),a.getY()-b.getY())<=commRange){
                        a.getOccGrid().merge(b.getOccGrid());
                        b.getOccGrid().merge(a.getOccGrid());
                    }
                }
            }
        }
    }

    private void updateCanvas(){
        BufferedImage image = new BufferedImage(Constants.MAP_WIDTH, Constants.MAP_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Draw a rectangle on the image

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0,0,Constants.MAP_WIDTH, Constants.MAP_HEIGHT);


        for(Agent a : agents){
            OccupancyGrid occ = a.getOccGrid();

            for(int x = 0; x <= Constants.MAP_WIDTH; x++){
                for(int y = 0; y <= Constants.MAP_HEIGHT; y++){
                    switch(occ.checkTile(x,y)){
                        case Wall -> {
                            g2d.setColor(Color.BLACK);
                            g2d.drawRect(x,y,0,0);
                        }
                        case Free -> {
                            g2d.setColor(Color.WHITE);
                            g2d.drawRect(x,y,0,0);
                        }
                    }
                }
            }


            if(Constants.DRAW_FRONTIERS){
                List<Frontier> frontiers = occ.findFrontiers(new Point(a.getX(),a.getY()));
                g2d.setColor(Color.GREEN);
                for (Frontier f : frontiers){
                    for(Point p : f.getPoints()){
                        g2d.fillRect(p.x,p.y,1,1);
                    }
                }
            }

        }

        g2d.setColor(Color.RED);
        for(Agent a : agents){
            g2d.fillRect(a.getX()-1, a.getY()-1, 2,2);
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
