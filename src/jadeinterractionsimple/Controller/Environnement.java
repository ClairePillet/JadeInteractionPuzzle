/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple.Controller;

import jadeinterractionsimple.Vue.Gui;
import jade.core.AID;
import jadeinterractionsimple.Agent.AStar;
import jadeinterractionsimple.Graph;
import jadeinterractionsimple.Node;
import jadeinterractionsimple.Position;
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
    volatile Map<Position, String> positionForme;
    volatile Map<String, Boolean> endPosForme;
    private Gui GUI = new Gui();
    private Graph g;
    boolean verbose = false;
    private  int nbForme;

    public Environnement(Integer size_grid,int nbForme) {
        size = size_grid;
        positionForme = new ConcurrentHashMap<Position, String>();
        endPosForme = new ConcurrentHashMap<String, Boolean>();
        aidForms = new ConcurrentHashMap<String, AID>();
        GUI.setSize(500, 500);
        GUI.setLocation(700, 20);
        GUI.setVisible(true);
        GUI.validate();
        this.nbForme=nbForme;
        createGraph(size_grid);
    }

     synchronized void createGraph(int sizeGrid) {
        g = new Graph();
        ArrayList<Node> lst = new ArrayList<Node>();
        for (int i = 0; i < sizeGrid; i++) {
            for (int j = 0; j < sizeGrid; j++) {
                Position p = new Position(i, j);
                positionForme.put(p,"NONE");
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

    public synchronized void setFormeOKNOK(String forme, boolean state) {
        endPosForme.remove(forme);
        endPosForme.put(forme, state);
    }

    public synchronized boolean moveForme(String forme, Position new_position, Position oldP, boolean back) {
            
            if(!positionForme.get(new_position).equals("NONE")){
                return false;
                
            }
           if(back!=true){
               
               positionForme.replace( oldP,"NONE");
           }
         
           positionForme.replace( new_position,forme);
        
        GUI.moveForme(positionForme);
        return true;
        
    }

    public synchronized Position moveOneCase(Position actual, Position new_position) {
        Set<Node> lst = g.getNode(actual).getAdjnode();

        Position pPossible = null;
        for (Node n : lst) {
            if (n.getPos().equals(new_position) == false ) {
                if (caseIsFree(n.getPos(),null) == null) {
                    return n.getPos();
                }
                pPossible = n.getPos();
            }
        }

        return pPossible;
    }

    synchronized boolean checkBlock(Position actual) {
        Set<Node> lst = g.getNode(actual).getAdjnode();
        for (Node nAdj : lst) {
            if (caseIsFree(nAdj.getPos(),null) == null) {
                return false;
            }
        }

        return true;
    }
    public synchronized boolean ready(){
        return endPosForme.size()==nbForme;
    }
    public synchronized AID caseIsFree(Position new_position,AID aidMe) {

            String forme=positionForme.get(new_position);
            if(forme.equals("NONE")){
                return null;
            }
            AID aid = aidForms.get(forme);
            if (aidMe==null ||!aid.equals(aidMe)) {
                
                return aid;
            }
        
        return null;
    }

    public synchronized boolean gridIsOK() {
        Boolean[] positionsTab = (Boolean[]) endPosForme.values().toArray(new Boolean[0]);

        for (Boolean state : positionsTab) {
            if (state == false) {
                return false;
            }
        }
        return true;
    }

    public synchronized Position[] add_forme(AID aid, String forme) {

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
                p1 = new Position(4, 0);
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
                p1 = new Position(4, 3);
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
                 case "M":
                p1 = new Position(0, 4);
                p2 = new Position(2, 2);

                break;
            case "N":
                p1 = new Position(2, 4);
                p2 = new Position(3, 2);

                break;
            case "O":
                p1 = new Position(4, 4);
                p2 = new Position(4, 2);

                break;
            case "P":
                p1 = new Position(3, 4);
                p2 = new Position(0, 3);
                break;
                case "Q":
                p1 = new Position(4, 1);
                p2 = new Position(1, 3);

                break;
            case "R":
                p1 = new Position(2, 1);
                p2 = new Position(2, 3);

                break;
            case "S":
                p1 = new Position(3, 1);
                p2 = new Position(3, 3);

                break;
            case "T":
                p1 = new Position(1, 0);
                p2 = new Position(4, 3);
                break;
                  case "U":
                p1 = new Position(1, 3);
                p2 = new Position(0, 4);

                break;
            case "V":
                p1 = new Position(0, 1);
                p2 = new Position(1, 4);

                break;
            case "W":
                p1 = new Position(0, 3);
                p2 = new Position(2, 4);

                break;
            case "X":
                p1 = new Position(1, 0);
                p2 = new Position(3, 4);
                break;
            default://d
                p1 = new Position(2, 3);
                p2 = new Position(3, 0);//3.0;

                break;
        }
        Position[] tab = {p1, p2};
        
      String done=  positionForme.replace( p1,forme);
        GUI.moveForme(positionForme);
        endPosForme.put(forme, false);
        aidForms.put(forme, aid);
        return tab;
    }
    public synchronized Position getNone(Position p) {
        Position  minPos=null;
        int minDist=10000;
         Iterator i = positionForme.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry ps = (Map.Entry) i.next();
                String id = (String) ps.getValue();
                Position pos = (Position) ps.getKey();
                if (id.equals("NONE") == true) {
                    int d=p.distance(pos);
                    if(minDist>d){
                        minDist=d;
                        minPos=pos;
                    }
                    //return (Position) p;
                }
            }
          
        return minPos;

    }
    public void remove_forme(String aid) {
        positionForme.remove(aid);

    }

    public synchronized Position getFormePosFromName(String forme) {
          Iterator i = positionForme.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry ps = (Map.Entry) i.next();
                String id = (String) ps.getValue();
                Position pos = (Position) ps.getKey();
                if (id.equals(forme) == true) {
                    return (Position) pos;
                }
            }
        return null;
    }

    public synchronized Position getFormePosFromAID(AID aid) {
        boolean containsValue = aidForms.containsValue(aid);
        if (containsValue) {
            Iterator i = aidForms.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry ps = (Map.Entry) i.next();
                AID id = (AID) ps.getValue();
                String forme = (String) ps.getKey();
                if (id.equals(aid) == true) {
                    return getFormePosFromName(forme);
                }
            }

        }
        return null;
    }

}
