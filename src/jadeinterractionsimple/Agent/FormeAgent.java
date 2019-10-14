/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple.Agent;

import jadeinterractionsimple.Controller.Environnement;
import jade.core.AID;
import jade.core.Agent;
import static jade.core.Agent.AP_ACTIVE;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import jadeinterractionsimple.Node;
import jadeinterractionsimple.Position;
import java.awt.Shape;
import java.awt.geom.Line2D;
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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author claire
 */
public class FormeAgent extends Agent {

    private AStar aStar;
    private int timeWait = 0;
    private Position posBegin;
    private Position posEnd;
    private MoveMessage msgRecieve;
    private ACLMessage msgSend;
    private String forme;
    private Environnement env;
    private ArrayList<AID> exclusionList = null;
    private LinkedList<Node> path = null;
     private AID lastAgentIMoveFor;
    private AID personneToAnswerAfterMove;
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
        exclusionList = new ArrayList<>();
        path = new LinkedList<>();
        addBehaviour(new Routine());
    }

    public class Routine extends CyclicBehaviour {

        public void action() {
            //check grid is ok

            if (env.gridIsOK() == false) {
                messageGestion();
                aStarWhithCom();
            } else {
                System.out.println("Finish");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.err.format("IOException : %s%n", e);
            }
        }
        public void messageGestion(){
             ACLMessage msgR = receive();
            while(msgR!=null){
                int performative = msgR.getPerformative();
                if (performative == ACLMessage.PROPOSE) {
                    if (msgRecieve == null) {
                        if(lastAgentIMoveFor!= null && lastAgentIMoveFor.equals(msgR.getSender())){
                           
                             sendMsg("AlreadyMoveForYou", ACLMessage.REJECT_PROPOSAL, msgR.getSender());
                   
                             lastAgentIMoveFor=null;
                        }else{
                             hasReceiveMsg = true;
                        msgRecieve = new MoveMessage(msgR);
                        mustSendAnswerAfterMoved = false;
                        System.out.println(getLocalName() + " receive a msg");
                            
                        }
                      
                    } else {
                        sendMsg("ToBusy", ACLMessage.REJECT_PROPOSAL, msgR.getSender());
                    }
                }
                if (performative == ACLMessage.REJECT_PROPOSAL) {
                    if(msgSend!=null && msgSend.getAllReceiver().next().equals(msgR.getSender())){
                        msgSend = null;
                    waitOK = false;
                    mustSendAnswerAfterMoved = false;
                    exclusionList.add(msgR.getSender());
                    }
                    
                }
//               
                if (performative == ACLMessage.INFORM) {
                    if (msgR.getContent() != null) {
                        if (msgR.getContent().contains("free")) {
                            waitOtherMove = false;
                        }
                        if (msgR.getContent().contains("YouCanMove")) {
                            waitOK = false;
                            mustSendAnswerAfterMoved = true;
                            personneToAnswerAfterMove=msgR.getSender();
                           
                        } 
                    }
                }
                msgR = receive();
            }
            
        }
        public State checkState() {
            isGoodPlace = posBegin.equals(posEnd);
            env.setFormeOKNOK(forme, isGoodPlace);
           
            if (msgRecieve != null && waitOtherMove == false) {
                if (isGoodPlace) {// final palce but have msg to move
                    return State.MOVEFORMSG;
                } else if (waitOK) {// wait for move
                        return State.WAITANSWER;
                    }else {
                    try {
                        AID reciver = null;
                        AID sender = null;
                        if (msgSend != null) {
                            reciver = (AID) msgSend.getAllReceiver().next();
                        }
                        if (msgRecieve != null) {
                            sender = (AID) msgRecieve.getMsg().getSender();
                        }
                        
                        if (sender != null && reciver != null && reciver.equals(sender)) { // oposition muttulay send msg
                            return State.OPPOSITION;
                        } else if (waitOK == true) {// wait to move
                            return State.WAITANSWER;
                        } else {
                            return State.MOVEFORMSG;// avoid agent
                        }
                      
                    } catch (Exception ex) {

                        Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else if (waitOtherMove) {
                return State.WAITOTHERMOVE;
            } else {
                if (isGoodPlace) {// final place 
                    return State.FINISH;
                } else {
                    if (waitOK) {// wait for move
                        return State.WAITANSWER;
                    }
                    return State.MOVETOFINISH;// normaly move
                }
            }
            return State.FINISH;
        }

        synchronized void sendMsg(String Content, int Performative, AID reciver) {
            ACLMessage msg = new ACLMessage(Performative);
            msg.setContent(Content);
            msg.addReceiver(reciver);
            myAgent.send(msg);
            System.out.println(getLocalName() + " send a msg" + reciver + " " + Content);
        }

        public void sendMsgWithContent(MoveMessageContent Content, int Performative, AID reciver) throws IOException {
           // if( msgSend==null){
                  msgSend = new ACLMessage(Performative);
            Date d = new Date();
            msgSend.setConversationId(d.getTime() + getLocalName());
            msgSend.setContentObject(Content);
            msgSend.addReceiver(reciver);
            myAgent.send(msgSend);
waitOK=true;
            System.out.println(getLocalName() + " send a msg" + reciver + " " + Content.toString());
            }
          
        //}

        public void aStarWhithCom() {
            State state = checkState();
            System.out.println(getLocalName() + " " + state);
            switch (state) {
                case MOVETOFINISH:// move may have an obstacle
                    path = aStar.aStarSearch(posBegin, posEnd, exclusionList);
                    AID obstacle = move(path.get(0).getPos());
                    if (obstacle != null) {

                        try {
                            Position nextPos = path.size() > 1 ? path.get(1).getPos() : path.get(0).getPos();// on envoie la direction que l'on souhaite atteindre 
                            sendMsgWithContent(new MoveMessageContent(path.size(), nextPos), ACLMessage.PROPOSE, obstacle);
                            
                        } catch (IOException ex) {
                            Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        path.remove(0);
                        if (msgRecieve != null) {
                            sendMsg("YouCanMove", ACLMessage.INFORM, msgRecieve.getMsg().getSender());
                            hasReceiveMsg = false;
                             lastAgentIMoveFor=msgRecieve.getMsg().getSender();
                            msgRecieve = null;
                               
                            waitOtherMove = true;
                        }
                        if (mustSendAnswerAfterMoved) {
                            sendMsg("free", ACLMessage.INFORM, personneToAnswerAfterMove);
                            mustSendAnswerAfterMoved = false;
                            personneToAnswerAfterMove=null;
                            msgSend = null;
                            System.out.println(getLocalName() + " msgsendNUll238");
                        }
                    }

                    break;

                case MOVEFORMSG: // need to move for an agent
                    LinkedList<Node> pathToAvoid = aStar.findClosestWhite(posBegin, msgRecieve.getContent().getDirection(), msgRecieve.getMsg().getSender());//env.getFormePosFromAID(msgRecieve.getMsg().getSender()));
                    Position p = null;
                    if (pathToAvoid.size() > 0) {
                        p = pathToAvoid.get(0).getPos();
                    }

                    obstacle = move(p);
                    if (obstacle == null) {
                        sendMsg("YouCanMove", ACLMessage.INFORM, msgRecieve.getMsg().getSender());
                        hasReceiveMsg = false;
                         lastAgentIMoveFor=msgRecieve.getMsg().getSender();
                        msgRecieve = null;
                        
                        waitOtherMove = true;
                        path = aStar.aStarSearch(posBegin, posEnd, exclusionList);
                        if (mustSendAnswerAfterMoved) {
                            
                            sendMsg("free", ACLMessage.INFORM, personneToAnswerAfterMove);
                            mustSendAnswerAfterMoved = false;
                            personneToAnswerAfterMove=null;
                            msgSend = null;
                        }
                        msgRecieve = null;
                        hasReceiveMsg = false;
                    } else {
                        try {
                            
                            sendMsgWithContent(new MoveMessageContent(Integer.MAX_VALUE, p), ACLMessage.PROPOSE, obstacle);

                        } catch (IOException ex) {
                            Logger.getLogger(FormeAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    break;

                case FINISH:
                    break;
                case OPPOSITION: //oposition case 

                    boolean imove = false;
                    if (path.size() < (msgRecieve.getContent().getSizePathLeft())) {
                        imove = true;
                    } else if (path.size() == (msgRecieve.getContent().getSizePathLeft())) {
                        int i = msgSend.getConversationId().compareTo(msgRecieve.getMsg().getConversationId());
                        if (i > 0) {
                            imove = true;
                        } else if (i < 0) {
                            imove = false;
                        } else {
                            Random rd = new Random();
                            imove = rd.nextBoolean();
                        }

                    }
                    if (imove == false) {
                        sendMsg("NotMove", ACLMessage.REJECT_PROPOSAL, (AID) msgRecieve.getMsg().getSender());
                        hasReceiveMsg = false;
                        msgRecieve = null;
                        System.out.println(getLocalName() + "not move");
                    } else {
                        sendMsg("IMove", ACLMessage.ACCEPT_PROPOSAL, (AID) msgRecieve.getMsg().getSender());
                        msgSend = null;
                        System.out.println(getLocalName() + " move");
                    }

                    break;
                case WAITANSWER://wait
                    timeWait++;
                    if (timeWait > 2) {
                        timeWait = 0;
                        waitOK = false;
                        if(msgSend!=null){
                            exclusionList.add(msgSend.getSender());
                        msgSend=null;
                        }
                          
                      
                    }

                    break;
                case WAITOTHERMOVE:
                    timeWait++;
                    
                    if (timeWait > 2) {
                        timeWait = 0;
                        waitOtherMove = false;
                        msgRecieve=null;
                    }
            break;
                default:
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
                env.moveForme(forme, futurePos);
                posBegin = futurePos;
                return null;
            }
            return caseTest;
        }
    }

}
