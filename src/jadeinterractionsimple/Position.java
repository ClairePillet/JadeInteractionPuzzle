/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import java.io.Serializable;
import static java.lang.Math.abs;

/**
 *
 * @author claire
 */
public class Position implements Serializable 
{

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distance(Position p) {

        return (abs(this.x - p.getX()) + abs(this.y - p.getY()));

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) {
            return false;
        }

        Position posToTes = (Position) o;
        if (posToTes.getX() == this.x && posToTes.getY() == this.y) {

            return true;
        }
        return false; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "x:" + this.x + " y: " + this.y; //To change body of generated methods, choose Tools | Templates.
    }

}
