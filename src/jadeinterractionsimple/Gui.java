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
        dbImageA = ImageIO.read(new File("A.png"));
        dbImageB = ImageIO.read(new File("b.png"));
        dbImageC = ImageIO.read(new File("c.jpg"));
        dbImageD = ImageIO.read(new File("d.jpg"));
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
             
             
               
        
          
          
        }
    }
}

