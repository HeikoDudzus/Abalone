 

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
        button1.setBounds(20, 400, 120, 20);
        button1.setText("Neues Spiel");
        button1.addActionListener(this);
        cp.add(button1);

        // Ende Komponenten

        setVisible(true);
    } // end of public VierGUI

    // Anfang Methoden
    public void sfeldPanel_MouseReleased(MouseEvent evt) {
        int wf = sfeldPanel.getWidth()/7; // Spielfeldbreite durch 7 = spaltenbreite
        int reihe = Math.min((int) ((double)evt.getX()/wf),7); //x-Koordinate des Clicks / Spaltenbreite 
    } // end of sfeldPanel_MouseReleased

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
            //Neues Spiel starten
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
    // Ende Methoden
} // end of class VierGUI
