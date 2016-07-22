
/**
 * Spiel-Logik fuer das Brettspiel Abalone.
 * 
 * Das Abalone-Spielbrett, dessen Verbindungslinien zwischen den Nachbarfeldern im Winkel 
 * von 60 Grad zueinander stehen, kann intern durch ein orthogonales Koordinatensystem 
 * repraesentiert werden. Ausser den horizontalen und vertikalen Nachbarfeldern werden
 * die Felder auf EINER der beiden Diagonalen als Nachbarn betrachtet. (Siehe Klasse Vektor)
 * So erhaelt man zu jedem inneren Spielfeld die sechs Nachbarn, die es auch auf dem 
 * echten Spielfeld gibt.
 * 
 * Das Array, das das Spielfeld repraesentiert hat bei den Indizes 0 und 10 Rand-Felder, die 
 * nicht mehr zum regulaeren Spielfeld gehoeren. Damit werden im Prinzip die Konventionen 
 * eingehalten, die in https://de.wikipedia.org/wiki/Abalone_(Spiel)#Notation besprochen
 * werden, wenn man die Zeilen A bis I mit den Zeilennummer 1 bis 9 identifiziert.
 * 
 * @author Heiko Dudzus
 * @version 2016-07-11
 */
public class Spiel implements Zustand
{
    private Feld[][] spielfeld;
    private Spieler[] spielerListe = new Spieler[2];
    private Spieler gewinner;
    private Spieler verlierer;
    private int aktiv = 0; //Spieler 1 (Feldid 0) ist zunächst aktiv
    private boolean spielLaeuft;
    private int spielNr;

    // fuer Testzwecke: erlaubte Verschiebungen
    public Vektor rechtsOben = new Vektor(1,1);
    public Vektor linksUnten = new Vektor(-1,-1);
    public Vektor hoch = new Vektor (0,1);
    public Vektor runter = new Vektor(0,-1);
    public Vektor rechts = new Vektor(1,0);
    public Vektor links = new Vektor(-1,0);

    /**
     * Constructor for objects of class Spielfeld
     */
    public Spiel(Spieler pSpieler1, int pSpielNr)
    {
        spielNr = pSpielNr;
        spielfeld = new Feld[11][11];
        spielerListe[0]=pSpieler1;

        spielLaeuft = false;

        // Felder erzeugen und in Array verwalten
        for (int zeile = 0; zeile < 11; zeile++) {
            for (int spalte = 0; spalte < 11; spalte++) {
                spielfeld[zeile][spalte] = new Feld();
            }
        }

    }

    /**
     * schiebe schiebt die durch die beiden Positionen beschriebenen Steine des pSpielers in 
     * die durch den Vektor pRichtung angegebene Richtung
     */
    public boolean schiebe(Position pGrundseite1, Position pGrundseite2, Vektor pRichtung, Spieler pSpieler) {
        if (schiebe2(pGrundseite1, pGrundseite2, pRichtung, pSpieler)) {
            aktiv=1-aktiv; //aktiver Spieler wechselt
            return true;
        } else {
            return false;
        }
    }

    private boolean schiebe2(Position pGrundseite1, Position pGrundseite2, Vektor pRichtung,
    Spieler pSpieler) {
        if (! (spielerListe[aktiv]==pSpieler)) return false;
        if (!pRichtung.richtungErlaubt()) return false;
        Vektor grundseitenVektor = new Vektor(pGrundseite1, pGrundseite2);
        if (grundseitenVektor.laenge() > 2) return false; // zu viele Steine ausgewaehlt!

        // Laengszug oder Querzug?
        if (grundseitenVektor.linearAbhaengig(pRichtung)) {
            // Laengszug
            if (grundseitenVektor.skalarprodukt(pRichtung) < 0) {
                //Fuer die Verschiebung muss am Stein an Position pGrundseite2
                // angesetzt werden.
                return laengszug(pGrundseite2, pRichtung, pSpieler);
            } else {
                //Fuer die Verschiebung muss am Stein an Position pGrundseite1
                // angesetzt werden.
                return laengszug(pGrundseite1, pRichtung, pSpieler);
            }
        } else {
            // Querzug
            // Wieviele Steine sollen verschoben werden?
            int anzahl = grundseitenVektor.laenge() + 1;
            if (anzahl == 1) {
                return laengszug(pGrundseite1,pRichtung,pSpieler);
            } else if (anzahl == 2) {
                boolean status1 = laengszug(pGrundseite1,pRichtung,pSpieler);
                boolean status2 = laengszug(pGrundseite2,pRichtung,pSpieler);
                return status1 | status2;
            } else if (anzahl == 3){
                int x1 = pGrundseite1.gibX();
                int y1 = pGrundseite1.gibY();
                int x2 = pGrundseite2.gibX();
                int y2 = pGrundseite2.gibY();
                Position mitte = new Position((x1+x2)/2, (y1+y2)/2);
                boolean status1 = laengszug(pGrundseite1,pRichtung, pSpieler);
                boolean status2 = laengszug(pGrundseite2,pRichtung, pSpieler);
                boolean status3 = laengszug(mitte,pRichtung, pSpieler);
                return status1 | status2 | status3;
            } else {
                return false;
            }
        }
    }

    /**
     * Wrapper fuer die Methode schiebeRekursiv(). 
     * Es wird eine Verschiebung an gegebener 
     * Position in eine gegebene Richtung angefragt.
     * @param pZeile Zeilennummer des Spielfeldes
     * @param pSpalte Spaltennummer des Spielfeldes
     * @param pRichtung Richtungsvektor der Verschiebung
     * @param pSpieler Spieler, der den Spielzug ausfuehren moechte
     * @return Wahrheitswert, ob die geplante Verschiebung durchgefuehrt werden kann
     */
    public boolean laengszug(Position pPos, Vektor pRichtung, Spieler pSpieler) {
        int zeile = pPos.gibY();
        int spalte = pPos.gibX();
        if (!pRichtung.richtungErlaubt()) return false;
        Stein stein = spielfeld[zeile][spalte].gibStein();
        if (stein == null) return false;
        if (stein.gibBesitzer() != pSpieler) return false;
        if (schiebeRekursiv(zeile, spalte, pRichtung, pSpieler, 0, 0, stein)) {
            // Der Stein konnte verschoben werden, entferne ihn hier
            spielfeld[zeile][spalte].setzeStein(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Es wird versucht, Steine auf der angegebenen Spielfeld-Position werden rekursiv in
     * die angegebene Richtung zu verschieben. Bedingungen:
     * Vgl.: https://de.wikipedia.org/wiki/Abalone_(Spiel)#Z.C3.BCge
     * Im Erfolgsfall wird nach dem Verschieben der uebergebene Stein auf dem Feld platziert.
     * @param pZeile Zeilennummer des Spielfeldes
     * @param pSpalte Spaltennummer des Spielfeldes
     * @param pRichtung Richtungsvektor der Verschiebung
     * @param pSpieler Spieler, der den Spielzug ausfuehren moechte
     * @param pEigene Anzahl der bisher von der geplanten Verschiebung betroffenen eigenen Steine
     * @param pAndere Anzahl der bisher von der geplanten Verschiebung betroffenen fremden Steine
     * @param pStein Stein, der im Erfolgsfall hier abgelegt wird
     * @return Wahrheitswert, ob die geplante Verschiebung durchgefuehrt werden kann
     */
    private boolean schiebeRekursiv (int pZeile, int pSpalte, Vektor pRichtung,
    Spieler pSpieler, int pEigene, int pAndere, Stein pStein) {
        if (!imFeld(pZeile,pSpalte)) {
            /* 
             * Der Stein pStein ist ueber den Rand gefallen, der Besitzer muss ihn abgeben
             * und hat moeglicherweise verloren.
             */
            Spieler spieler = pStein.gibBesitzer();
            if (spieler == pSpieler) return false;
            spieler.gibSteinAb();
            if (spieler.hatVerloren()) beendeSpiel(spieler);
            return true;
        }
        Stein stein = spielfeld[pZeile][pSpalte].gibStein();
        // vektorielle Verschiebung
        int zeile = pZeile + pRichtung.gibY();
        int spalte = pSpalte + pRichtung.gibX();
        if (stein != null) {
            if (stein.gibBesitzer() == pSpieler) {
                // eigener Stein
                if (pAndere > 0 ) return false; // schon fremde Steine angetroffen. Fehler!
                // Wir sind noch dabei, eigene Steine zu zaehlen, alles ok!
                pEigene++;
                if (pEigene > 3) return false; // mehr als 3 eigene Steine!
            } else {
                // fremder Stein
                pAndere++;
                if (pAndere >= pEigene || pAndere > 2) return false; // zu viele fremde Steine
            }
            /* 
             * Alle verbotenen Faelle ausgeschlossen. Versuche, den Stein weiter zu
             * schieben.
             */
            if (schiebeRekursiv(zeile, spalte, pRichtung, pSpieler, 
                pEigene, pAndere, stein)) {
                /* 
                 * Die voraus liegenden Steine konnten rekursiv verschoben werden. 
                 * Insbesondere der bisher hier liegende Stein konnte vorgeschoben 
                 * werden. Also kann auch der fuer hier uebergebene Stein gesetzt werden!
                 */ 
                spielfeld[pZeile][pSpalte].setzeStein(pStein);
                return true;
            } else {
                // Bei den voraus liegenden Steinen gab es ein Problem
                return false;
            }
        } else {
            // leeres Feld gefunden, verbotene Faelle von voriger Instanz ausgeschlossen
            spielfeld[pZeile][pSpalte].setzeStein(pStein);
            return true;
        }
    }

    /**
     * Beendet das Spiel
     * @param pSpieler Der Spieler, der als Verlierer das Spiel beendet
     */
    private void beendeSpiel(Spieler pSpieler) {
        System.out.println("" + pSpieler.gibName() + " hat verloren");
        // Im Serverbetrieb geschieht hier noch viel mehr... Spielerstatus etc
        spielLaeuft = false;
        verlierer = pSpieler;
        gewinner = gibGegenspieler(pSpieler);
    }

    /**
     * Prueft, ob eine gegebene Position innerhalb des gueltigen Spielfeldes ist.
     * @param y Zeilennummer des zu pruefenden Feldes
     * @param x Spaltennummer des zu pruefenden Feldes
     * @return Wahrheitswert, ob an der Position ein gueltiges Spielfeld ist
     */
    private boolean imFeld(int y, int x) {
        /*
        Es gibt 6 Spielfeldraender, also 6 Bedingungen
        Spielfeld:

        |      I  O O O O O
        |     H  O O O O O O
        |    G  . . O O O . . 
        |   F  . . . . . . . . 
        |  E  . . . . . . . . . 
        |   D  . . . . . . . .  9
        |    C  . . X X X . .  8
        |     B  X X X X X X  7
        |      A  X X X X X  6
        |          1 2 3 4  5

        orthogonalisiert:

        9  I         O O O O O
        8  H       O O O O O O
        3  G     . . O O O . .
        6  F   . . . . . . . .
        5  E . . . . . . . . .
        4  D . . . . . . . .
        3  C . . X X X . .
        2  B X X X X X X
        1  A X X X X X
        1 2 3 4 5 6 7 8 9 
         */
        boolean in = true;
        if (x < 1) in = false;
        if (x > 9) in = false;
        if (y < 1) in = false;
        if (y > 9) in = false;
        if (y <= x - 5) in = false;
        if (y > x + 5) in = false;
        return in;
    }

    /**
     * Erzeugt einen String, der das aktuelle Spielfeld darstellt.
     */
    public String toString() {
        String out = "";
        for (int zeile = 9; zeile > 0; zeile--) {
            out += "" + zeile + "  " + (char) ('A' - 1 +zeile) + "  ";
            for (int spalte = 1; spalte < 10; spalte++) {
                Stein stein = spielfeld[zeile][spalte].gibStein();
                if (stein == null) {
                    if (imFeld(zeile, spalte)) {
                        out += ". ";
                    } else {
                        out += "  ";
                    }
                } else {
                    out += "" + stein.gibBesitzer().gibSymbol() + " ";
                }
            }
            out += "\n";
        }
        out += "      1 2 3 4 5 6 7 8 9\n";
        return out;
    }

    /**
     * Erzeugt ein int-Array, das das aktuelle Spielfeld darstellt.
     */
    public int[][] toIntegerArray() {
        int[][] result = new int[11][11];
        for (int zeile = 9; zeile > 0; zeile--) {
            for (int spalte = 1; spalte < 10; spalte++) {
                Stein stein = spielfeld[zeile][spalte].gibStein();
                if (stein == null) {
                    result[10-zeile][spalte]=0;
                } else {
                    if (stein.gibBesitzer() == spielerListe[0]) {
                        result[10-zeile][spalte]=1;
                    } else {
                        result[10-zeile][spalte]=2;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Druckt das aktuelle Spielfeld auf der Konsole aus
     */
    public void toConsole() {
        System.out.print(toString());
    }

    /*
     * Netzwerk-Methoden fuer Netzwerkspiele zu zweit
     */
    public Spieler gibGegenspieler(Spieler pSpieler) {
        if (spielerListe[0]==pSpieler) {
            return spielerListe[1];
        } else {
            return spielerListe[0];
        }
    }

    public void loescheSpieler(Spieler pClient) {
        if (spielerListe[0]==pClient) {
            spielerListe[0]=null;
        } else {
            spielerListe[1]=null;
        }
    }

    public boolean pruefeSpieler(Spieler pSpieler) {
        for (int i=0; i<spielerListe.length; i++) {
            if (spielerListe[i]==pSpieler) return true;
        }
        return false;
    }

    public boolean beideSpielerWeg() {
        return (spielerListe[0]==null && spielerListe[1]==null);
    }

    /**
     * isJoinable prueft ob ein zweiter Spieler an dem spiel teilnehmen 
     * kann (wenn also bisher nur ein Spieler vorhanden ist)
     */
    public boolean isJoinable() { 
        if (beideSpielerWeg()) return false;
        if (spielerListe[1]==null) return true;
        return false;
    }

    public boolean join(Spieler pSpieler2) {
        if (!isJoinable()) return false;
        if (spielerListe[0]==pSpieler2) return false;
        Spieler pSpieler1 = spielerListe[0];
        spielerListe[1]=pSpieler2;

        // Startpositionen belegen
        for (int spalte = 1; spalte <=5; spalte++) {
            spielfeld[1][spalte].setzeStein(new Stein(pSpieler1));
            spielfeld[9][10-spalte].setzeStein(new Stein(pSpieler2));
            pSpieler1.erhalteStein();
            pSpieler2.erhalteStein();
        }
        for (int spalte = 1; spalte <=6; spalte++) {
            spielfeld[2][spalte].setzeStein(new Stein(pSpieler1));
            spielfeld[8][10-spalte].setzeStein(new Stein(pSpieler2));
            pSpieler1.erhalteStein();
            pSpieler2.erhalteStein();
        }
        for (int spalte = 3; spalte <=5; spalte++) {
            spielfeld[3][spalte].setzeStein(new Stein(pSpieler1));
            spielfeld[7][10-spalte].setzeStein(new Stein(pSpieler2));
            pSpieler1.erhalteStein();
            pSpieler2.erhalteStein();
        }

        aktiv = 0;
        spielLaeuft = true;

        //beiden Spielern das Spielfeld senden
        if (gibSpielerNr(1)!= null && gibSpielerNr(2) !=null) {
            this.sendeSpielfeld(gibSpielerNr(1));
            this.sendeSpielfeld(gibSpielerNr(2));
        }
        return true;
    }

    public Spieler gibSpielerNr(int pNr) {
        if (pNr>0 && pNr<=spielerListe.length) return spielerListe[pNr-1];
        return null;
    }

    public int gibSpielNr() {
        return spielNr;
    }

    /**
     * sendet dem uebergebenen Spieler das Spielfeld
     */
    public void sendeSpielfeld(Spieler pSpieler) {
        //evtl. diesen Pfeil fuer mehrzeilige rücksendungen
        //diese enden dann mit >end <typ>
        pSpieler.send(">GAME "+spielNr);
        Spieler sp1 = gibSpielerNr(1);
        Spieler sp2 = gibSpielerNr(2);
        pSpieler.send(">PLAYER1: "+sp1.gibName());
        pSpieler.send(">PLAYER2: "+sp2.gibName());
        pSpieler.send("> "+!sp1.hatVerloren()+" : "+!sp2.hatVerloren());
        pSpieler.send(">ACTIVE "+(aktiv+1));
        for (int zeile = 9; zeile > 0; zeile--) {
            String temp = ">"+zeile+" "+ (char)('A'+zeile-1)+" ";
            for (int spalte = 1; spalte < 10; spalte++) {
                Stein stein = spielfeld[zeile][spalte].gibStein();
                if (stein == null) {
                    temp +="0 ";
                } else {
                    if (stein.gibBesitzer() == spielerListe[0]) {
                        temp+="1 ";
                    } else {
                        temp+="2 ";
                    }
                }
            }
            pSpieler.send(temp);
        }
        pSpieler.send(">      1 2 3 4 5 6 7 8 9");
        pSpieler.send(">end GAME");
    }

    /**
     * sendet das Spielfeld an Alle
     */
    public void sendeSpielfeldAnAlle() {
        this.sendeSpielfeld(gibSpielerNr(1));
        this.sendeSpielfeld(gibSpielerNr(2));
    }

    public Spieler gibSpielerAmZug() {
        return spielerListe[aktiv];
    }

}
