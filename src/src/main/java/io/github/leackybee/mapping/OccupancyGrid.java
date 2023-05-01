package io.github.leackybee.mapping;

import io.github.leackybee.main.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class OccupancyGrid{

    private enum tileState{
        Unknown,
        Wall,
        Free,
    }
    private final tileState[][] grid;

    public OccupancyGrid(){
        grid = new tileState[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
        for (tileState[] tileStates : grid) {
            Arrays.fill(tileStates, tileState.Unknown);
        }
    }

    public void setWall(Point c){
        grid[c.x][c.y] = tileState.Wall;
    }

    public void setFree(Point c){
        grid[c.x][c.y] = tileState.Free;
    }

    public boolean isFree(Point c){
        return isFree(c.x,c.y);
    }

    public boolean isFree(int x, int y){
        return grid[x][y] == tileState.Free;
    }

    public boolean isWall(Point c){
        return grid[c.x][c.y] == tileState.Wall;
    }

    public boolean isWall(int x, int y){
        return grid[x][y] == tileState.Wall;
    }

    public boolean isUnknown(Point c){
        return isUnknown(c.x,c.y);
    }

    public boolean isUnknown(int x, int y){
        return grid[x][y] == tileState.Unknown;
    }

    public boolean isInBounds(Point c){
        return isInBounds(c.x,c.y);
    }

    public boolean isInBounds(int x, int y){
        return x > grid.length || x < 0 || y > grid[0].length || y < 0;
    }


    public List<Point> getValidNeighbours(Point c, int radius){
        List<Point> output = new ArrayList<>();
        for(int i = c.x - radius; i <= c.x + radius; i++){
            for(int j = c.y - radius; j <= c.y + radius; j++){
                if(i == c.x && j == c.y){
                    continue;
                } else if(Math.hypot(c.y-j, c.x-i) > radius){
                    continue;
                } else{
                    if(!isWall(i,j) || !isInBounds(i,j) || !isUnknown(i,j)){
                        output.add(new Point(i,j));
                    }
                }
            }
        }
        return output;
    }

}