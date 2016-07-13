
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
    private String ip;
    private int port;
    private int zustand;
    private char symbol;
    private int anzahlSteine;
    //private List<Steine> steineListe;

    /**
     * Constructor for objects of class Spieler
     */
    public Spieler(String pIP, int pPort)
    {
        ip = pIP;
        port = pPort;
        anzahlSteine = 0;
    }
    
    public String gibIP(){
        return ip;
    }

    public int gibPort(){
        return port;
    }
    
    public boolean vergleicheIPUndPort(String pIP, int pPort) {
        return (this.ip.equals(pIP) && this.port == pPort);
    }
    
    public String gibName()
    {
        return name;
    }
    
    public void setzeName(String pName){
        this.name = pName;
    }
    
     public int gibZustand() {
        return zustand;
    }
    
    public void setzeZustand(int pZustand){
        this.zustand = pZustand;
    }
    
    public char gibSymbol() {
        return symbol;
    }

    public void setzeSymbol(char pSymbol) {
        symbol = pSymbol;
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
