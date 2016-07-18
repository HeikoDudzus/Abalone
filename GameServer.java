
/**
 * Klasse GameServer.
 * 
 * @author Heiko Dudzus, Gerrit (Die Hinterbaenkler)
 * @version 2016-07-12
 */

public class GameServer extends Server implements Zustand
{
    //private DBVierGewinnt db;
    private List<Spieler> spielerListe;
    private Queue<Spieler> warteschlange;
    private List<Spiel> spiele;

    /**
     * Konstruktor fuer Objekte der Klasse GameServer
     * @param port Portnummer, auf der der Server lauschen soll
     */
    public GameServer(int port)
    {
        super(port);
        spielerListe = new List<Spieler>();
        warteschlange = new Queue<Spieler>();
        spiele = new List<Spiel>();
        System.out.println("Der Server lauscht an Port "+port+"!");
    }

    /**
     * Eine neue Verbindung wird aufgenommen.
     * @param pClientIP IP-Nummer des verbindenden Clients
     * @param pClientPort Portnummer des Clients
     */
    public void processNewConnection(String pClientIP, int pClientPort) {
        send(pClientIP, pClientPort, "Herzlich Willkommen auf dem Abalone-Gameserver!");
        send(pClientIP, pClientPort, "\"QUIT\" beendet die Verbindung!");
        System.out.println(pClientIP + " : " + pClientPort + " hat sich eingew�hlt.");
        Spieler spieler = new Spieler(pClientIP, pClientPort);
        spielerListe.append(spieler);
        send(pClientIP, pClientPort, "Waehlen Sie einen Nickname mit NICK <name>");
    }

    /**
     * Eine neue Nachricht eines Clients wird verarbeitet.
     * @param pClientIP IP-Nummer des anfragenden Clients
     * @param pClientPort Portnummer des Client-Sockets
     * @param pMessage Nachricht des Clients
     */
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        Spieler spieler = sucheClientNachIPUndPort(pClientIP, pClientPort);
        //System.out.println(spieler.gibName());
        if (spieler != null){
            int zustand = spieler.gibZustand();
            switch (zustand) {
                case NICKNAME : processNickname(spieler, pMessage); break;
                case WAITPW : processWaitPW(spieler, pMessage); break;
                case AUTHORIZED : processAuthorized(spieler, pMessage); break;
                default: System.out.println("Fehler, Client-Zustand "+ zustand+" nicht definiert.");
                System.out.println(spieler);
            }
        } else {
            System.out.println("PANIC - kein Client mit diesen Daten.");
        }
    }

    /**
     * Nachricht eines noch nicht benannten Spielers wird verarbeitet.
     * @param pClient anfragender Spieler
     * @param pMessage Nachricht des Spielers
     */
    private void processNickname(Spieler pClient, String pMessage){
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        String[] stuecke = pMessage.split(" ");
        if (stuecke.length == 2) {
            // NICK angefordert?
            if (stuecke[0].equals("NICK")){
                // Ist ein Client mit dem gewuenschten Namen noch nicht vorhanden?
                if (nameVorhanden(stuecke[1]) && gibSpielerNachName(stuecke[1]).gibZustand()==DISCONNECTED) {
                    Spieler temp = gibSpielerNachName(stuecke[1]);
                    temp.setzeZustand(WAITPW);
                    temp.setzeIP(clientIP);
                    temp.setzePort(clientPort);
                    send(clientIP, clientPort, "+NICKIS "+stuecke[1]);
                } else if (!nameVorhanden(stuecke[1]) && nameErlaubt(stuecke[1])){
                    pClient.setzeName(stuecke[1]);
                    pClient.setzeZustand(WAITPW);
                    send(clientIP, clientPort, "+GUESTNICK " + stuecke[1]);
                    //warteschlange.enqueue(pClient);
                    // Wenn schon zwei Spieler angemeldet sind, soll das Spiel gestartet werden
                    //starteSpielWennMoeglich();
                } else {
                    send(clientIP, clientPort, "Name invalid");
                }
            }
        } else if (pMessage.equals("QUIT")){
            send(clientIP, clientPort, "+OK see you soon");
            loescheSpieler(pClient);
            closeConnection(clientIP, clientPort);
        } else {
            send(clientIP, clientPort, "ERR unknown command");
        } 
    }

    /**
     * Passworteingabe wird verarbeitet.
     * @param pClient anfragender Spieler
     * @param pMessage Nachricht des Spielers
     */
    private void processWaitPW(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        if (pMessage.equals("QUIT")) {
            send(clientIP, clientPort, "+OK see you soon");
            pClient.setzeZustand(DISCONNECTED);
            closeConnection(clientIP, clientPort);
        } else if (pMessage.startsWith("PASS")) {
            String[] stuecke = pMessage.split(" ");
            if (pClient.isGuest()) {
                send(clientIP, clientPort, "+LOGGEDIN as guest");
                pClient.setzeZustand(AUTHORIZED);
            } else if (stuecke.length > 0 && pClient.checkPW(stuecke[1])) {
                send(clientIP, clientPort, "+LOGGEDIN");
                pClient.setzeZustand(AUTHORIZED);
            } else {
                send(clientIP, clientPort, "+INVPASS");
                pClient.setzeZustand(NICKNAME);
            }
        } else {
            send(clientIP, clientPort, "ERR unknown command - PASS <password> expected");
        }
    }

    /**
     * Nachrichten eines authorisierten Spielers werden verarbeitet.
     * @param pClient anfragender Spieler
     * @param pMessage Nachricht des Spielers
     */
    private void processAuthorized(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        if (pMessage.equals("QUIT")) {
            send(clientIP, clientPort, "+OK see you soon");
            try {
                //TODO Spiele beenden bzw. adjournen
                pClient.setzeZustand(DISCONNECTED);
            } catch (Exception e) {
                System.out.println("Gegenspieler nicht mehr vorhanden!");
            }
            if (pClient.isGuest()) loescheSpieler(pClient);
            closeConnection(clientIP, clientPort);
        } else if (pMessage.startsWith("PASSWD ")) {
            String[] stuecke = pMessage.split(" ");
            if (stuecke.length > 0) {
                pClient.setPasswd(stuecke[1]);
                pClient.setGuest(false);
                send(clientIP, clientPort, "+PASSWD changed");
            } else {
                send(clientIP, clientPort, "ERR passwd missing ? ");
            }
        } else {
            send(clientIP, clientPort, "ERR invalid command");
        }
    }

    /**
     * Ein Spiel wird aus der Liste aller aktuelle Spiele entfernt.
     * @param pSpiel das zu entfernende Spiel
     */
    private void loescheSpielAusListe(Spiel pSpiel) {
        spiele.toFirst();
        while (spiele.hasAccess()) {
            if (pSpiel == spiele.getContent()) spiele.remove();
            spiele.next();
        }
    }

    /**
     * Ein Spieler wird aus der Spielerliste entfernt.
     * @param der zu entfernende Spieler
     */
    private void loescheSpieler(Spieler pSpieler) {
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (spieler == pSpieler) {
                spielerListe.remove();
            }
            spielerListe.next();
        }
        if (warteschlange.front() == pSpieler) warteschlange.dequeue();
    }

    /**
     * Der Spieler zu einer IP-Nummer und Portnummer wird in der Spielerliste gesucht.
     * @param pClientIP IP-Nummer eines Spielers
     * @param Portnummer
     * @return Spieler
     */
    private Spieler sucheClientNachIPUndPort(String pClientIP, int pClientPort){
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (spieler.vergleicheIPUndPort(pClientIP, pClientPort)) {
                return spieler;
            }
            spielerListe.next();
        }
        return null;
    }

    /**
     * liefert den Spieler zu einem Namen
     * @param pName angefragter Name
     * @return Spieler sonst null
     */
    private Spieler gibSpielerNachName(String pName) {
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (pName.equals(spieler.gibName())) {
                return spieler;
            }
            spielerListe.next();
        }
        return null;
    }

    /**
     * Es wird geprueft, ob der Name schon in der Spielerliste des Servers vorhanden ist.
     * @param pName angefragter Name
     * @return Wahrheitswert: Name schon vergeben?
     */
    private boolean nameVorhanden(String pName) {
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (pName.equals(spieler.gibName())) {
                return true;
            }
            spielerListe.next();
        }
        return false;
    }

    public void processClosedConnection(String pClientIP, int pClientPort) {
        System.out.println(pClientIP + " : " + pClientPort + " hat sich abgemeldet.");
        /*Spieler spieler = sucheClientNachIPUndPort(pClientIP, pClientPort);
        Spiel s = gibSpielNachSpieler(spieler);
        Spieler gegenspieler = s.gibGegenspieler(spieler);
        send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+WON");
        registriereGewinnerInDB(gegenspieler);
        s.loescheSpieler(spieler);
        if (s.beideSpielerWeg()) loescheSpielAusListe(s);
        loescheSpieler(spieler);*/
    }

    /**
     * Zu einem gegebenen Spieler wird das derzeit gespielte Spiel gesucht.
     * @param pSpieler Der gegebene Spieler
     * @return Gespieltes Spiel
     */
    private Spiel gibSpielNachSpieler(Spieler pSpieler) {
        spiele.toFirst();
        while (spiele.hasAccess()) {
            Spiel spiel = spiele.getContent();
            if (spiel.pruefeSpieler(pSpieler)) return spiel;
            spiele.next();
        }
        return null;
    }

    /**
     * Prueft auf erlaubte alphanumerische Zeichen im angeforderten Benutzernamen,
     * um die Datenbank vor Injektionen zu schuetzen.
     * @param pName angeforderter Benutzername
     * @return Wahrheitswert, ob der Name erlaubt ist
     */
    private boolean nameErlaubt(String pName) {
        boolean out = true;
        int i = 0;
        boolean kleinbuchstabe, grossbuchstabe, ziffer;
        while (out == true && i < pName.length()) {
            char[] c = pName.toCharArray();
            kleinbuchstabe = false;
            grossbuchstabe = false;
            ziffer = false;
            if (c[i] >=48 && c[i] <= 57) ziffer = true;
            if (c[i] >=65 && c[i] <= 90) grossbuchstabe = true;
            if (c[i] >=97 && c[i] <= 122) kleinbuchstabe = true;
            if (!ziffer && !grossbuchstabe && !kleinbuchstabe) out = false;
            i++;
        }
        return out;
    }

    /**
     * Der Gewinn eines Spielers wird in der Datenbank registriert.
     * @param pSpieler Sieger des Spiels
     */
    private void registriereGewinnerInDB(Spieler pSpieler) {
    }

    /**
     * Ein begonnenes Spiel wird in der Datenbank registriert.
     * @param pSpieler1 erster Spieler
     * @param pSpieler2 zweiter Spieler
     */
    private void registriereSpielInDB(Spieler pSpieler1, Spieler pSpieler2) {
    }

    //Fuer die Verwendung auf Linux-Servern und Windows-Clients
    /*
    public void send(String pClientIP, int pClientPort, String pMessage){
    super.send(pClientIP,pClientPort,pMessage+"\r");
    } */
}