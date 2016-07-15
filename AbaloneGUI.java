 

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * Rudimentaere GUI-Entwicklung fuer ein Abalone Spiel
 *
 * @version 14.07.2016
 * @author scholl@unterrichtsportal.org
 */

public class AbaloneGUI extends JFrame implements ActionListener { //ActionListener erst mal weglassen
    // Anfang Attribute
    private SpielfeldPanel sfeldPanel = new SpielfeldPanel(); //Das Spielfeld
    private JTextField textfeld;
    private JButton button1;
    private Spiel spiel;

    // Ende Attribute

    public AbaloneGUI() { 
        // Frame-Initialisierung
        super("Abalone GUI - Test");
        //setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600,800);  //Breite und H�he des Frames
        setResizable(false);
        Container cp = getContentPane();  //Container des Frames holen um ihn zu f�llen
        cp.setLayout(null); //kein automatisches Layout der Objekte
        // Anfang Komponenten
        sfeldPanel= new SpielfeldPanel();
        sfeldPanel.setLocation(45,50); //x - y Koordinate
        sfeldPanel.setSize(420,360);   // breite und H�he
        sfeldPanel.addMouseListener(sfeldPanel);
        cp.add(sfeldPanel); //Spielfeld zum Container hinzuf�gen

        textfeld = new JTextField();
        textfeld.setBounds(10,10,280,20);
        textfeld.setText("Ausgabefeld");
        cp.add(textfeld);

        button1 = new JButton();
        button1.setBounds(20, 440, 120, 20);
        button1.setText("Ziehe");
        button1.addActionListener(this);
        cp.add(button1);

        // Ende Komponenten

        setVisible(true);
    } // end of public VierGUI

    /**
     * zeige zeichnet ein uebergebenes Spielfeld aus int
     * 0 - leer, 1- schwarz, 2-weiß - wird einfach an das JPanel uebergeben
     */
    public void zeige(int[][] pSpielfeld) {
        sfeldPanel.zeige(pSpielfeld);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource(); //Quelle der Action ermitteln
        if (source == button1) {
            //Zug ziehen
            if (sfeldPanel.gibZug() != null) {
                // es wurden drei Felder ausgewählt
                Position[] felder = sfeldPanel.gibZug();
                int spielerNr = sfeldPanel.gibSpielerDesZuges();
                System.out.println("Pos1: "+felder[0].gibX()+", "+felder[0].gibY());
                System.out.println("Pos2: "+felder[1].gibX()+", "+felder[1].gibY());
                System.out.println("Pos3: "+felder[2].gibX()+", "+felder[2].gibY());
                System.out.println("SpielerNr: "+spielerNr);
                System.out.println("Name: "+spiel.gibSpielerNr(spielerNr).gibName());
                
                System.out.println(spiel.schiebe(felder[0], felder[1], new Vektor(felder[0],felder[2]), spiel.gibSpielerNr(spielerNr)));
                this.zeigeSpiel();
            }
            // } else if  (source == button2) { //gibt es noch nicht
        } 
    }

    public void nachrichtAnzeigen(String text) {
        textfeld.setText(text);
    }

    public void testStartSpielfeldAnzeigen() {
        int[][] tfeld = new int[][] {{0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,1,1,1,1,1,0},
                {0,0,0,0,1,1,1,1,1,1,0},
                {0,0,0,0,0,1,1,1,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,2,2,2,0,0,0,0,0},
                {0,2,2,2,2,2,2,0,0,0,0},
                {0,2,2,2,2,2,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0}};
       sfeldPanel.zeige(tfeld);
    }
    
    public void setzeSpiel(Spiel pSpiel) {
        spiel = pSpiel;
    }
    
    public void zeigeSpiel() {
        if (spiel!=null) zeige(spiel.toIntegerArray());
    }
    // Ende Methoden
} // end of class VierGUI
