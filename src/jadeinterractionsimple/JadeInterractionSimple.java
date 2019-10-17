/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import jadeinterractionsimple.Agent.FormeAgent;
import jadeinterractionsimple.Controller.Environnement;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author claire
 */
public class JadeInterractionSimple {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Runtime runtime = Runtime.instance();
        Profile config = new ProfileImpl("localhost", 8888, null);
        config.setParameter("gui", "true");
        AgentContainer mc = runtime.createMainContainer(config);
        AgentController acA;
        AgentController acB;

        Environnement env = new Environnement(5,20);

        try {
            Object[] param = {env};
            acA = mc.createNewAgent("A", FormeAgent.class.getName(), param);
            acA.start();

            acB = mc.createNewAgent("B", FormeAgent.class.getName(), param);
            acB.start();

            acB = mc.createNewAgent("C", FormeAgent.class.getName(), param);
            acB.start();

            acB = mc.createNewAgent("D", FormeAgent.class.getName(), param);
            acB.start();

            acB = mc.createNewAgent("E", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("F", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("G", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("H", FormeAgent.class.getName(), param);
            acB.start();
            
            acB = mc.createNewAgent("I", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("J", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("K", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("L", FormeAgent.class.getName(), param);
            acB.start();
            
            acB = mc.createNewAgent("M", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("N", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("O", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("P", FormeAgent.class.getName(), param);
            acB.start();
            
            acB = mc.createNewAgent("Q", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("R", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("S", FormeAgent.class.getName(), param);
            acB.start();
            acB = mc.createNewAgent("T", FormeAgent.class.getName(), param);
            acB.start();
            
//            acB = mc.createNewAgent("U", FormeAgent.class.getName(), param);
//            acB.start();
////            acB = mc.createNewAgent("V", FormeAgent.class.getName(), param);
//            acB.start();
//            acB = mc.createNewAgent("W", FormeAgent.class.getName(), param);
//            acB.start();
//            acB = mc.createNewAgent("X", FormeAgent.class.getName(), param);
//            acB.start();
        } catch (StaleProxyException ignored) {
        }
    }

}
