package io.github.leackybee.mapping;

public class Vector {

    public final double x;
    public final double y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector(double heading){
        this.x = Math.sin(heading);
        this.y = - Math.cos(heading);
    }

}
