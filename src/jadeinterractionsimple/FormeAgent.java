/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.Map;

/**
 *
 * @author claire
 */
public class FormeAgent extends Agent {

    private Position posBegin;
    private Position posEnd;
    private String forme;
    private Environnement env;

    private boolean isGoodPlace = false;
    private boolean isGridOK = false;

    public void setup() {
        Object[] args = getArguments();
        env = (Environnement) args[0];
        forme = getLocalName();
        Position[] tab = env.add_forme(getAID(), forme);
        posBegin = tab[0];
        posEnd = tab[1];
        // Init :

        addBehaviour(new Routine());
    }

    public class Routine extends CyclicBehaviour {

        public void action() {
            //check grid is ok

            if (env.gridIsOK() == false) {
                System.out.println(getLocalName() + " checkGrid false");
                isGoodPlace = posBegin.equals(posEnd);
                if (isGoodPlace == false) {
                    Position newP = env.aStarSearch(posBegin, posEnd);
                    move(newP);
                    System.out.println(newP + " a*");
                } else {
//                setFormeOKNOK //get message if someone want him to move
                    env.setFormeOKNOK(forme, true);
                    System.out.println(getLocalName() + " is OK");
                }
            } else {
                System.out.println("Finish");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.err.format("IOException : %s%n", e);
            }
        }

        public void itineraireReflexionLow() {
            System.out.println(getLocalName() + " place false");
            boolean haveMove = false;
            if (posBegin.getX() != posEnd.getX() && haveMove == false) {
                // move on x
                if (posBegin.getX() > posEnd.getX()) {
                    //try move x-1

                    Position futurePos = new Position(posBegin.getX() - 1, posBegin.getY());
                    haveMove = move(futurePos);
                    System.out.println(getLocalName() + " x-1" + haveMove);
                } else {
                    //try move x+1 
                    Position futurePos = new Position(posBegin.getX() + 1, posBegin.getY());
                    haveMove = move(futurePos);
                    System.out.println(getLocalName() + " x+1" + haveMove);
                }
            }
            if (posBegin.getY() != posEnd.getY() && haveMove == false) {
                //move on y 
                if (posBegin.getY() > posEnd.getY()) {
                    //try move y-1
                    Position futurePos = new Position(posBegin.getX(), posBegin.getY() - 1);
                    haveMove = move(futurePos);
                    System.out.println(getLocalName() + " y-1" + haveMove);
                } else {
                    //try move y+1 
                    Position futurePos = new Position(posBegin.getX(), posBegin.getY() + 1);
                    haveMove = move(futurePos);
                    System.out.println(getLocalName() + " y+1" + haveMove);
                }
            }
//                 if(haveMove==false){
//                       //or ask for message 
//                        ACLMessage move = new ACLMessage(ACLMessage.PROPOSE);
//                        move.addReceiver((AID)caseTest.getValue());
//                         move.setContent("Move");
//                        myAgent.send(move);
//                                           
//                 }

        }

        public boolean move(Position futurePos) {
            //check if case is free
            Map.Entry caseTest = env.caseIsFree(futurePos);
            if (caseTest == null) {
                //move
                env.move_forme(forme, futurePos);
                posBegin = futurePos;
                return true;
            }
            return false;
        }
    }

}
