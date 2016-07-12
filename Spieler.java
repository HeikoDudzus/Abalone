
/**
 * Write a description of class Spieler here.
 * 
 * @author Heiko Dudzus
 * @version 2016-07-12
 */
public class Spieler implements Zustand
{
    // instance variables - replace the example below with your own
    private String name;
    private int zustand;
    private char symbol;
    private int anzahlSteine;
    //private List<Steine> steineListe;

    /**
     * Constructor for objects of class Spieler
     */
    public Spieler(String pName)
    {
        name = pName;
        zustand = WAIT;
        anzahlSteine = 0;
        //steineListe = new List<Stein>();
    }

    public char gibSymbol() {
        return symbol;
    }

    public void setzeSymbol(char pSymbol) {
        symbol = pSymbol;
    }

    public int gibZustand() {
        return zustand;
    }

    public void setzeZustand(int pZustand) {
        zustand = pZustand;
    }
    
    public String gibName() {
        return name;
    }
    
    public boolean hatVerloren() {
        return anzahlSteine < 9;
    }

    public void erhalteStein() {
        anzahlSteine++;
    }

    public void gibSteinAb() {
        anzahlSteine--;
    }
}
