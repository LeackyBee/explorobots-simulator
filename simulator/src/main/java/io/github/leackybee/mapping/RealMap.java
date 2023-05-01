package io.github.leackybee.mapping;

import io.github.leackybee.main.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RealMap {

    private static final int BLACK = -16777216;
    private static final int WHITE = -1;

    public enum TileState{
        Free,
        Wall
    }

    private TileState[][] map;

    public RealMap(){
        initMap();
    }

    private void initMap(){
        try {
            BufferedImage png = ImageIO.read(new File(Constants.MAP_DIRECTORY.concat(Constants.MAP)));
            Constants.MAP_HEIGHT = png.getHeight();
            Constants.MAP_WIDTH = png.getWidth();
            map = new TileState[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];


            for(int x = 0; x < png.getWidth(); x++){
                for(int y = 0; y < png.getHeight(); y++){
                    System.out.println(png.getRGB(x,y));
                    if(png.getRGB(y,x) == BLACK){
                        map[x][y] = TileState.Wall;
                    } else{
                        map[x][y] = TileState.Free;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Map not found: ".concat(Constants.MAP));
        }
    }

    public TileState checkTile(Point c){
        return checkTile(c.x,c.y);
    }

    public TileState checkTile(int x, int y){
        return map[x][y];
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(" ");
        for(TileState ignored : map[0]){
            output.append("▁");
        }
        output.append(" \n");
        for (TileState[] row : map){
            output.append("▕");
            for(TileState cell : row){
                switch (cell){
                    case Wall:
                        output.append("█");
                        break;
                    case Free:
                        output.append(" ");
                        break;
                }
            }
            output.append("▏\n");
        }
        output.append(" ");
        for(TileState ignored : map[0]){
            output.append("▔");
        }
        output.append(" ");
        return output.toString();
    }
}
