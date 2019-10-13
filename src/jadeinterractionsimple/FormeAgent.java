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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author claire
 */
public class FormeAgent extends Agent {

    private AStar aStar;
    private Position posBegin;
    private Position posEnd;
    private MoveMessage msgRecieve;
    private ACLMessage msgSend;
    private String forme;
    private Environnement env;
    private ArrayList<AID> exclusionList = null;
    private LinkedList<Node> path = null;
    private boolean mustSendAnswerAfterMoved = false;
    private boolean hasReceiveMsg = false;
    private boolean waitOK = false;
    private boolean waitOtherMove = false;
    private boolean isGoodPlace = false;
    private boolean isGridOK = false;

    private enum State {
        WAITANSWER,
        WAITOTHERMOVE,
        FINISH,
        MOVETOFINISH,
        MOVEFORMSG,
        OPPOSITION;

    }

    public void setup() {
        Object[] args = getArguments();
        env = (Environnement) args[0];
        forme = getLocalName();
        Position[] tab = env.add_forme(getAID(), forme);
        posBegin = tab[0];
        posEnd = tab[1];
        aStar = new AStar(env);
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

        public State checkState() {
            isGoodPlace = posBegin.equals(posEnd);
            env.setFormeOKNOK(forme, isGoodPlace);
           ACLMessage msgR = receive();
        
            if (msgR != null) {
                 
                int performative = msgR.getPerformative();
                if (performative == ACLMessage.PROPOSE) {
                    hasReceiveMsg = true;
                      msgRecieve=new MoveMessage(msgR);
                    System.out.println(getLocalName() + " receive a msg");
                }
                if (performative == ACLMessage.REJECT_PROPOSAL) {
                    msgSend = null;
                    waitOK = false;
                    exclusionList = new ArrayList<>();
                    exclusionList.add(msgR.getSender());
                }
                if (performative == ACLMessage.INFORM) {
                    if (msgR.getContent() != null) {
                        if (msgR.getContent().contains("free")) {
                            msgSend = null;
                            waitOtherMove = false;
                        } else {
                            waitOK = false;
                            mustSendAnswerAfterMoved = true;
                        }
                    }
                }
            }
            if (msgRecieve != null && waitOtherMove == false) {
                if (isGoodPlace) {// a sa place mais doit bouger
                    return State.MOVEFORMSG;
                } else if (waitOtherMove) {//a liberer sa place a ttend la validation de l'autre 
                    return State.WAITOTHERMOVE;
                } else {
                    try {
                        AID reciver = null;
                        AID sender = null;
                        if (msgSend != null) {
                            reciver = (AID) msgSend.getAllReceiver().next();
                        }
                        if (msgRecieve != null) {
                            sender = (AID) msgRecieve.getMsg().getSender();
                        }

                        if (sender != null && reciver != null && reciver.equals(sender)) { // il s'oppose et s'envoie des msg mutuelement
                            return State.OPPOSITION;
                        } else if (waitOK == true) {// il n'est pas a sa place et continu a bouger il ne genera olus l'autre
                            return State.WAITANSWER;
                        } else {
                            return State.MOVEFORMSG;// esquive l'agent
                        }
                        //else if(!posBegin.equals(((MoveMessage) msgRecieve.getContentObject()).getDirection())) {
//                            return 1;
//                        }
                    } catch (Exception ex) {

                        Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else if (waitOtherMove) {
                return State.WAITOTHERMOVE;
            } else {
                if (isGoodPlace) {// il est a la bonne place mais ne fait rien 
                    return State.FINISH;
                } else {
                    if (waitOK) {// il attend pour bouger
                        return State.WAITANSWER;
                    }
                    return State.MOVETOFINISH;// il bouge normalement 
                }
            }
            return State.FINISH;
        }

        

        public void aStarWhithCom() {
            State state = checkState();
            System.out.println(getLocalName() + " " + state);
            switch (state) {
                case MOVETOFINISH:// move may have an obstacle
                    path = aStar.aStarSearch(posBegin, posEnd, exclusionList);
                    AID obstacle = move(path.get(0).getPos());
                    if (obstacle != null) {
                        System.out.println(getLocalName() + " send a msg");
                        msgSend = new ACLMessage(ACLMessage.PROPOSE);
                        Date d = new Date();
                        msgSend.setConversationId(d.getTime() + getLocalName());
                        msgSend.addReceiver(obstacle);
                        try {
                            Position nextPos = path.size() > 1 ? path.get(1).getPos() : path.get(0).getPos();// on envoie la direction que l'on souhaite atteindre 
                             msgSend.setContentObject(new MoveMessageContent(path.size(), nextPos));
                            
                            myAgent.send(msgSend);
                            waitOK = true;
                        } catch (IOException ex) {
                            Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        path.remove(0);
                    }
                    if (msgRecieve != null) {
                        ACLMessage cantMove = new ACLMessage(ACLMessage.INFORM);
                        // cantMove.setPerformative(ACLMessage.INFORM);
                        cantMove.addReceiver(msgRecieve.getMsg().getSender());
                        cantMove.setContent("YouMove");
                        myAgent.send(cantMove);
                        hasReceiveMsg = false;
                        msgRecieve = null;

                    }
                    if (mustSendAnswerAfterMoved) {
                        ACLMessage cantMove = new ACLMessage(ACLMessage.INFORM);
                        cantMove.addReceiver((AID) msgSend.getAllReceiver().next());
                        cantMove.setContent("free");
                        myAgent.send(cantMove);
                        mustSendAnswerAfterMoved = false;
                    }
                    break;

                case MOVEFORMSG: // he is in place but need to move for an agent
                   
                        Position  p=env.moveoneCase(posBegin, msgRecieve.getContent().getDirection());
                        if(p==null){
                             ACLMessage cantMove = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                            cantMove.addReceiver(msgRecieve.getMsg().getSender());
                            cantMove.setContent("cantmove");
                            // cantMove.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            myAgent.send(cantMove);
                            hasReceiveMsg = false;
                            msgRecieve = null;
                            break;
                        }
                        obstacle = move(p);
                        if (obstacle == null) {
                            ACLMessage cantMove = new ACLMessage(ACLMessage.INFORM);
                            // cantMove.setPerformative(ACLMessage.INFORM);
                            cantMove.addReceiver(msgRecieve.getMsg().getSender());
                            cantMove.setContent("YouMove");
                            myAgent.send(cantMove);
                            hasReceiveMsg = false;
                            msgRecieve = null;
                            waitOtherMove = true;
                            path = aStar.aStarSearch(posBegin, posEnd, exclusionList);
                        } else {
                            ACLMessage cantMove = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                            cantMove.addReceiver(msgRecieve.getMsg().getSender());
                            cantMove.setContent("cantmove");
                            // cantMove.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            myAgent.send(cantMove);
                            hasReceiveMsg = false;
                            msgRecieve = null;
                        }
                        if (mustSendAnswerAfterMoved) {
                            ACLMessage cantMove = new ACLMessage(ACLMessage.INFORM);
                            cantMove.addReceiver((AID) msgSend.getAllReceiver().next());
                            cantMove.setContent("free");
                            myAgent.send(cantMove);
                            mustSendAnswerAfterMoved = false;
                        }
                        msgRecieve = null;
                        hasReceiveMsg = false;
                     

                    break;

                case OPPOSITION: //oposition case 
                    
                        boolean imove = false;
                        if (path.size() < (msgRecieve.getContent().getSizePathLeft())) {
                            imove = true;
                        } else if (path.size() == ( msgRecieve.getContent().getSizePathLeft())) {

                            imove = msgSend.getConversationId().compareTo(msgRecieve.getMsg().getConversationId()) > 1 ? true : false;
                        }
                        if (imove == false) {

                            ACLMessage notMove = new ACLMessage(ACLMessage.REJECT_PROPOSAL);;
                            //notMove.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            notMove.addReceiver(msgRecieve.getMsg().getSender());
                            notMove.setContent("NotMove");
                            myAgent.send(notMove);
                            hasReceiveMsg = false;
                            msgRecieve = null;
                            System.out.println(getLocalName() + "not move");
                        } else {
                            ACLMessage Move = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                            //  Move.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                            Move.addReceiver(msgRecieve.getMsg().getSender());
                            Move.setContent("IMove");
                            myAgent.send(Move);
                            msgSend = null;
                            waitOK = false;
                            System.out.println(getLocalName() + " move");
                        }

                    break;
                default://State.FINISH //WAIT
                    break;
            }
        }

        public void aStarWhithoutCom() {
            System.out.println(getLocalName() + " checkGrid false");
            isGoodPlace = posBegin.equals(posEnd);
            if (isGoodPlace == false) {
                Position newP = aStar.aStarSearch(posBegin, posEnd, exclusionList).get(0).getPos();
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
