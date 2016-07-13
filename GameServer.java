 

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
        System.out.println(pClientIP + " : " + pClientPort + " hat sich eingewählt.");
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
                case WAIT : processWait(spieler, pMessage); break;
                case PASSIVE : processPassive(spieler, pMessage); break;
                case ACTIVE : processActive(spieler, pMessage); break;
                case OVER : processOver(spieler, pMessage); break;
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
                if (!nameVorhanden(stuecke[1]) && nameErlaubt(stuecke[1])){
                    pClient.setzeName(stuecke[1]);
                    pClient.setzeZustand(WAIT);
                    send(clientIP, clientPort, "+NICKIS " + stuecke[1]);
                    warteschlange.enqueue(pClient);
                    // Wenn schon zwei Spieler angemeldet sind, soll das Spiel gestartet werden
                    starteSpielWennMoeglich();
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
     * Nachricht eines wartenden Spielers wird verarbeitet.
     * @param pClient anfragender Spieler
     * @param pMessage Nachricht des Spielers
     */
    private void processWait(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        if (pMessage.equals("QUIT")) {
            send(clientIP, clientPort, "+OK see you soon");
            loescheSpieler(pClient);
            closeConnection(clientIP, clientPort);
        } else {
            send(clientIP, clientPort, "ERR unknown command");
        }
    }
    
    /**
     * Nachricht eines passiven Spielers wird verarbeitet.
     * @param pClient anfragender Spieler
     * @param pMessage Nachricht des Spielers
     */
    private void processPassive(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        Spiel s = gibSpielNachSpieler(pClient);
        Spieler gegenspieler = s.gibGegenspieler(pClient);
        if (pMessage.equals("QUIT")) {
            send(clientIP, clientPort, "+OK see you soon");
            try {
                send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+WON");
                gegenspieler.setzeZustand(OVER);
                registriereGewinnerInDB(gegenspieler);
            } catch (Exception e) {
                System.out.println("Gegenspieler nicht mehr vorhanden!");
            }
            s.loescheSpieler(pClient);
            if (s.beideSpielerWeg()) loescheSpielAusListe(s);
            loescheSpieler(pClient);
            closeConnection(clientIP, clientPort);
        } else {
            send(clientIP, clientPort, "ERR it is not your turn");
        }
    }
    
    /**
     * Nachricht des Spielers, der am Zug ist, wird verarbeitet.
     * @param pClient anfragender Spieler
     * @param pMessage Nachricht des Spielers
     */
    private void processActive(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        String[] stuecke = pMessage.split(" ");
        char symbol = pClient.gibSymbol();
        Spiel s = gibSpielNachSpieler(pClient);
        Spieler gegenspieler = s.gibGegenspieler(pClient);
        boolean validInt = true;
        int vonZeile=0, vonSpalte=0;
        int bisZeile=0, bisSpalte=0;
        int nachZeile=0, nachSpalte=0;
        Position von = null, bis = null;
        Vektor richtung = null;
        if (stuecke.length == 7) {
            if (stuecke[0].equals("SHIFT")) {
                try {
                    vonZeile = Integer.parseInt(stuecke[1]);
                    vonSpalte = Integer.parseInt(stuecke[2]);
                    bisZeile = Integer.parseInt(stuecke[3]);
                    bisSpalte = Integer.parseInt(stuecke[4]);
                    nachZeile = Integer.parseInt(stuecke[5]);
                    nachSpalte = Integer.parseInt(stuecke[5]);
                    von = new Position(vonSpalte, vonZeile);
                    bis = new Position(bisSpalte, bisZeile);
                    richtung = new Vektor(bisSpalte, bisZeile, nachSpalte, nachZeile);
                } catch (Exception e) {
                    //System.out.println("Konnte keine Integer parsen!");
                    send(clientIP, clientPort, "Die Eingaben waren ungültig");
                    validInt = false;
                }
                if (s != null && validInt) {
                    boolean status = s.schiebe(von, bis, richtung,pClient);
                    if (status) {
                        //TODO
                        //send(clientIP, clientPort, "+SET "+symbol+" "+i+" "+j);
                        send(clientIP, clientPort, "+PASSIVE");
                        send(clientIP, clientPort, s.toString());
                        pClient.setzeZustand(PASSIVE);
                        // TODO
                        //send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+SET "+symbol+" "+i+" "+j);
                        send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+ACTIVE");
                        send(gegenspieler.gibIP(), gegenspieler.gibPort(), s.toString());
                        gegenspieler.setzeZustand(ACTIVE);
                        
                        
                        
                        
                        if (gegenspieler.hatVerloren()) {
                            send(clientIP, clientPort, "+WON");
                            registriereGewinnerInDB(pClient);
                            pClient.setzeZustand(OVER);
                            try {  // braucht man diesen Try-Catch-Block wirklich?
                                send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+LOST");
                                gegenspieler.setzeZustand(OVER);
                            } catch (Exception e) {
                                System.out.println("Gegenspieler nicht mehr vorhanden!");
                            }
                        }
                        
                        
                        
                        
                        
                    } else {
                        send(clientIP, clientPort, "move not possible");
                    }
                }
            }
        } else if (stuecke.length == 1) {
            if (pMessage.equals("QUIT")) {
                send(clientIP, clientPort, "+OK see you soon");
                try {
                    send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+WON");
                    gegenspieler.setzeZustand(OVER);
                    registriereGewinnerInDB(gegenspieler);
                } catch (Exception e) {
                    System.out.println("Gegenspieler nicht mehr vorhanden!");
                }
                s.loescheSpieler(pClient);
                if (s.beideSpielerWeg()) loescheSpielAusListe(s);
                loescheSpieler(pClient);
                closeConnection(clientIP, clientPort);
            } else {
                send(clientIP, clientPort, "ERR unknown command");
            }
        } else {
            send(clientIP, clientPort, "ERR unknown command");
        }
    }

    /**
     * Nachricht eines Spielers, der ein Spiel beendet hat, wird verarbeitet.
     * @param pClient anfragender Spieler
     * @param pMessage Nachricht des Spielers
     */
    private void processOver(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        Spiel s = gibSpielNachSpieler(pClient);
        Spieler gegenspieler = s.gibGegenspieler(pClient);
        if (pMessage.equals("NEW")) {
            // Spieler in Wartezustand
            send(clientIP, clientPort, "Waiting for a new game");
            send(clientIP, clientPort, "+WAIT");
            pClient.setzeZustand(WAIT);
            warteschlange.enqueue(pClient);
            // Spieler aus seinem Spiel nehmen
            s.loescheSpieler(pClient);
            // Liste der Spiele aktualisieren
            if (s.beideSpielerWeg()) loescheSpielAusListe(s);
            starteSpielWennMoeglich();
        } else if(pMessage.equals("QUIT")) {
            send(clientIP, clientPort, "+OK see you soon");
            s.loescheSpieler(pClient);
            if (s.beideSpielerWeg()) loescheSpielAusListe(s);
            loescheSpieler(pClient);
            closeConnection(clientIP, clientPort);
        } else {
            send(pClient.gibIP(), pClient.gibPort(), "ERR unknown command");
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

    private void starteSpielWennMoeglich(){
        Spieler spieler1 = warteschlange.front();
        warteschlange.dequeue();
        if (warteschlange.isEmpty()) {
            warteschlange.enqueue(spieler1);
        } else {
            Spieler spieler2 = warteschlange.front();
            warteschlange.dequeue();
            spieler1.setzeSymbol('X');
            spieler2.setzeSymbol('O');
            Spiel s = new Spiel(spieler1, spieler2);
            spiele.append(s);
            spieler1.setzeZustand(ACTIVE);
            spieler2.setzeZustand(PASSIVE);
            send(spieler1.gibIP(), spieler1.gibPort(), "+GAMEWITH "+spieler2.gibName());
            send(spieler1.gibIP(), spieler1.gibPort(), "+SYMBOL "+spieler1.gibSymbol());
            send(spieler2.gibIP(), spieler2.gibPort(), "+GAMEWITH "+spieler1.gibName());
            send(spieler2.gibIP(), spieler2.gibPort(), "+SYMBOL "+spieler2.gibSymbol());
            send(spieler1.gibIP(), spieler1.gibPort(), "+ACTIVE");
            send(spieler2.gibIP(), spieler2.gibPort(), "+PASSIVE");
            send(spieler1.gibIP(), spieler1.gibPort(), "-Spielzuege werden mit SHIFT <x> <y> <i> <j> gemacht.");
            send(spieler2.gibIP(), spieler2.gibPort(), "-Spielzuege werden mit MOVE <x> <y> <i> <j> gemacht.");
            System.out.println("Spiel gestartet mit "+spieler1.gibName()+" und "+spieler2.gibName());
            registriereSpielInDB(spieler1, spieler2);
        }
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