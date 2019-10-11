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
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author claire
 */
public class FormeAgent extends Agent {

    private int state = 0;
    private Position posBegin;
    private Position posEnd;
    private ACLMessage msgRecieve;
    private ACLMessage msgSend;
    private String forme;
    private Environnement env;
    private LinkedList<Node> path = null;
    private boolean hasReceiveMsg = false;
    private boolean waitOK = false;
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
        path = new LinkedList<>();
        addBehaviour(new Routine());
    }

    public class Routine extends CyclicBehaviour {

        public void action() {
            //check grid is ok

            if (env.gridIsOK() == false) {
                aStarWhithCom();
            } else {
                System.out.println("Finish");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.format("IOException : %s%n", e);
            }
        }

        public int checkState() {
            isGoodPlace = posBegin.equals(posEnd);
            env.setFormeOKNOK(forme, isGoodPlace);
            msgRecieve = receive();
            if (msgRecieve != null) {
                int performative = msgRecieve.getPerformative();
                if (performative == ACLMessage.PROPOSE) {
                    hasReceiveMsg = true;
                    System.out.println(getLocalName() + " receive a msg");
                }
                
            }
            if (hasReceiveMsg == true) {
                if (isGoodPlace) {
                    return 3;
                } else {
                    AID reciver =null;
                    if(msgSend!=null)
                    {
                       reciver= (AID) msgSend.getAllReceiver().next();
                    }
                    AID sender = (AID) msgRecieve.getSender();
                    if (sender == reciver) { // il se rentre dedans
                        return 5;
                    } else if(waitOK==false){// i natturaly move in other case
                        return 3;
                    }else{
                        return 2;
                    }
                }
            } else {
                if (isGoodPlace) {
                    return 0;
                } else {
                    if (waitOK) {
                        return 2;
                    }
                    return 1;
                }
            }

        }

        public void aStarWhithCom() {
            state = checkState();
            switch (state) {
                case 1:// move may have an obstacle
                    path = env.aStarSearch(posBegin, posEnd);
                    AID obstacle = move(path.get(0).getPos());
                    if (obstacle != null) {
                        System.out.println(getLocalName() + " send a msg");
                        msgSend = new ACLMessage(ACLMessage.PROPOSE);
                        msgSend.addReceiver(obstacle);
                        try {
                            Position nextPos = path.size() > 1 ? path.get(1).getPos() : path.get(0).getPos();// on envoie la direction que l'on souhaite atteindre 
                            msgSend.setContentObject(new MoveMessage(path.size(), path.get(1).getPos()));
                            myAgent.send(msgSend);
                            waitOK = true;
                        } catch (IOException ex) {
                            Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 2: // we wait for answer

                    break;
                case 3: // final place but need to move for an agent
                    try {
                        obstacle = move(env.moveoneCase(((MoveMessage) msgRecieve.getContentObject()).getDirection()));
                        if (obstacle != null) {
                            ACLMessage cantMove = msgRecieve.createReply();
                            cantMove.addReceiver(msgRecieve.getSender());
                            cantMove.setContent("CantMove");
                            myAgent.send(cantMove);
                        } else {
                            ACLMessage cantMove = msgRecieve.createReply();
                            cantMove.addReceiver(msgRecieve.getSender());
                            cantMove.setContent("OK");
                            myAgent.send(cantMove);
                        }
                    } catch (UnreadableException ex) {
                        Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    msgRecieve = null;
                    hasReceiveMsg = false;
                    break;
                case 4: //juste move normally 

                break;
                case 5: //oposition case 
                        try {
                            boolean imove=false;
                        if (path.size() < (((MoveMessage) msgRecieve.getContentObject()).getSizePathLeft())) {
                             imove=true;
                        }else if(path.size() == (((MoveMessage) msgRecieve.getContentObject()).getSizePathLeft())) {
                            imove=msgSend.getPostTimeStamp()<msgRecieve.getPostTimeStamp();
                        }
                        if( imove==false){
                            //dont move
                            ACLMessage notMove = msgRecieve.createReply();
                            notMove.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            notMove.addReceiver(msgRecieve.getSender());
                            notMove.setContent("NotMove");
                            myAgent.send(notMove);
                            hasReceiveMsg=false;
                        }else{
                             ACLMessage Move = msgRecieve.createReply();
                            Move.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                            Move.addReceiver(msgRecieve.getSender());
                            Move.setContent("IMove");
                            myAgent.send(Move);
                            msgSend=null;
                            waitOK=false;
                            
                        }
                         } catch (UnreadableException ex) {
                        Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                break;
                default://0
                    break;
            }
        }

        public void aStarWhithComold() {

            if (hasReceiveMsg == true) {
                try {
                    //on verrifie que l'on ne bouge pas deja
                    //on bouge si notre temps de trajet est  cours ou long
                    if (path.size() <= (((MoveMessage) msgRecieve.getContentObject()).getSizePathLeft())) {
                        AID obstacle = move(env.moveoneCase(((MoveMessage) msgRecieve.getContentObject()).getDirection()));
                        if (obstacle != null) {
                            ACLMessage cantMove = msgRecieve.createReply();
                            cantMove.addReceiver(msgRecieve.getSender());
                            cantMove.setContent("CantMove");
                            myAgent.send(cantMove);
                        } else {
                            ACLMessage cantMove = msgRecieve.createReply();
                            cantMove.addReceiver(msgRecieve.getSender());
                            cantMove.setContent("OK");
                            myAgent.send(cantMove);
                        }
                    }
                    msgRecieve = null;
                    hasReceiveMsg = false;
                } catch (UnreadableException ex) {
                    Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((isGoodPlace == false) && !waitOK) {
                path = env.aStarSearch(posBegin, posEnd);
                AID obstacle = move(path.get(0).getPos());
                if (obstacle != null) {
                    System.out.println(getLocalName() + " send a msg");
                    msgSend = new ACLMessage(ACLMessage.PROPOSE);
                    msgSend.addReceiver(obstacle);
                    try {
                        Position nextPos = path.size() > 1 ? path.get(1).getPos() : path.get(0).getPos();// on envoie la direction que l'on souhaite atteindre 
                        msgSend.setContentObject(new MoveMessage(path.size(), path.get(1).getPos()));
                    } catch (IOException ex) {
                        Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    myAgent.send(msgSend);
                }

            } else if (waitOK == true) {
                //get message
                ACLMessage msg = receive();

                if (msg.getContent() == "OK") {
                    waitOK = false;

                }
            } else {

                env.setFormeOKNOK(forme, true);
                System.out.println(getLocalName() + " is OK");
            }
            //get message if someone want him to move
            msgRecieve = receive();
            if (msgRecieve != null) {
                int performative = msgRecieve.getPerformative();
                if (performative == ACLMessage.PROPOSE) {
                    hasReceiveMsg = true;
                    System.out.println(getLocalName() + " receive a msg");
                }
            }
        }

        public void aStarWhithoutCom() {
            System.out.println(getLocalName() + " checkGrid false");
            isGoodPlace = posBegin.equals(posEnd);
            if (isGoodPlace == false) {
                Position newP = env.aStarSearch(posBegin, posEnd).get(0).getPos();
                move(newP);
                System.out.println(newP + " a*");
            } else {
//                setFormeOKNOK //get message if someone want him to move
                env.setFormeOKNOK(forme, true);
                System.out.println(getLocalName() + " is OK");
            }
        }

        public void itineraireBasic() {
            System.out.println(getLocalName() + " place false");
            boolean haveMove = false;
            if (posBegin.getX() != posEnd.getX() && haveMove == false) {
                // move on x
                if (posBegin.getX() > posEnd.getX()) {
                    //try move x-1

                    Position futurePos = new Position(posBegin.getX() - 1, posBegin.getY());
                    haveMove = move(futurePos) == null ? true : false;
                    System.out.println(getLocalName() + " x-1" + haveMove);
                } else {
                    //try move x+1 
                    Position futurePos = new Position(posBegin.getX() + 1, posBegin.getY());
                    haveMove = move(futurePos) == null ? true : false;
                    System.out.println(getLocalName() + " x+1" + haveMove);
                }
            }
            if (posBegin.getY() != posEnd.getY() && haveMove == false) {
                //move on y 
                if (posBegin.getY() > posEnd.getY()) {
                    //try move y-1
                    Position futurePos = new Position(posBegin.getX(), posBegin.getY() - 1);
                    haveMove = move(futurePos) == null ? true : false;
                    System.out.println(getLocalName() + " y-1" + haveMove);
                } else {
                    //try move y+1 
                    Position futurePos = new Position(posBegin.getX(), posBegin.getY() + 1);
                    haveMove = move(futurePos) == null ? true : false;
                    System.out.println(getLocalName() + " y+1" + haveMove);
                }
            }

        }

        public AID move(Position futurePos) {
            //check if case is free
            AID caseTest = env.caseIsFree(futurePos);
            if (caseTest == null) {
                //move
                env.move_forme(forme, futurePos);
                posBegin = futurePos;
                return null;
            }
            return caseTest;
        }
    }

}
