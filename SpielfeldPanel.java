 

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 * SpielfeldPanel erledigt die Darstellung eines AbaloneSpielfeldes
 * 
 * @author Peter Scholl / scholl@unterrichtsportal.org
 * @version 14.07.2016
 */
public class SpielfeldPanel extends JPanel implements MouseListener
{
    private int[][] spielfeld = new int[11][11];
    private int[][] markierungen = new int[11][11];

    /**
     * Konstruktor fuer Objekte der Klasse SpielfeldPanel
     */
    public SpielfeldPanel()
    {
        super(); // JPanel initialisieren
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = this.getWidth();
        int h = this.getHeight();
        if (h > Math.sqrt(3)/2*(double)w) {
            h = (int)(Math.sqrt(3)/2*(double)w);
        } else {
            w = (int)((double)h*2/Math.sqrt(3));
        }
        int r = (w/18); //Max Radius einer Kugel
        int bmx=-4*r; //Starteckpunkt x-Koord liegt links ausserhalb des Feldes
        int bmy= 0; //Starteckpunkt y-Koord
        g.setColor(Color.green);
        g.fillRect(0,0,w,h);
        for (int i=1;i<10 ;i++ ) {
            for (int j=1;j<10 ;j++ ) {
                if (i+j>5 && i+j<=14) {
                    if(markierungen[i][j]==1) {
                        g.setColor(Color.blue);
                        g.fillOval(bmx+(i-1)*2*r+(j-1)*r,bmy+(int)((double)(j-1)*Math.sqrt(3)*(double)r),2*r,2*r); 
                    }
                    switch (spielfeld[j][i]) {
                        case 2: 
                        g.setColor(Color.white); 

                        break;
                        case 1: 
                        g.setColor(Color.black);
                        break;
                        default: 
                        g.setColor(Color.gray);

                    } // end of switch
                    g.fillOval(1+bmx+(i-1)*2*r+(j-1)*r,1+bmy+(int)((double)(j-1)*Math.sqrt(3)*(double)r),2*r-2,2*r-2); 
                }
            } // end of for j 
        }  // end of for i          
    }

    public void zeige(int[][] pSpielfeld) {
        if (pSpielfeld==null) return;
        if (pSpielfeld.length != 11) {
            System.out.println("FEHLER - Spielfeldlaenge stimmt nicht");
            return;
        }
        if (pSpielfeld[0].length != 11) {
            System.out.println("FEHLER - Spielfeldhoehe stimmt nicht");
            return;
        }
        for (int i=0; i<11; i++) {
            for (int j=0; j<11; j++) {
                spielfeld[i][j]=pSpielfeld[i][j];
            }
        }
        this.repaint();
    }

    public void mouseExited(java.awt.event.MouseEvent e) {
    }

    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    public void mouseReleased(java.awt.event.MouseEvent evt) {
        System.out.println("Mouse Released");
        int x = evt.getX();
        int y = evt.getY();
        int w = this.getWidth();
        int h = this.getHeight();
        if (h > Math.sqrt(3)/2*(double)w) {
            h = (int)(Math.sqrt(3)/2*(double)w);
        } else {
            w = (int)((double)h*2/Math.sqrt(3));
        }
        int r = (w/18); //Max Radius einer Kugel
        double b = ((double)(y-r)/(Math.sqrt(3)*(double)r));
        double a = ((double)(x+3*r)/(double)(2*r)-b/2);
        int xpos = (int)Math.round(a);
        int ypos = (int)Math.round(b);
        System.out.println("b:"+b);
        System.out.println("a:"+a);
        System.out.println("x: "+Math.round(a)+" y: "+Math.round(b));

        markierungen[xpos+1][ypos+1]=1-markierungen[xpos+1][ypos+1];
        this.repaint();

    }

    public void mousePressed(java.awt.event.MouseEvent e) {
    }

    public void mouseClicked(java.awt.event.MouseEvent e) {
    }

}
