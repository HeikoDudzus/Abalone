
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
    private Position pos1 = new Position(1,2);
    private Position pos2 = new Position(2,3);
    private Position pos3 = new Position(3,4);
    private int zustandPositionsClick = 0; // 0 noch nichts, 1 pos1 gewählt, 2 pos3 gewählt
    private int spielerAmZug = 0; //0 niemand, 1 Schwarz, 2 Weiß
    private int xposPressed, yposPressed;
    private AbaloneGUI myAbaloneGUI;
    private boolean boardFlipped = false;
    

    /**
     * Konstruktor fuer Objekte der Klasse SpielfeldPanel
     */
    public SpielfeldPanel(AbaloneGUI pAG)
    {
        super(); // JPanel initialisieren
        myAbaloneGUI = pAG;
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
        if (spielerAmZug==1) {
            g.setColor(Color.black);
            g.fillOval(2,2,r,r);
        } else if (spielerAmZug==2) {
            g.setColor(Color.white);
            g.fillOval(2,2,r,r);
        } else {
            g.setColor(Color.black);
            g.drawOval(2,2,r,r);
        }
        for (int i=1;i<10 ;i++ ) {
            for (int j=1;j<10 ;j++ ) {
                if (i+j>5 && i+j<=14) {
                    if(markierungen[i][j]==1) {
                        g.setColor(Color.blue);
                        g.fillOval(bmx+(i-1)*2*r+(j-1)*r,bmy+(int)((double)(j-1)*Math.sqrt(3)*(double)r),2*r,2*r); 
                    }
                    if(markierungen[i][j]==2) {
                        g.setColor(Color.red);
                        g.fillOval(bmx+(i-1)*2*r+(j-1)*r,bmy+(int)((double)(j-1)*Math.sqrt(3)*(double)r),2*r,2*r); 
                    }
                    if(markierungen[i][j]==3) {
                        g.setColor(Color.yellow);
                        g.fillOval(bmx+(i-1)*2*r+(j-1)*r,bmy+(int)((double)(j-1)*Math.sqrt(3)*(double)r),2*r,2*r); 
                    }
                    if (!boardFlipped) {
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
                    } else {
                        switch (spielfeld[10-j][10-i]) {
                            case 2: 
                            g.setColor(Color.white); 

                            break;
                            case 1: 
                            g.setColor(Color.black);
                            break;
                            default: 
                            g.setColor(Color.gray);

                        } // end of switch
                    }
                    g.fillOval(1+bmx+(i-1)*2*r+(j-1)*r,1+bmy+(int)((double)(j-1)*Math.sqrt(3)*(double)r),2*r-2,2*r-2); 
                }
            } // end of for j 
        }  // end of for i          
    }

    /**
     * Teile dem Layout-Manager mit, wie groß diese Komponente sein soll.
     * 
     * @return Die bevorzugte Größe (als Dimension) dieser Komponente.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 400);
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

    /**
     * Falls ein Zug ausgewaehlt werden die drei Positionen zu diesem zurueckgegeben
     * ansonsten null
     */
    public Position[] gibZug() {
        if (zustandPositionsClick == 3 && !boardFlipped) {
            return new Position[] {pos1, pos2, pos3};
        } else if (zustandPositionsClick == 3 && boardFlipped) {
            return new Position[] {pos1.flipped(), pos2.flipped(), pos3.flipped()};
        } else {
            return null;
        }
    }

    public void mouseExited(java.awt.event.MouseEvent e) {
    }

    public void mouseEntered(java.awt.event.MouseEvent evt) {
        System.out.println("Entered: "+evt.getX()+", "+evt.getY());
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

        switch(zustandPositionsClick) {
            case 0: //noch keine Wahl erfolgt
            if ((xpos != xposPressed || ypos != yposPressed)) {
                pos1 = new Position(xposPressed+1, 9-yposPressed);
                pos2 = new Position(xposPressed+1, 9-yposPressed);
                pos3 = new Position(xpos+1, 9-ypos);
                markierungen[xposPressed+1][yposPressed+1]=1;
                markierungen[xposPressed+1][yposPressed+1]=2;
                markierungen[xpos+1][ypos+1]=3;
                zustandPositionsClick = 3;   
                myAbaloneGUI.moveCompleted();
            } else if ((spielfeld[ypos+1][xpos+1]!=0 && !boardFlipped) ||
                       (spielfeld[9-ypos][9-xpos]!=0 && boardFlipped)) { //dort liegt eine Kugel - AChtung Tausch!!
                markierungen[xpos+1][ypos+1]=1;
                pos1 = new Position(xpos+1,9-ypos);
                zustandPositionsClick++;
            }
            break;
            case 1: //basisFeld schon gewählt
            markierungen[xpos+1][ypos+1]=2;
            pos2 = new Position(xpos+1,9-ypos );
            zustandPositionsClick++;
            break;
            case 2: //Reihe schon gewählt
            if (markierungen[xpos+1][ypos+1]==1) {
                markierungen[pos1.gibX()][10-pos1.gibY()]=0;
                markierungen[pos2.gibX()][10-pos2.gibY()]=0;
                zustandPositionsClick=0;
            } else {
                markierungen[xpos+1][ypos+1]=3;
                pos3 = new Position(xpos+1,9-ypos );
                zustandPositionsClick++;
                myAbaloneGUI.moveCompleted();
            }
            break;
            default:
            this.markierungenEntfernen();
            break;
        }
        System.out.println("ZustandPositionsClick:"+zustandPositionsClick);
        System.out.println("Position1: "+pos1.gibX()+", "+pos1.gibY());
        System.out.println("Position2: "+pos2.gibX()+", "+pos2.gibY());
        System.out.println("Position3: "+pos3.gibX()+", "+pos3.gibY());

        this.repaint();

    }

    public void mousePressed(java.awt.event.MouseEvent evt) {
        //So kann mittels eines Clicks gezogen werden
        // zu ziehende Kugel wird angeclickt, man bewegt sich 
        // mit gedrueckter Maustaste auf das Zielfeld und laesst
        // dort die Maustaste los
        System.out.println("Mouse Pressed");
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
        xposPressed = (int)Math.round(a);
        yposPressed = (int)Math.round(b);
        System.out.println("b:"+b);
        System.out.println("a:"+a);
        System.out.println("x: "+Math.round(a)+" y: "+Math.round(b));

    }

    public void mouseClicked(java.awt.event.MouseEvent e) {
    }

    public int gibSpielerDesZuges() {
        //TODO: Pruefen ob überhaupt Zug ausgewaehlt
        if (!boardFlipped) {
            return spielfeld[10-pos1.gibY()][pos1.gibX()];
        } else {
            return spielfeld[10-pos1.flipped().gibY()][pos1.flipped().gibX()];
        }
        //return spielerAmZug;
    }

    public void markierungenEntfernen() {
        markierungen[pos1.gibX()][10-pos1.gibY()]=0;
        markierungen[pos2.gibX()][10-pos2.gibY()]=0;
        markierungen[pos3.gibX()][10-pos3.gibY()]=0;
        zustandPositionsClick=0;
    }

    public void setBoardFlipped(boolean pBF) {
        boardFlipped = pBF;
    }
    
    public void setSpielerAmZug(int pSNr) {
        spielerAmZug = pSNr;
    }

}
