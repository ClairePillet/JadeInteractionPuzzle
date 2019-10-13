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
    volatile Map<String, Position> position_Forme;
    volatile Map<String, Boolean> EndPos_Forme;
    private Integer[] position_B;
    private Integer[] position_C;
    private Integer[] position_D;
    private Gui GUI = new Gui();
    private Graph g;
    //private gui_musee GUI = new gui_musee();
    boolean verbose = false;

    Environnement(Integer size_grid) {
        size = size_grid;
        position_Forme = new ConcurrentHashMap<String, Position>();
        EndPos_Forme = new ConcurrentHashMap<String, Boolean>();
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
            int iMax = 0;
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
//            for (Node nAdj : lst) {
//                Position posAdj = nAdj.getPos(), pos = n.getPos();
//
//                if (!pos.equals(posAdj)) {
//                    if (((posAdj.getX() == pos.getX() - 1 || posAdj.getX() == pos.getX() + 1) && posAdj.getY() == pos.getY()) || ((posAdj.getY() == pos.getY() - 1 || posAdj.getY() == pos.getY() + 1) && posAdj.getX() == pos.getX())) {
//                        n.addNodeAdj(nAdj);
//                        iMax++;
//                    }
//                    if (iMax == 4) {
//                        break;
//                    }
//                }
//            }
            g.addNode(n);
        }
    }

    public Graph getG() {
        return g;
    }

    synchronized void setFormeOKNOK(String forme, boolean state) {
        EndPos_Forme.remove(forme);
        EndPos_Forme.put(forme, state);
    }

    synchronized void move_forme(String forme, Position new_position) {
        position_Forme.remove(forme);
        position_Forme.put(forme, new_position);
        GUI.moveForme(position_Forme);
    }

    synchronized Position moveoneCase(Position actual, Position new_position) {
        Set<Node> lst = g.getNode(actual).getAdjnode();
        for (Node n : lst) {
            if (n.getPos().equals(new_position) == false) {
                if (caseIsFree(n.getPos()) == null) {
                    return n.getPos();
                }
            }
        }
        return null;
    }

    synchronized AID caseIsFree(Position new_position) {

        Iterator i = position_Forme.entrySet().iterator();
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
        Boolean[] positionsTab = (Boolean[]) EndPos_Forme.values().toArray(new Boolean[0]);

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
                p1 = new Position(3, 2);
                p2 = new Position(2, 1);

                break;
        
            default://d
                p1 = new Position(2, 3);
                p2 = new Position(3, 0);//3.0;

                break;
        }
        Position[] tab = {p1, p2};
        position_Forme.put(forme, p1);
        GUI.moveForme(position_Forme);
        EndPos_Forme.put(forme, false);
        aidForms.put(forme, aid);
        return tab;
    }

    void remove_forme(String aid) {
        position_Forme.remove(aid);

    }

    synchronized Position getFormePosFromName(String forme) {
        return (Position) position_Forme.get(forme);
    }

    synchronized Position getFormePosFromAID(AID aid) {
        boolean containsValue = aidForms.containsValue(aid);
        if (containsValue) {
            Iterator i = aidForms.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry ps = (Map.Entry) i.next();
                AID id = (AID) ps.getValue();
                String  forme = (String) ps.getKey();
                if (id.equals(aid) == true) {
                    return (Position) position_Forme.get(forme);
                }
            }

        }
        return null;
    }

}
