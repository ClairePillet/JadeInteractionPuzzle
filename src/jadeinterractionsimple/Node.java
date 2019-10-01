/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author claire
 */
public class Node {
    
     private Set<Node> adjNode = new HashSet<>();
    private Position Pos;

    public Node(Position Pos) {
        this.Pos = Pos;
    }

    public Set<Node> getAdjnode() {
        return adjNode;
    }
   public void addNodeAdj(Node nodeA) {
        adjNode.add(nodeA);
    }

    public void setAdjnode(Set<Node> adjnode) {
        this.adjNode = adjnode;
    }

    public Position getPos() {
        return Pos;
    }

    public void setPos(Position Pos) {
        this.Pos = Pos;
    }
    
    
}
