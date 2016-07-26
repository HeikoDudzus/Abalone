
/**
 * Write a description of class Position here.
 * 
 * @author Heiko Dudzus
 * @version 2016-07-13
 */
public class Position
{
    int x;
    int y;

    /**
     * Constructor für Position aus dem Bezeichner
     * 
     * @param feldname gibt den Feldnamen in Abalone-Notation an 
     * (z.B. I5)
     */
    public Position(String feldname)
    {
        //Was passiert im Fehlerfall?!
        if (feldname.length()!=2) return; // falsche Eingabe
        if (feldname.charAt(0)<'A' || feldname.charAt(0)>'I') return; //kein Buchstabe 
        if (feldname.charAt(1)<'1' || feldname.charAt(1)>'9') return; //falsche Zahl
        
        y = feldname.charAt(0)-'A'+1;
        x = feldname.charAt(1)-'0';
    }
    
    /**
     * Constructor for objects of class Position
     */
    public Position(int pX, int pY)
    {
        x = pX;
        y = pY;
    }

    public int gibX() {
        return x;
    }
    
    public int gibY() {
        return y;
    }
    
    public Position gibZiel(Vektor v) {
        return new Position(x+v.gibX(), y+v.gibY());
    }
    
    public Position gibMitte(Position p) {
        return new Position( (x+p.gibX())/2, (y+p.gibY())/2 );
    }
    
    /**
     * zulaessig prüft ob die Position im Feld liegt
     * 
     * @return
     */
    public boolean zulaessig() {
        return (x > 0 && x <=9 && y>=0 && y <= 9 && (x>3 || y-x >= 4) && (x < 6 || x-y >=4));
    }
    
    /**
     * toString gibt die Position in menschenlesbarer Notation aus
     */
    public String toString() {
        return "" + (char)('A'-1+y) + x;
    }
    
    /**
     * gibt die Position zurück, wie sie einem um 180° gedrehten Feld enstprechen würde
     */
    public Position flipped() {
        return new Position(10-x, 10-y);
    }
}
