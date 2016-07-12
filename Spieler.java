
/**
 * Write a description of class Spieler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
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
    
    //     public int gibAnzahlSteine() {
    //         return anzahlSteine;
    //     }
    
    public boolean hatVerloren() {
        return anzahlSteine < 9;
    }

    public void erhalteStein() {
        anzahlSteine++;
    }

    public void gibSteinAb() {
        anzahlSteine--;
    }
    //     private void setzeStein (Stein pStein) {
    //         
    //     }
    //     
    //     public void entferneStein(Stein pStein) {
    //         steine.toFirst();
    //         while (steine.hasAccess()) {
    //             if (steine.getContent() == pStein) {
    //                 steine.remove();
    //                 anzahlSteine--;
    //             }
    //             steine.next();
    //         }
    //     }
    //     
    //     public int gibAnzahlSteine() {
    //         return anzahlSteine;
    //     }
}
