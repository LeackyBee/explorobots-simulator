package io.github.leackybee.mapping;

import io.github.leackybee.main.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OccupancyGrid{

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
        setWall(c.x,c.y);
    }

    public void setWall(int x, int y){
        if(isInBounds(x,y)){
            grid[x][y] = tileState.Wall;
        }
    }

    public void setFree(Point c){
        setFree(c.x,c.y);
    }

    public void setFree(int x, int y){
        if(isInBounds(x,y)){
            grid[x][y] = tileState.Free;
        }
    }

    public boolean isFree(Point c){
        return isFree(c.x,c.y);
    }

    public boolean isFree(int x, int y){
        if(isInBounds(x,y)){
            return grid[x][y] == tileState.Free;
        } else {
            return false;
        }
    }

    public boolean isWall(Point c){
        return isWall(c.x,c.y);
    }

    public boolean isWall(int x, int y){
        if(isInBounds(x,y)){
            return grid[x][y] == tileState.Wall;
        } else {
            return true;
        }
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
        return !(x > grid.length || x < 0 || y > grid[0].length || y < 0);
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

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(" ");
        for(tileState ignored : grid[0]){
            output.append("▁");
        }
        output.append(" \n");
        for (tileState[] row : grid){
            output.append("▕");
            for(tileState cell : row){
                switch (cell){
                    case Wall:
                        output.append("█");
                        break;
                    case Free:
                        output.append(" ");
                        break;
                    case Unknown:
                        output.append("░");
                        break;
                }
            }
            output.append("▏\n");
        }
        output.append(" ");
        for(tileState ignored : grid[0]){
            output.append("▔");
        }
        output.append(" ");
        return output.toString();
    }

    // Bresenham's Line Algorithm
    public List<Point> getLinePoints(int x0, int y0, int xn, int yn){
        List<Point> output = new ArrayList<>();
        int dx = Math.abs(xn-x0);
        int dy = Math.abs(yn-y0);
        int sx = x0 < xn ? 1 : -1;
        int sy = y0 < yn ? 1 : -1;
        int err = dx - dy;

        while(x0 != xn || y0 != yn){
            output.add(new Point(x0,y0));
            int e2 = 2*err;
            if(e2 > -dy){
                err -= dy;
                x0 += sx;
            }

            if(e2 < dx){
                err += dx;
                y0 += sy;
            }

        }

        return output;
    }

    // Bresenham's Circle Algorithm
    public List<Point> getCirclePoints(int radius, int x0, int y0){
        List<Point> output = new ArrayList<>();

        int d = 3-2*radius;
        int x = 0;
        int y = radius;

        while(x <= y){
            output.add(new Point(x0+x, y0+y));
            output.add(new Point(x0+y, y0+x));
            output.add(new Point(x0-x, y0+y));
            output.add(new Point(x0-y, y0+x));
            output.add(new Point(x0-x, y0-y));
            output.add(new Point(x0-y, y0-x));
            output.add(new Point(x0+x, y0-y));
            output.add(new Point(x0+y, y0-x));

            if(d<0){
                d = d + 4*x + 6;
            } else{
                d = d + 4*(x-y) + 10;
                y--;
            }
            x++;
        }
        return output;
    }

}