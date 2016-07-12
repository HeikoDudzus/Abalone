
/**
 * Write a description of class Spielfeld here.
 * 
 * @author Heiko Dudzus
 * @version 2016-07-11
 */
public class Spiel implements Zustand
{
    // instance variables - replace the example below with your own
    private Feld[][] spielfeld;
    private List<Spieler> spielerListe;
    public Vektor rechtsOben = new Vektor(1,1);
    public Vektor linksUnten = new Vektor(-1,-1);
    public Vektor hoch = new Vektor (0,1);
    public Vektor runter = new Vektor(0,-1);
    public Vektor rechts = new Vektor(1,0);
    public Vektor links = new Vektor(-1,0);

    /**
     * Constructor for objects of class Spielfeld
     */
    public Spiel(Spieler pSpieler1, Spieler pSpieler2)
    {
        spielfeld = new Feld[11][11];
        spielerListe = new List<Spieler>();
        spielerListe.append(pSpieler1);
        spielerListe.append(pSpieler2);

        for (int zeile = 0; zeile < 11; zeile++) {
            for (int spalte = 0; spalte < 11; spalte++) {
                spielfeld[zeile][spalte] = new Feld();
            }
        }

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
    }
    
    /**
     * Wrapper fuer die Methode schiebeRekursiv(). Es wird eine Verschiebung an gegebener 
     * Position in eine gegebene Richtung angefragt.
     * @param pZeile Zeilennummer des Spielfeldes
     * @param pSpalte Spaltennummer des Spielfeldes
     * @param pRichtung Richtungsvektor der Verschiebung
     * @param pSpieler Spieler, der den Spielzug ausfuehren moechte
     * @return Wahrheitswert, ob die geplante Verschiebung durchgefuehrt werden kann
     */
    public boolean schiebe (int pZeile, int pSpalte, Vektor pRichtung, Spieler pSpieler) {
        if (!pRichtung.richtungErlaubt()) return false;
        Stein stein = spielfeld[pZeile][pSpalte].gibStein();
        if (stein == null) return false;
        if (stein.gibBesitzer() != pSpieler) return false;
        if (schiebeRekursiv(pZeile, pSpalte, pRichtung, pSpieler, 0, 0, stein)) {
            // Der Stein konnte verschoben werden, entferne ihn hier
            spielfeld[pZeile][pSpalte].setzeStein(null);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Es wird versucht, Steine auf der angegebenen Spielfeld-Position werden rekursiv in
     * die angegebene Richtung zu verschieben. Im Erfolgsfall wird nach dem Verschieben
     * der uebergebene Stein auf dem Feld platziert.
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
        |    G  · · O O O · · 
        |   F  · · · · · · · · 
        |  E  · · · · · · · · · 
        |   D  · · · · · · · · 9
        |    C  · · X X X · · 8
        |     B  X X X X X X 7
        |      A  X X X X X 6
        |          1 2 3 4 5

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

    public String toString() {
        String out = "";
        for (int zeile = 9; zeile > 0; zeile--) {
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
        return out;
    }

    public void toConsole() {
        System.out.print(toString());
    }

    public class Vektor {
        private int dx, dy;

        Vektor()
        {
            dx = 0;
            dy = 0;
        }

        Vektor(int dx, int dy)
        {
            this.dx = dx;
            this.dy = dy;
        }

        public int gibX() {
            return dx;
        }

        public int gibY() {
            return dy;
        }

        public boolean richtungErlaubt() {
            /* Im Rahmen des Spiels sind nur die Verschiebungen
             * (1,1), (-1,-1)
             * (1,0), (-1,0)
             * (0,1), (0,-1)
             * erlaubt. Insbesondere sind (1,-1) und (-1,1) verboten, sowie Verschiebungen
             * um 2 Stellen.
             */
            if (dx*dx + dy*dy > 2) return false;
            if (dx + dy == 0) return false;
            return true;
        }
    }
}
