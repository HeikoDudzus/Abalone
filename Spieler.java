
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
    private boolean guest = true;
    private String passwd = "";
    private GameServer myGameServer;
    //private List<Steine> steineListe;

    /**
     * Constructor for objects of class Spieler
     */
    public Spieler(String pIP, int pPort, GameServer pGameServer)
    {
        ip = pIP;
        port = pPort;
        anzahlSteine = 0;
        myGameServer = pGameServer;
    }

    public String gibIP(){
        return ip;
    }

    public void setzeIP(String pIp) {
        ip = pIp;
    }

    public int gibPort(){
        return port;
    }

    public void setzePort(int pPort) {
        port = pPort;
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

    public void erhalteSteine(int pAnz) {
        anzahlSteine+=pAnz;
    }

    public void gibSteinAb() {
        anzahlSteine--;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean pGuest) {
        guest = pGuest;
    }

    public boolean checkPW(String pPasswd) {
        return pPasswd.startsWith(passwd);
    }

    public void setPasswd(String pPasswd) {
        passwd = pPasswd;
    }
    
    public void send (String pMessage) {
        if (myGameServer != null) myGameServer.send(ip, port, pMessage);
    }
}
