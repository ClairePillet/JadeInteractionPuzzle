/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import jade.core.AID;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author claire
 */
public class Environnement {
     private Integer size;
      private HashMap<String,AID> aidForms; 
    private HashMap<String,Position> position_Forme; 
    private HashMap<String,Boolean> EndPos_Forme; 
    private Integer[] position_B; 
    private Integer[] position_C; 
    private Integer[] position_D; 
   private Gui GUI = new Gui();
    //private gui_musee GUI = new gui_musee();
    boolean verbose = false;

    Environnement(Integer size_grid){
        size = size_grid;
       position_Forme= new HashMap<String, Position>();
      EndPos_Forme= new HashMap<String, Boolean>();
      aidForms= new HashMap<String, AID>();
        GUI.setSize( 500, 500 );
        GUI.setLocation( 700, 20 );
        GUI.setVisible( true );
        GUI.validate();

    }
 synchronized void setFormeOKNOK(String forme, boolean state){
        EndPos_Forme.remove(forme);
        EndPos_Forme.put(forme, state);
        
    }
    synchronized void move_forme(String forme, Position new_position){
        position_Forme.remove(forme);
        position_Forme.put(forme, new_position);
        GUI.move_guide(position_Forme);
    }
 synchronized Map.Entry caseIsfree(Position new_position){
      
         Iterator i=position_Forme.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry ps = (Map.Entry) i.next();
           if(ps.equals(new_position)==true){
               
               return ps;
           }
        }
        return null;
    }

    synchronized boolean gridIsOK(){
         Boolean[] positionsTab = (Boolean[]) EndPos_Forme.values().toArray(new Boolean[0]);
     
        for (Boolean state : positionsTab) {
           if(state==false){
               return false;
           }
        }
        return true;
    }

     Position[] add_forme(AID aid, String forme){
      
        Position p1 ;
        Position p2;
       
         System.out.println(forme);
        switch(forme){
                case "A":
                    p1=new Position (1,0);
                    p2=new Position (1,0);
                  
                    break;
                case "B":
                    p1=new Position (0,1);
                    p2=new Position (0,1);
                  
                 break;
                case "C":
                    p1=new Position (3,1);
                    p2=new Position (0,0);
                   
                break; 
                default:
                    p1=new Position (1,4);
                    p2=new Position (0,4);
                   
                break;
        }
           Position[] tab= {p1,p2};
        position_Forme.put(forme, p1);
            GUI.move_guide(position_Forme);
            EndPos_Forme.put(forme,false);
            aidForms.put(forme, aid);
        return tab;
    }

   

    void remove_forme(String aid){
        position_Forme.remove(aid);
      //  GUI.move_tourist(position_tourist);
    }

    synchronized Position get_forme_position(String forme){
        return (Position) position_Forme.get(forme);
    }

   
}
