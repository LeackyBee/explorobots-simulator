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
            map = new TileState[Constants.MAP_HEIGHT][Constants.MAP_WIDTH];

            for(int y = 0; y < png.getHeight(); y++){
                for(int x = 0; x < png.getWidth(); x++){
                    // x and y are this way around because we are storing our map in
                    // row-major fashion, while the image is column-major
                    if(png.getRGB(x,y) == BLACK){
                        map[y][x] = TileState.Wall;
                    } else{
                        map[y][x] = TileState.Free;
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
        if (isInBounds(x, y)) {
            return map[y][x];
        }
        return TileState.Wall;
    }

    public boolean isInBounds(Point c){
        return isInBounds(c.x,c.y);
    }

    public boolean isInBounds(int x, int y){
        return !(x >= map[0].length || x < 0 || y >= map.length || y < 0);
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
                switch (cell) {
                    case Wall -> output.append("█");
                    case Free -> output.append(" ");
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
