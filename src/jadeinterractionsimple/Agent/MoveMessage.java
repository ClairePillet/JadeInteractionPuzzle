/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple.Agent;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Claire
 */
public class MoveMessage  {
    private ACLMessage msg;
    private MoveMessageContent content;

    public MoveMessage(ACLMessage msg) {
        try {
            this.msg = msg;
            Object o =msg.getContentObject();
          
            content=  MoveMessageContent.class.cast(o);
        } catch (UnreadableException ex) {
            Logger.getLogger(MoveMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ACLMessage getMsg() {
        return msg;
    }

    public void setMsg(ACLMessage msg) {
        this.msg = msg;
    }

    public MoveMessageContent getContent() {
        return content;
    }

    public void setContent(MoveMessageContent content) {
        this.content = content;
    }
    
    
    
}
