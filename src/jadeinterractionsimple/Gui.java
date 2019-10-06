/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeinterractionsimple;

import jade.core.AID;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author claire
 */
public class Gui extends JFrame {

  
    private HashMap<String,Position> positions;
    private Image dbImageA;
     private Image dbImageB; 
     private Image dbImageD;
      private Image dbImageC;
       private Image dbImageE;
     private Image dbImageF; 
     private Image dbImageG;
      private Image dbImageH;
         private Image dbImage;
     
    private painting_area canvas;

    public Gui() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {

        dbImage = ImageIO.read(new File("test.jpg"));
   
             dbImageA = ImageIO.read(new File("A.jpg"));
        dbImageB = ImageIO.read(new File("B.jpg"));
        dbImageC = ImageIO.read(new File("C.jpg"));
        dbImageD = ImageIO.read(new File("D.jpg"));
             dbImageE = ImageIO.read(new File("E.jpg"));
        dbImageF = ImageIO.read(new File("F.jpg"));
        dbImageG = ImageIO.read(new File("G.jpg"));
        dbImageH = ImageIO.read(new File("H.jpg"));
        canvas = new painting_area();
        add(canvas);

        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                canvas.repaint();
            }
        };

        new Timer(500, taskPerformer).start();

    }

    void move_guide(HashMap<String,Position> positions) {
//         Position[] positionsTab = (Position[]) positions.values().toArray(new Position[0]);
//         
//        x_forme = new Integer[positions.size()];
//        y_forme = new Integer[positions.size()];
//        int compteur= 0;
//        for (Position pos : positionsTab) {
//           System.out.println(pos);
//            y_forme[compteur] = pos.getY();
//            x_forme[compteur] = pos.getX();
//            compteur ++;
//        }
        this.positions =positions;
    }

   
    public class painting_area extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //
           
            g.drawImage(dbImage, 0, 0, getWidth(), getHeight(), this);
                       
                       //A
             g.drawImage(dbImageA, positions.get("A").getX()*100+25,positions.get("A").getY()*100+25,
                     25, 
                     25, this);
              //B
             g.drawImage(dbImageB, positions.get("B").getX()*100+25,positions.get("B").getY()*100+25,
                     25, 25, this); 
                //C
             g.drawImage(dbImageC, positions.get("C").getX()*100+25,positions.get("C").getY()*100+25, 
                    25, 25, this); 
            //D
             g.drawImage(dbImageD, positions.get("D").getX()*100+25,positions.get("D").getY()*100+25, 
                     25, 25, this);
             
                      //A
             g.drawImage(dbImageE, positions.get("E").getX()*100+25,positions.get("E").getY()*100+25,
                     25, 
                     25, this);
              //B
             g.drawImage(dbImageF, positions.get("F").getX()*100+25,positions.get("F").getY()*100+25,
                     25, 25, this); 
                //C
             g.drawImage(dbImageG, positions.get("G").getX()*100+25,positions.get("G").getY()*100+25, 
                    25, 25, this); 
            //D
             g.drawImage(dbImageH, positions.get("H").getX()*100+25,positions.get("H").getY()*100+25, 
                     25, 25, this);
             
        }
    }
}

