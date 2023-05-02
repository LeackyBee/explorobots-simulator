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
        Focus,
    }
    private final tileState[][] grid;

    public OccupancyGrid(){
        grid = new tileState[Constants.MAP_HEIGHT][Constants.MAP_WIDTH];
        for (tileState[] tileStates : grid) {
            Arrays.fill(tileStates, tileState.Unknown);
        }
    }

    public void setWall(Point c){
        setWall(c.x,c.y);
    }

    public void setWall(int x, int y){
        if(isInBounds(x,y)){
            grid[y][x] = tileState.Wall;
        }
    }

    public void setFree(Point c){
        setFree(c.x,c.y);
    }

    public void setFree(int x, int y){
        if(isInBounds(x,y)){
            grid[y][x] = tileState.Free;
        }
    }

    public void setFocus(Point p){
        setFocus(p.x,p.y);
    }

    public void setFocus(int x, int y){
        if(isInBounds(x,y)){
            grid[y][x] = tileState.Focus;
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
        return !(x >= grid.length || x < 0 || y >= grid[0].length || y < 0);
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



    // Bresenham's Line Algorithm
    public List<Point> getLinePoints(int x0, int y0, int xn, int yn){
        List<Point> output = new ArrayList<>();
        int dx = Math.abs(xn-x0);
        int dy = Math.abs(yn-y0);
        int sx = x0 < xn ? 1 : -1;
        int sy = y0 < yn ? 1 : -1;
        int err = dx - dy;

        while(x0 != xn || y0 != yn){
            output.add(new Point(x0, y0));
            int e2 = 2*err;
            if(e2 > -dy){
                err -= dy;
                x0 += sx;
                if(Constants.THICK_LINES){
                    // Adding a point here too gives us thicker lines, preventing some artefacts
                    output.add(new Point(x0, y0));
                }
            }

            if(e2 < dx){
                err += dx;
                y0 += sy;
                if(Constants.THICK_LINES){
                    // Adding a point here too gives us thicker lines, preventing some artefacts
                    output.add(new Point(x0, y0));
                }
            }

        }

        return output;
    }

    public List<Point> getLinePoints(double x0, double y0, double xn, double yn){
        List<Point> output = new ArrayList<>();
        double dx = xn - x0;
        double dy = yn - y0;
        boolean steep = Math.abs(dy) > Math.abs(dx);
        if(steep){
            double temp = x0;
            x0 = y0;
            y0 = temp;

            temp = xn;
            xn = yn;
            yn = temp;

            temp = dx;
            dx = dy;
            dy = temp;
        }

        if(x0 > xn){
            double temp = x0;
            x0 = xn;
            xn = temp;

            temp = y0;
            y0 = yn;
            yn = temp;
        }

        double gradient = dy/dx;
        double y = y0 + gradient;
        double ystep = y0 < yn ? gradient : -gradient;
        double error = 0;

        for(double x = x0; x <= xn; x++){
            if(steep){
                output.add(new Point((int) y, (int) x));
            } else{
                output.add(new Point((int) x, (int) y));
            }

            error += Math.abs(gradient);
            if(error >= 0.5){
                y += ystep;
                if(Constants.THICK_LINES){
                    if(steep){
                        output.add(new Point((int) y, (int) x));
                    } else{
                        output.add(new Point((int) x, (int) y));
                    }
                }
                error -= 1;
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
                output.add(new Point(x0+x, y0+y));
                output.add(new Point(x0+y, y0+x));
                output.add(new Point(x0-x, y0+y));
                output.add(new Point(x0-y, y0+x));
                output.add(new Point(x0-x, y0-y));
                output.add(new Point(x0-y, y0-x));
                output.add(new Point(x0+x, y0-y));
                output.add(new Point(x0+y, y0-x));
            }
            x++;
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
                    case Focus:
                        output.append("F");
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
}