/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import jade.core.AID;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author Claire
 */
public class AStar {

    private Environnement env;
    private Graph g;

    public Environnement getEnv() {
        return env;
    }

    public void setEnv(Environnement env) {
        this.env = env;
    }

    public AStar(Environnement env) {
        this.env = env;
        this.g = env.getG();
    }

    synchronized PriorityQueue<Node> initQueue() {
        return new PriorityQueue<>(10, new Comparator<Node>() {
            public int compare(Node x, Node y) {
                if (x.getDistToStart() < y.getDistToStart()) {
                    return -1;
                }
                if (x.getDistToStart() > y.getDistToStart()) {
                    return 1;
                }
                return 0;
            }
        ;
    }

    );
    }
    synchronized LinkedList<Node> reconstructPath(Node start, Node goal,
            Map<Node, Node> parentMap) {
        // construct output list
        LinkedList<Node> path = new LinkedList<>();
        Node currNode = goal;
        while (!currNode.equals(start)) {
            path.addFirst(currNode);
            currNode = parentMap.get(currNode);
        }

        return path;
    }

    synchronized Map<Node, Integer> initializeAllToInfinity() {
        Map<Node, Integer> distances = new HashMap<>();

        for (Node n : g.getNodes()) {

            distances.put(n, Integer.MAX_VALUE);
        }
        return distances;
    }

    synchronized LinkedList<Node> aStarSearch(Position start, Position goal, ArrayList<AID> exclusionList) {

        Node startNode = g.getNode(start);
        Node endNode = g.getNode(goal);

        // setup for A*
        HashMap<Node, Node> parentMap = new HashMap<Node, Node>();
        HashSet<Node> visited = new HashSet<Node>();
        Map<Node, Integer> distances = initializeAllToInfinity();
        ArrayList<Position> postionTotAvoid= new ArrayList<>();
        Queue<Node> priorityQueue = initQueue();
        if (exclusionList != null) {
            for (AID aid : exclusionList){
                Position p= env.getFormePosFromAID(aid);
                if(p!=null){
                     postionTotAvoid.add(p);
                }
               
            }
        }
        //  enque StartNode, with distance 0
        startNode.setDistToStart(0);
        distances.put(startNode, 0);
        priorityQueue.add(startNode);
        Node current = null;

        while (!priorityQueue.isEmpty()) {
            current = priorityQueue.remove();

            if (!visited.contains(current)) {
                visited.add(current);
                // if last element in PQ reached
                if (current.equals(endNode)) {
                    LinkedList<Node> path = reconstructPath(startNode, endNode, parentMap);

                    return path;

                }

                Set<Node> neighbors = current.getAdjnode();
                for (Node neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {

                        // calculate predicted distance to the end node
                        Integer predictedDistance = neighbor.getPos().distance(endNode.getPos());

                        // 1. calculate distance to neighbor. 2. calculate dist from start node
                        Integer neighborDistance = 1;
//                        if (caseIsFree(neighbor.getPos()) != null) {
//                            neighborDistance = 50;
//                        }
                        if (!postionTotAvoid.isEmpty()) {
                            if(postionTotAvoid.contains(neighbor.getPos())){
                                neighborDistance=1000;
                            }                            
                        }
                        Integer totalDistance = current.getDistToStart() + neighborDistance + predictedDistance;

                        // check if distance smaller
                        if (totalDistance < distances.get(neighbor)) {
                            // update n's distance
                            distances.put(neighbor, totalDistance);
                            // used for PriorityQueue
                            neighbor.setDistToStart(totalDistance);
                            neighbor.setDistPredicted(predictedDistance);
                            // set parent
                            parentMap.put(neighbor, current);
                            // enqueue
                            priorityQueue.add(neighbor);
                        }
                    }
                }
            }
        }
        return null;
    }

}
