/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author claire
 */
public class Node {

    private Set<Node> adjNode = new HashSet<>();
    private Position Pos;
    private int distToStart;
    private int distPredicted;

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

    public int getDistToStart() {
        return distToStart;
    }

    public void setDistToStart(int distToStart) {
        this.distToStart = distToStart;
    }

    public int getDistPredicted() {
        return distPredicted;
    }

    public void setDistPredicted(int distPredicted) {
        this.distPredicted = distPredicted;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node n = (Node) obj;
        if (this.Pos.equals(n.getPos())) {
            return true;
        }
        return false;
    }

}
