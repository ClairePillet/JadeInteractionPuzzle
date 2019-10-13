/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import java.io.Serializable;

/**
 *
 * @author Claire
 */
public class MoveMessageContent  implements Serializable {
    
    private int sizePathLeft;
    private Position direction;
  
    public MoveMessageContent(int sizePathLeft, Position direction) {
        this.sizePathLeft = sizePathLeft;
        this.direction = direction;
    }

    public int getSizePathLeft() {
        return sizePathLeft;
    }

    public void setSizePathLeft(int sizePathLeft) {
        this.sizePathLeft = sizePathLeft;
    }

    public Position getDirection() {
        return direction;
    }

    public void setDirection(Position direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "MoveMessageContent{" + "sizePathLeft=" + sizePathLeft + ", direction=" + direction + '}';
    }
    
    
}
