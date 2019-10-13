/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import jade.core.AID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author claire
 */
public class Environnement {

    private Integer size;
    private Map<String, AID> aidForms;
    volatile Map<String, Position> positionForme;
    volatile Map<String, Boolean> endPosForme;
    private Gui GUI = new Gui();
    private Graph g;
    boolean verbose = false;

    Environnement(Integer size_grid) {
        size = size_grid;
        positionForme = new ConcurrentHashMap<String, Position>();
        endPosForme = new ConcurrentHashMap<String, Boolean>();
        aidForms = new ConcurrentHashMap<String, AID>();
        GUI.setSize(500, 500);
        GUI.setLocation(700, 20);
        GUI.setVisible(true);
        GUI.validate();
        createGraph(size_grid);
    }

    public void createGraph(int sizeGrid) {
        g = new Graph();
        ArrayList<Node> lst = new ArrayList<Node>();
        for (int i = 0; i < sizeGrid; i++) {
            for (int j = 0; j < sizeGrid; j++) {
                Position p = new Position(i, j);
                Node n = new Node(p);
                lst.add(n);
            }
        }

        for (Node n : lst) {
       
            for (Node nAdj : lst) {
                Position posAdj = nAdj.getPos(), pos = n.getPos();

                if (posAdj.getX() == pos.getX()) {
                    if (posAdj.getY() == pos.getY() + 1 || posAdj.getY() == pos.getY() - 1) {
                        n.addNodeAdj(nAdj);
                    }
                }
                if (posAdj.getY() == pos.getY()) {
                    if (posAdj.getX() == pos.getX() + 1 || posAdj.getX() == pos.getX() - 1) {
                        n.addNodeAdj(nAdj);
                    }
                }
            }

            g.addNode(n);
        }
    }

    public Graph getG() {
        return g;
    }

    synchronized void setFormeOKNOK(String forme, boolean state) {
        endPosForme.remove(forme);
        endPosForme.put(forme, state);
    }

    synchronized void moveForme(String forme, Position new_position) {
        positionForme.remove(forme);
        positionForme.put(forme, new_position);
        GUI.moveForme(positionForme);
    }
synchronized  LinkedList<Node>  findClosestWhite(Position actual, Position new_position,AID sender) {
    
        Position pEmpty=null;
        ArrayList exclusionList = new ArrayList();
        exclusionList.add(sender);
        int minDist=1000;
        LinkedList<Node> pathmin=new LinkedList<>();
        AStar astar= new AStar(this);
       Set<Node> lst = g.getNodes();
         for (Node n : lst) {
             if(!n.getPos().equals(new_position)){
                  if (caseIsFree(n.getPos())==null) {
                    LinkedList<Node> path = astar.aStarSearch(actual, n.getPos(),exclusionList );
                    int dist=path.size();
                    if(dist<minDist){
                        pEmpty =n.getPos();
                        minDist=dist;
                        pathmin=path;
                    }
                }
             }
           
        }
    return pathmin;
}
    synchronized Position moveOneCase(Position actual, Position new_position,Position sender) {
        Set<Node> lst = g.getNode(actual).getAdjnode();
        
       
        Position pPossible=null;
        for (Node n : lst) {
            if (n.getPos().equals(new_position) == false &&n.getPos().equals(sender) == false) {
                if (caseIsFree(n.getPos()) == null) {
                    return n.getPos();
                }
                pPossible =n.getPos();
            }
        }
        
        return pPossible;
    }
synchronized boolean checkBlock(Position actual) {
        Set<Node> lst = g.getNode(actual).getAdjnode();
           for (Node nAdj : lst) {             
                if(caseIsFree(nAdj.getPos())==null){
                    return false;
                }                 
            }
       
      
        
        return true;
    }

    synchronized AID caseIsFree(Position new_position) {

        Iterator i = positionForme.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry ps = (Map.Entry) i.next();
            Position pos = (Position) ps.getValue();
            if (pos.equals(new_position) == true) {
                AID aid = aidForms.get(ps.getKey());
                return aid;
            }
        }
        return null;
    }

    synchronized boolean gridIsOK() {
        Boolean[] positionsTab = (Boolean[]) endPosForme.values().toArray(new Boolean[0]);

        for (Boolean state : positionsTab) {
            if (state == false) {
                return false;
            }
        }
        return true;
    }

    Position[] add_forme(AID aid, String forme) {

        Position p1;
        Position p2;

        System.out.println(forme);
        switch (forme) {
            case "A":
                p1 = new Position(2, 0);
                p2 = new Position(0, 0);

                break;
            case "B":
                p1 = new Position(3, 0);
                p2 = new Position(1, 0);//1,0

                break;
            case "C":
                p1 = new Position(1, 2);
                p2 = new Position(2, 0);//2.0

                break;
            case "E":
                p1 = new Position(4, 1);
                p2 = new Position(4, 0);

                break;
            case "F":
                p1 = new Position(3, 2);
                p2 = new Position(0, 1);

                break;
            case "G":
                p1 = new Position(1, 4);
                p2 = new Position(1, 1);

                break;
            case "H":
                p1 = new Position(3, 3);
                p2 = new Position(2, 1);

                break;
            case "I":
                p1 = new Position(4, 2);
                p2 = new Position(3, 1);

                break;
            case "J":
                p1 = new Position(3, 4);
                p2 = new Position(4, 1);

                break;
            case "K":
                p1 = new Position(1, 1);
                p2 = new Position(0, 2);

                break;
            case "L":
                p1 = new Position(2, 2);
                p2 = new Position(1, 2);
                break;
            default://d
                p1 = new Position(2, 3);
                p2 = new Position(3, 0);//3.0;

                break;
        }
        Position[] tab = {p1, p2};
        positionForme.put(forme, p1);
        GUI.moveForme(positionForme);
        endPosForme.put(forme, false);
        aidForms.put(forme, aid);
        return tab;
    }

    void remove_forme(String aid) {
        positionForme.remove(aid);

    }

    synchronized Position getFormePosFromName(String forme) {
        return (Position) positionForme.get(forme);
    }

    synchronized Position getFormePosFromAID(AID aid) {
        boolean containsValue = aidForms.containsValue(aid);
        if (containsValue) {
            Iterator i = aidForms.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry ps = (Map.Entry) i.next();
                AID id = (AID) ps.getValue();
                String forme = (String) ps.getKey();
                if (id.equals(aid) == true) {
                    return (Position) positionForme.get(forme);
                }
            }

        }
        return null;
    }

}
