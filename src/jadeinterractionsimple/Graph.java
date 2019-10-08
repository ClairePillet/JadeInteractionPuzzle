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
public class Graph {

    private Set<Node> nodes = new HashSet<>();

    public Graph() {
    }

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public Node getNode(Position p) {
        for (Node n : nodes) {
            if (n.getPos().equals(p)) {
                return n;
            }
        }
        return null;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

}
