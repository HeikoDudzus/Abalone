
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

/*
 * Zur Erinnerung
 * 
 * Um in einem Textfeld mit Scrollbalken am Ende zu bleiben (aus Kniffel von Jens):
 *      tA1.setText(ausgabe);
 *       int len = tA1.getDocument().getLength();
 *       tA1.setCaretPosition(len);
 *       
 *       tA1 = new JTextArea();
 *       JScrollPane lleiste1 = new JScrollPane(tA1);
 *       lleiste1.setBounds(460, 480, 420, 100);
 *       lleiste1.setBackground(new Color(255, 255, 255));
 */

public class AbaloneGUI extends JFrame implements ActionListener { //ActionListener erst mal weglassen
    // Anfang Attribute
    private SpielfeldPanel sfeldPanel = new SpielfeldPanel(); //Das Spielfeld
    private JTextField textfeld, tfBefehl;
    private JTextArea tA1;
    private JScrollPane sp1;
    private JButton button1, bSendCmd;
    private Spiel spiel;
    private int gameNr; //SpielNr auf dem Server
    private ClientGUI myClientGUI = null; //ClientGUI von der das Brett gestartet wurde 

    // Ende Attribute
    public AbaloneGUI(ClientGUI pClientGUI) {
        this();
        myClientGUI = pClientGUI;
    }

    public AbaloneGUI() { 
        // Frame-Initialisierung
        super("Abalone GUI - Test");
        //setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600,800);  //Breite und Hoehe des Frames
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

        tA1 = new JTextArea();
        tA1.setEditable(false);
        JScrollPane sP1 = new JScrollPane(tA1);
        sP1.setBounds(20, 470, 420, 100);
        sP1.setBackground(new Color(255, 255, 255));
        cp.add(sP1);

        tfBefehl = new JTextField();
        tfBefehl.setBounds(20, 580, 300, 25);
        tfBefehl.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    bSendCmd_ActionPerformed(evt);
                }
            });
        cp.add(tfBefehl);
        bSendCmd = new JButton();
        bSendCmd.setBounds(330, 580, 90, 25);
        bSendCmd.setText("SendCmd");
        bSendCmd.setMargin(new Insets(2, 2, 2, 2));
        bSendCmd.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    bSendCmd_ActionPerformed(evt);
                }
            });
        cp.add(bSendCmd);        

        // Ende Komponenten
        setVisible(true);
    } // end of public VierGUI

    /**
     * zeige zeichnet ein uebergebenes Spielfeld aus int
     * 0 - leer, 1- schwarz, 2-weiß - wird einfach an das JPanel uebergeben
     */
    public void zeige(int[][] pSpielfeld) {
        sfeldPanel.zeige(pSpielfeld);
        sfeldPanel.repaint();
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
                if (spiel!= null) System.out.println("Name: "+spiel.gibSpielerNr(spielerNr).gibName());

                if (myClientGUI != null) {
                    //Zug an Server schicken
                    myClientGUI.send("MOVE "+gameNr+" "+felder[0]+" "+felder[1]+" "+felder[2]);
                } else {
                    //lokales Spiel
                    System.out.println(spiel.schiebe(felder[0], felder[1], new Vektor(felder[0],felder[2]), spiel.gibSpielerNr(spielerNr)));
                    this.zeigeSpiel();
                }
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

    public void bSendCmd_ActionPerformed(ActionEvent evt) {
        // TODO hier Quelltext einfuegen (Send Command aus jTextField3)
        System.out.println("Sende Commando:"+tfBefehl.getText());
        tfBefehl.setText("");
    } // end of bSendCmd_ActionPerformed    
    
    public void setGameNr(int pGameNr) { gameNr = pGameNr; }
    // Ende Methoden
} // end of class VierGUI
