import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.HashMap;
/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 01.01.2016
 * @author 
 */

public class ClientGUI extends JFrame {
    // Anfang Attribute
    private JLabel jLabel1 = new JLabel();
    private JLabel jLabel3 = new JLabel();
    private JTextField jTextField1 = new JTextField();
    private JLabel jLabel4 = new JLabel();
    private JTextField jTextField2 = new JTextField();
    private JLabel jLabel5 = new JLabel();
    private JButton jButton4 = new JButton();
    private JList jList1 = new JList();
    private DefaultListModel jList1Model = new DefaultListModel();
    private JScrollPane jList1ScrollPane = new JScrollPane(jList1);
    private JTextArea tA1;
    private JScrollPane sP1;
    // Ende Gui-Attribute
    int spieltyp=0; //0-Einzel, 1-Versus, 2-Team - TODO Sollte eigentlich weg
    private MyClient client;
    private boolean isConnected = false;
    private int participatesInGame = -1; //GameNummer, -1 wenn kein Spiel
    private boolean isPlaying = false; //Spielt gerade ein Spiel
    private boolean isMaster = false; //Ist Master des Spiels
    private boolean adjustScrollbar = true; //Trigger f�r aktualisierung der Scrollbar
    //folgende HashMap soll die Liste der Spiele aufnehmen, die man Joinen kann (ComboBox)
    private HashMap<Integer,String> offenespiele = new HashMap<Integer,String>();

    private JButton jButton5 = new JButton();
    private JButton jButton6 = new JButton();
    private JButton jButton1 = new JButton();
    private String comboBoxListe[] = {};
    private JComboBox spielAuswahl;
    private JButton jButton2 = new JButton();
    private JTextField jTextField3 = new JTextField();
    private JButton jButton7 = new JButton();
    private JLabel jLabel14 = new JLabel();
    private JTextField jTextField10 = new JTextField();
    private JButton jButton8 = new JButton();
    // Ende Attribute

    public ClientGUI() { 
        // Frame-Initialisierung
        super("Abalone-Client");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        int frameWidth = 345; 
        int frameHeight = 475;
        setSize(frameWidth, frameHeight);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        setResizable(false);
        Container cp = getContentPane();
        cp.setLayout(null);
        // Anfang Komponenten

        jLabel1.setBounds(24, 16, 200, 23);
        jLabel1.setText("Abalone-Client");
        jLabel1.setFont(new Font("Dialog", Font.BOLD, 16));
        cp.add(jLabel1);
        jLabel3.setBounds(8, 104, 46, 20);
        jLabel3.setText("Server:");
        cp.add(jLabel3);
        jTextField1.setBounds(56, 104, 150, 20);
        jTextField1.setText("localhost");
        cp.add(jTextField1);
        jLabel4.setBounds(224, 104, 46, 20);
        jLabel4.setText("Port:");
        cp.add(jLabel4);
        jTextField2.setBounds(264, 104, 46, 20);
        jTextField2.setText("55555");
        cp.add(jTextField2);
        jButton4.setBounds(16, 248, 73, 18);
        jButton4.setText("Create");
        jButton4.setMargin(new Insets(2, 2, 2, 2));
        jButton4.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    jButton4_ActionPerformed(evt);
                }
            });
        cp.add(jButton4);
        
        tA1 = new JTextArea();
        tA1.setEditable(false);
        JScrollPane sP1 = new JScrollPane(tA1);
        sP1.setBounds(16, 296, 305, 113);
        sP1.setBackground(new Color(255, 255, 255));
        cp.add(sP1);
        
                
        
        cp.setBackground(Color.WHITE);
        jButton5.setBounds(16, 271, 73, 18);
        jButton5.setText("Leave");
        jButton5.setMargin(new Insets(2, 2, 2, 2));
        jButton5.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    jButton5_ActionPerformed(evt);
                }
            });
        jButton5.setEnabled(false);
        cp.add(jButton5);
        jButton6.setBounds(94, 248, 73, 18);
        jButton6.setText("Join");
        jButton6.setMargin(new Insets(2, 2, 2, 2));
        jButton6.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    jButton6_ActionPerformed(evt);
                }
            });
        cp.add(jButton6);
        jButton1.setBounds(94, 271, 73, 18);
        jButton1.setText("Start");
        jButton1.setMargin(new Insets(2, 2, 2, 2));
        jButton1.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    jButton1_ActionPerformed(evt);
                }
            });
        cp.add(jButton1);
        //JComboBox f�r Spieleintr�ge wird erstellt
        spielAuswahl = new JComboBox(comboBoxListe);
        //JComboBox wird Panel hinzugef�gt
        spielAuswahl.setBounds(172,249,151,18);
        cp.add(spielAuswahl);

        jButton2.setBounds(172, 271, 73, 18);
        jButton2.setText("OffeneSpiele");
        jButton2.setMargin(new Insets(2, 2, 2, 2));
        jButton2.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    jButton2_ActionPerformed(evt);
                }
            });
        cp.add(jButton2);        

        jTextField3.setBounds(16, 416, 201, 25);
        jTextField3.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    jButton7_ActionPerformed(evt);
                }
            });
        cp.add(jTextField3);
        jButton7.setBounds(224, 416, 75, 25);
        jButton7.setText("SendCmd");
        jButton7.setMargin(new Insets(2, 2, 2, 2));
        jButton7.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    jButton7_ActionPerformed(evt);
                }
            });
        cp.add(jButton7);
        jLabel14.setBounds(8, 136, 46, 20);
        jLabel14.setText("Status:");
        cp.add(jLabel14);
        jTextField10.setBounds(56, 136, 150, 20);
        jTextField10.setText("not connected");
        cp.add(jTextField10);
        jButton8.setBounds(232, 136, 75, 20);
        jButton8.setText("Connect");
        jButton8.setMargin(new Insets(2, 2, 2, 2));
        jButton8.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    jButton8_ActionPerformed(evt);
                }
            });
        cp.add(jButton8);
        // Ende Komponenten
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    System.exit(0); }
            }
        );
        setButtonActiveDeActive();
        setVisible(true);
    } // end of public ClientGUI

    // Anfang Methoden

    public static void main(String[] args) {
        new ClientGUI();
    } // end of main


    public void jButton4_ActionPerformed(ActionEvent evt) { //Create
        //Spiel wird erzeugt
        client.execServerCmd("c");
    } // end of jButton4_ActionPerformed

    public void jButton5_ActionPerformed(ActionEvent evt) { //Leave
        client.execServerCmd("q");
    } // end of jButton5_ActionPerformed

    public void jButton6_ActionPerformed(ActionEvent evt) { //Join
        //pr�fen ob auswahl getroffen wurde
        int index = spielAuswahl.getSelectedIndex();
        if (index == -1) {
            //evtl. als Messagebox
            System.out.println("Kein Spiel ausgew�hlt oder kein Spiel in der Liste - Liste aktualisieren");
        } else {
            String spiel = (String)spielAuswahl.getSelectedItem();
            String[] spielNr = spiel.split(" ");
            if (spielNr.length>0) {
                client.execServerCmd("j "+spielNr[0]);
            } else {
                System.out.println("FEHLER: Spielnummer konnte nicht ermittelt werden");
            }
        }
    } // end of jButton6_ActionPerformed

    public void jButton1_ActionPerformed(ActionEvent evt) { //Start
        client.execServerCmd("r");
    } // end of jButton6_ActionPerformed

    public void jButton2_ActionPerformed(ActionEvent evt) { //Aktualisiere offene Spiele
        client.execServerCmd("l o");
    } // end of jButton6_ActionPerformed

    public void jButton7_ActionPerformed(ActionEvent evt) {
        // TODO hier Quelltext einfügen (Send Command aus jTextField3)
        if (client!=null && client.isConnected()) {
            jList1Model.addElement("Sende Commando:"+jTextField3.getText());
            client.execServerCmd(jTextField3.getText());
            jTextField3.setText("");
        } else {
            jList1Model.addElement("Client nicht verbunden");
        }
    } // end of jButton7_ActionPerformed

    public void jButton8_ActionPerformed(ActionEvent evt) {
        // Connect / Disconnect-Button
        if (!isConnected) {
            setConnected(false);
            if (client == null || !client.isConnected()) {
                try {
                    client = new MyClient(jTextField1.getText(),Integer.parseInt(jTextField2.getText()), this);

                    Thread thread = new Thread(client);
                    thread.start();
                } catch (Exception ex) {
                    textAusgeben(ex.getMessage());
                }
            }
        } else { //Disconnect
            if (client!=null) {
                client.beenden();
                client=null;
            }
        }
        if (client != null && client.isConnected()) {
            setConnected(true);
        } else {
            setConnected(false);
        }
    } // end of jButton8_ActionPerformed (Disconnect / Connect)

    // Ende Methoden
    synchronized public void textAusgeben(String pText) {
        // Updates der Liste m�ssen wohl durch den EventDispatcherThread
        // ausgef�hrt werden (was auch immer das ist)
        // von http://www.java-forum.org/thema/jlist-defaultlistmodel-race-condition.73198/
        
        tA1.append(pText+"\n");
        int len = tA1.getDocument().getLength();
        tA1.setCaretPosition(len);
        
        //this.repaint();
    }

    public void setConnected(boolean pIsConnected) {
        if (pIsConnected) {
            jTextField10.setText("connected");
            jButton8.setText("Disconnect");
            isConnected=true;
        } else {
            jTextField10.setText("not connected");
            jButton8.setText("Connect");
            isConnected=false;
            isMaster=false;
            participatesInGame=-1;
            isPlaying=false;
        }
        setButtonActiveDeActive();
    }

    /**
     * setGameNr setzt die Nr des Spiels an dem Teilgenommen wird
     * -1 hei�t kein Spiel
     */
    public void setGameNr(int gNr) {
        participatesInGame = gNr;
        setButtonActiveDeActive();
    }

    /**
     * setButtonActiveDeactive pr�ft die Zustandsvariablen und schaltet entsprechend
     * die Buttons aktiv oder deaktiviert sie
     */
    public void setButtonActiveDeActive() {
        //Button 4 - Create nur Active wenn isConnected und !participatesInGame
        jButton4.setEnabled(isConnected && participatesInGame==-1);

        //Button 5 - Leave nur Active wenn isConnected
        jButton5.setEnabled(isConnected);

        //jButton 6 - Join nur Active wenn isConnected und !participatesInGame
        jButton6.setEnabled(isConnected && participatesInGame==-1);

        //Button 1 - Start nur Active wenn isMaster und participatesInGame und nicht spielt
        jButton1.setEnabled(isMaster && participatesInGame>=0 && !isPlaying);

        //Button 2 - OpenGames aktualisieren nur wenn nicht an Spiel teilgenommen wird und Connected
        jButton2.setEnabled(isConnected && participatesInGame==-1);

    }

    /**
     * setPlaying setzt den Zustand isPlaying auf den �bergebenen Wert
     */
    public void setPlaying(boolean pIsPlaying) {
        isPlaying=pIsPlaying;
        setButtonActiveDeActive();
    }

    /**
     * setIsMaster setzt den Zustand isMaster auf den �bergebenen Wert
     * also true, wenn dieser Spieler der Leiter des Spiels ist
     */
    public void setIsMaster(boolean pIsMaster) {
        isMaster=pIsMaster;
        setButtonActiveDeActive();
    }    

    /**
     * resetGameList setzt die Liste der offenen Spiele zur�ck
     */
    public void resetGameList() {
        spielAuswahl.removeAllItems();
    }

    /**
     * addGameListEntry setzt die Liste der offenen Spiele zur�ck
     */
    public void addGameListEntry(String pGameString) {
        spielAuswahl.addItem(pGameString);
    }

    /**
     * myTest() Methode um eigenschaften auszugeben zu Testzwecken
     */
    public void myTest() {
        System.out.println("Scrollpane Maximum: "+jList1ScrollPane.getVerticalScrollBar().getMaximum());
        jList1ScrollPane.getVerticalScrollBar().setValue(jList1ScrollPane.getVerticalScrollBar().getMaximum());
    }

} // end of class ClientGUI
