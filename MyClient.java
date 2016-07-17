import java.net.*;
import java.io.*;
/**
 * Die Klasse MyClient baut die Verbindung zum Server auf, interpretiert die
 * gesendeten Daten und startet wenn noetig das SpielGUI
 * 
 * Entstammt aus der MineServer implementation fuer MineSweeper
 * 
 * @author Peter Scholl scholl@unterrichtsportal.org
 * @version 09.02.2016
 */

public class MyClient implements Runnable
{
    // instance variables - replace the example below with your own
    Socket clientSock=null;
    BufferedReader in;
    PrintWriter out;
    String line;
    ClientGUI clientGUI;
    AbaloneGUI spielGUI;
    long timeIGotLastPong;
    boolean closeSock = false;

    /**
     * Constructor for objects of class MyClient
     */
    public MyClient(String pServerIP, int pServerPort, ClientGUI pClientGUI) throws RuntimeException
    {
        clientGUI = pClientGUI;
        timeIGotLastPong = System.currentTimeMillis();  
        try {
            clientSock = new Socket(pServerIP, pServerPort);
            System.out.println("Verbindung zu "+clientSock.getRemoteSocketAddress()+"wurde aufgebaut");
            in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            out = new PrintWriter(clientSock.getOutputStream());

        }
        catch (Exception e) {
            System.out.println("Fehler: "+e.getMessage());
            //z.B. Connection refused - dann wird kein Objekt erstellt - sehr sch�n ;-)
            throw new RuntimeException("Connection refused!");
        }
    }
    /* --------- Anfang der privaten inneren Klasse -------------- */
    private class sendPingEveryXs implements Runnable {

        private Socket sock;
        private int secs;

        public sendPingEveryXs(Socket pSock, int pSecs) {
            sock = pSock;
            secs = pSecs;
        }

        public void run() {
            try {
                PrintWriter out = new PrintWriter(sock.getOutputStream());
                while(!sock.isClosed() && System.currentTimeMillis()-timeIGotLastPong<20000) {
                    out.println(">ping");
                    out.flush();
                    //System.out.println(">ping gesendet");
                    Thread.sleep(secs*1000);
                }
                //Wenn kein ping mehr kommt - Socket schliessen
                sock.close();
                clientGUI.setConnected(false);
            } catch (Exception ex){
                System.out.println("Fehler in ping: "+ ex.getMessage());
            }
        }
    }

    /* ----------- Ende der privaten inneren Klasse -------------- */
    public void run() {
        // Dies ist die einzige Methode, in der die Informationen vom Server
        // eingelesen werden (bzw. in verarbeite)
        String line, res;
        //alle 5s ein Pong an den Server schicken
        Thread t = new Thread(new sendPingEveryXs(clientSock,5) );
        t.start();

        try {
            out = new PrintWriter(clientSock.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            clientSock.setSoTimeout(10000);
            while (!closeSock && !clientSock.isClosed()) {
                //System.out.println("Vor readline()");
                line = in.readLine();
                res = null;
                if (line != null)
                //System.out.println("Test: "+line);
                    if(line != null && line.length()>0 && line.charAt(0) =='>') {
                        //System.out.println("Starte verarbeite("+line+")");
                        verarbeite(line);
                    } else if (line.length()==0) {
                        System.out.println("Leerzeile empfangen - warum?");
                    } else if (line != null && line.charAt(0) =='0') {
                        System.out.println("Befehl erfolgreich: "+line);
                    } else {
                        clientGUI.textAusgeben(line);
                    }
            }
            System.out.println("Verbindung von "+clientSock.getInetAddress()+" beenden.");
            clientSock.close();
        }
        catch (Exception e) {
            System.out.println("Verbindungsfehler(in MyClient-run): "+e.getMessage());
            //z.B. Connection reset - weil Server abgesemmelt
            closeSock=true;
        }
        // Thread für Ping stoeren - wozu ? - kann mich nicht erinnern
        t.interrupt();
        try {
            clientSock.close();
        } catch (Exception ex) {
            System.out.println("Fehler beim schlie�en des Sockets");
        }
    }

    public boolean isConnected() {
        if (clientSock==null) return false;
        return clientSock.isConnected() && !clientSock.isClosed();
    }

    public void execServerCmd(String cmd) {
        //Nachricht an Server
        System.out.println("Sende "+cmd+" an Server");
        String msg = cmd;
        out.println(msg);
        out.flush();
    }

    public void verarbeite(String input) {
        clientGUI.textAusgeben(line);

        /* //alte Verarbeitung aus MineSweeper
        if (input.startsWith(">startespiel")) {
        //Spiel starten

        String[] args=input.split(" ");
        int groesse = Integer.parseInt(args[1]);
        int bomben = Integer.parseInt(args[2]);
        clientGUI.setPlaying(true);
        spielGUI = new SpielGUI(groesse,bomben,10,this);
        execServerCmd("z");
        } else if (input.startsWith(">spielfeld")) {
        if (spielGUI == null) {
        clientGUI.textAusgeben(input);
        } else {
        //weitere Zeilen lesen und Spielfeld spielGUI informieren
        try {
        System.out.println("Spielfeld lesen");
        String line = in.readLine();
        while (line == null || line.length()<2) { line = in.readLine(); }
        spielGUI.setzeSpielstatus(Integer.parseInt(line.split(" ")[0]));
        System.out.println("Spielstatus: "+line);
        System.out.println("Setzte Spielstatus: "+Integer.parseInt(line.split(" ")[0]));
        line = in.readLine();
        while (line == null|| line.length()<2) { line = in.readLine(); }
        int groesse = Integer.parseInt(line.split(" ")[0]);
        // TODO: Gr��e koennte �berpr�ft werden
        System.out.println("Groesse "+groesse);
        line = in.readLine();
        while (line == null|| line.length()<2) { line = in.readLine(); }
        // TODO: Bomben koennte �berpr�ft werden
        for (int i=0; i<groesse; i++) {
        line = in.readLine();
        while (line == null|| line.length()<2) { line = in.readLine(); }

        System.out.println(line);
        char[] lineInChars = line.toCharArray();
        for (int j=0; j< groesse; j++) {
        //System.out.println("x: "+i+" y: "+j+" : "+lineInChars[j]);
        spielGUI.setzeSpielfeld(j,i,lineInChars[j]);
        }
        }
        while (!line.startsWith("--")) { line = in.readLine(); }
        } catch (Exception ex) {
        System.out.println("Fehler: "+ex.getMessage());
        }
        spielGUI.zeige();
        spielGUI.repaint();
        System.out.println("Spielfeldlesen beendet");
        }
        } else if (input.startsWith(">spielbeendet")) {
        clientGUI.setPlaying(false);
        clientGUI.setGameNr(-1);
        clientGUI.setIsMaster(false);
        spielGUI.schliesseFenster();
        } else if (input.startsWith(">pong")) {
        timeIGotLastPong = System.currentTimeMillis();
        } else if (input.startsWith(">spieleranzahl")) {
        String[] parameter = input.split(" ");
        if (parameter.length>0) clientGUI.setzeAnzSpieler(Integer.parseInt(parameter[1]));
        } else if (input.startsWith(">groesse")) {
        String[] parameter = input.split(" ");
        if (parameter.length>0) clientGUI.setzeGroesse(Integer.parseInt(parameter[1]));
        } else if (input.startsWith(">bomben")) {
        String[] parameter = input.split(" ");
        if (parameter.length>0) clientGUI.setzeAnzBomben(Integer.parseInt(parameter[1]));
        } else if (input.startsWith(">anzspielfelder")) {
        String[] parameter = input.split(" ");
        if (parameter.length>0) clientGUI.setzeAnzSpielfelder(Integer.parseInt(parameter[1]));
        } else if (input.startsWith(">spieltyp")) {
        String[] parameter = input.split(" ");
        if (parameter.length>0) clientGUI.setzeSpieltyp(Integer.parseInt(parameter[1]));
        } else if (input.startsWith(">gamekilled")) {
        clientGUI.setPlaying(false);
        clientGUI.setGameNr(-1);
        clientGUI.setIsMaster(false);
        if (spielGUI!=null) {
        spielGUI.schliesseFenster();
        }
        } else if (input.startsWith(">spielerstellt")) {
        String[] parameter = input.split(" ");
        if (parameter.length>0) {
        clientGUI.setGameNr(Integer.parseInt(parameter[1]));
        clientGUI.setIsMaster(true);
        } else {
        System.out.println("Fehler - spielerstellt ohne Nummer!!!");
        }
        } else if (input.startsWith(">joined")) {
        String[] parameter = input.split(" ");
        if (parameter.length>0) {
        clientGUI.setGameNr(Integer.parseInt(parameter[1]));
        clientGUI.setIsMaster(false);
        } else {
        System.out.println("Fehler - spielerstellt ohne Nummer!!!");
        }
        } else if (input.startsWith(">newopengamelist")) {
        clientGUI.resetGameList();
        } else if (input.startsWith(">opengame")) {
        String[] parameter = input.split(" ",2);
        if (parameter.length>0) {
        clientGUI.addGameListEntry(parameter[1]);
        } else {
        System.out.println("Fehler - opengame mit falschen Infos");
        }
        } else if (input.startsWith(">closeConnection")) {
        //Socket schlie�en
        beenden();
        try {
        clientSock.close();
        } catch (Exception ex) {
        System.out.println("Fehler beim schlie�en der Verbindung: "+ex.getMessage());
        }
        clientGUI.setConnected(false);
        } else {
        // Commando l�sst sich nicht verarbeiten also ausgeben
        clientGUI.textAusgeben(input);
        }
         */
    }

    /**
     * beenden - Trennt die Verbindung und beendet (hoffentlich) alle Threads (z.B. ping)
     */
    public void beenden() {
        closeSock=true;
    }
}
