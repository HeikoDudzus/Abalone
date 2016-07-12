
/**
 * Vektoren fuer Verschiebungen auf dem Abalone-Brett in orthogonalisierter Repraesentation
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Vektor
{
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
