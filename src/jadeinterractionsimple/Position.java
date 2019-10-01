/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

/**
 *
 * @author claire
 */
public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
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
//        if(!o.getClass().isInstance(Position.class)){
//            return false;
//        }
        
        Position posToTes= (Position)o;
         System.out.println(posToTes +" test");
        if(posToTes.getX()==this.x && posToTes.getY()==this.y)
        {

            return true;
        }
        return false; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "x:" +this.x +" y: "+this.y; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
