
/**
 * Vektoren fuer Verschiebungen auf dem Abalone-Brett in orthogonalisierter Repraesentation
 * 
 * @author Heiko Dudzus 
 * @version 2016-07-13
 */
public class Vektor
{
    private int dx, dy;
    
    Vektor(int fromX, int fromY, int toX, int toY)
    {
        dx = toX - fromX;
        dy = toY - fromY;
    }
    
    Vektor (Position von, Position nach) 
    {
        this(von.gibX(), von.gibY(), nach.gibX(), nach.gibY());
    }
    
    Vektor()
    {
        this(0,0,0,0);
    }

    Vektor(int dx, int dy)
    {
        this(0,0,dx,dy);
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
        //if (dx*dx + dy*dy > 2) return false;
        if (laenge() > 1) return false;  // Zug zu lang
        if (dx + dy == 0) return false;  // verbotene Diagonale
        return true;
    }
    
    /**
     * Gibt die Laenge des Vektors nach Maximum-Norm zurueck. Alle erlaubten Richtungs-Vektoren
     * haben auf dem Spielbrett die logische Laenge 1 LE. Im repraesentierenden Array haben
     * die Vektoren (1,0), (0,1) und (1,1) sowie deren additiv Inverser nach der Maximum-Norm
     * ebenfalls die Laenge 1.
     * 
     * Die Vektoren, die die "Grundseite" eines Zuges darstellen, also den Verbindungsvektor
     * zwischen dem Anfangs- und dem Endstein einer zu schiebenden Reihe, haben die logische
     * Laenge 0, 1 oder 2. Sie werden durch die Vektoren (0,0), (1,0), (0,1), (1,1), (2,0)
     * (0,2) und (2,2) sowie deren additiv Inverse dargestellt. Die logische Laenge eines
     * erlaubten Grundseitenvektors entspricht also ebenfalls der Maximum-Norm
     * 
     * @return: Laenge des Vektors nach der Maximum-Norm
     */
    public int laenge() {
        return Math.max(Math.abs(dx),Math.abs(dy));
    }
    
    /**
     * Gibt die Determinante der 2x2-Matrix zurueck, die aus dem Vektor und dem Paramter-Vektor
     * gebildet wird. Kriterium fuer lineare Unabhaengigkeit
     * @param pVektor zweiter Vektor
     * @return Determinante der gebildeten 2x2-Matrix
     */
    private int determinante(Vektor pVektor) {
        return dx*pVektor.gibY() - dy*pVektor.gibX();
    }
    
    public boolean linearAbhaengig(Vektor pVektor) {
        return determinante(pVektor) == 0;
    }
    
    public int skalarprodukt(Vektor pVektor) {
        return dx*pVektor.gibX() + dy*pVektor.gibY();
    }
}
