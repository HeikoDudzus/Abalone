
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
    private SpielfeldPanel sfeldPanel = new SpielfeldPanel(this); //Das Spielfeld
    private JTextField textfeld, tfBefehl;
    private JTextArea tA1;
    private JScrollPane sp1;
    private JButton button1, bSendCmd, bFlipBoard;
    private JPanel southPanel, infoPanelEast;
    private JLabel jlNameSpieler1, jlNameSpieler2, jlAmZug;
    private JCheckBox cbInstantmove, cbFlipBoard;
    private Spiel spiel;
    private int gameNr; //SpielNr auf dem Server
    private String nameSpieler1="Name2", nameSpieler2="Name2";
    private int meineSpielerNr = 0; // sollte 1 oder 2 sein - 0 ist Beobachter
    private int gewinner = 0; // 0-spiel laeuft noch sonst Spielernummer, die Gewinnt
    private ClientGUI myClientGUI = null; //ClientGUI von der das Brett gestartet wurde 
    private boolean boardFlipped = false;

    // Ende Attribute
    public AbaloneGUI(ClientGUI pClientGUI) {
        this();
        myClientGUI = pClientGUI;
    }

    public AbaloneGUI() { 
        // Frame-Initialisierung
        super("Abalone GUI - Test");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // Was soll auf Exit passieren - TODO muss noch geändert werden, dass nur das Spiel
        // verlassen wird
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    if (myClientGUI!= null) {
                        myClientGUI.removeAbaloneGUI(gameNr);
                    } else { //lokales Spiel
                        System.exit(0);
                    }
                }
            }
        );
        setSize(600,800);  //Breite und Hoehe des Frames
        setResizable(true);
        Container cp = getContentPane();  //Container des Frames holen um ihn zu f�llen
        cp.setLayout(new BorderLayout()); //BorderLayout
        // Anfang Komponenten
        sfeldPanel= new SpielfeldPanel(this);
        //sfeldPanel.setLocation(45,50); //x - y Koordinate
        sfeldPanel.setSize(420,360);   // breite und H�he
        sfeldPanel.addMouseListener(sfeldPanel);
        cp.add(sfeldPanel, BorderLayout.CENTER); //Spielfeld zum Container hinzufuegen

        infoPanelEast = new JPanel();
        infoPanelEast.setLayout(new BoxLayout(infoPanelEast, BoxLayout.Y_AXIS));
        JLabel jl1 = new JLabel("Spieler Schwarz:        ");
        infoPanelEast.add(jl1);
        jlNameSpieler1 = new JLabel(nameSpieler1);
        infoPanelEast.add(jlNameSpieler1);
        JLabel jl2 = new JLabel("Spieler Weiss:");
        infoPanelEast.add(jl2);
        jlNameSpieler2 = new JLabel(nameSpieler2);
        infoPanelEast.add(jlNameSpieler2);
        jlAmZug = new JLabel("");
        infoPanelEast.add(jlAmZug);
        cp.add(infoPanelEast, BorderLayout.EAST);

        textfeld = new JTextField();
        textfeld.setBounds(10,10,280,20);
        textfeld.setText("Ausgabefeld");
        cp.add(textfeld,BorderLayout.NORTH);

        southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());

        bFlipBoard = new JButton();
        bFlipBoard.setText("FlipBoard");
        bFlipBoard.addActionListener(this);
        southPanel.add(bFlipBoard);

        cbInstantmove = new JCheckBox("InstantMove");
        cbInstantmove.setSelected(true);
        southPanel.add(cbInstantmove);

        button1 = new JButton();
        //button1.setBounds(20, 440, 120, 20);
        button1.setText("Ziehe");
        button1.addActionListener(this);
        southPanel.add(button1);

        tfBefehl = new JTextField() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(300, 25);
            }
        };

        tfBefehl.setText("Befehl hier eingeben");
        //tfBefehl.setBounds(20, 580, 300, 25);
        tfBefehl.setSize(300,25);
        tfBefehl.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    bSendCmd_ActionPerformed(evt);
                }
            });
        southPanel.add(tfBefehl);
        bSendCmd = new JButton();
        //bSendCmd.setBounds(330, 580, 90, 25);
        bSendCmd.setText("SendCmd");
        bSendCmd.setMargin(new Insets(2, 2, 2, 2));
        bSendCmd.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    bSendCmd_ActionPerformed(evt);
                }
            });
        southPanel.add(bSendCmd);        

        cp.add(southPanel, BorderLayout.SOUTH);
        // Ende Komponenten
        this.pack();
        this.setVisible(true);
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
            ziehe();
            // } else if  (source == button2) { //gibt es noch nicht
        } else if (source == bFlipBoard) {
            flipBoard();
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

    public void moveCompleted() {
        if (cbInstantmove.isSelected()) {
            ziehe();
            sfeldPanel.markierungenEntfernen();
        }
    }

    public void ziehe() {
        if (sfeldPanel.gibZug() != null && gewinner == 0) {
            // es wurden drei Felder ausgewählt und noch niemand hat gewonnen
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
    }

    public void setActive(boolean pActive) {
        // ich bin am Zug
        // das sollte angezeigt werden
        if (pActive) {
            jlAmZug.setBackground(Color.red);
            jlAmZug.setOpaque(true);
            jlAmZug.setText("Du bist dran!");
        } else {
            jlAmZug.setOpaque(false);
            jlAmZug.setText("");
        }
        //this.repaint();
    }

    public void setzeNameSpieler1(String pName) {
        nameSpieler1 = pName;
        jlNameSpieler1.setText(pName+"\n");
    }

    public void setzeNameSpieler2(String pName) {
        nameSpieler2 = pName;
        jlNameSpieler2.setText(pName+"\n");
    }

    public void setzeMeineSpielerNr(int pNr) {
        System.out.println("Meine Nr: "+pNr);
        meineSpielerNr = pNr;

        if (pNr == 1) {
            jlNameSpieler1.setText(nameSpieler1+"(*)\n");
        } else if (pNr == 2) {
            jlNameSpieler2.setText(nameSpieler2+"(*)\n");
            flipBoard();
        }
    }

    public void setzeGewinner(int pNr) {
        gewinner=pNr;
        if (meineSpielerNr == gewinner) {
            textfeld.setText("Du hast gewonnen!");
        } else if (meineSpielerNr > 0) {
            textfeld.setText("Du hast verloren!");            
        } else {
            textfeld.setText("Spieler "+pNr+" hat gewonnen!");
        }

    }

    public void flipBoard() {
        boardFlipped = !boardFlipped;
        sfeldPanel.setBoardFlipped(boardFlipped);
        this.repaint();
    }

    public void setzeSpielerAmZug(int pNr) {
        sfeldPanel.setSpielerAmZug(pNr);
    }
    // Ende Methoden
} // end of class VierGUI
