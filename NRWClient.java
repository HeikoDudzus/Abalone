import javax.swing.JTextArea;


public class NRWClient extends Client 
{

    private ClientGUI ausgabe;
    
    public NRWClient(String ip, int port, ClientGUI ausgabe){
        super(ip, port);
        this.ausgabe = ausgabe;
    }
    
    public void processMessage(String antwort){
        ausgabe.textAusgeben(antwort);
    }

    public void execServerCmd(String nachricht){
        super.send(nachricht);
    }
    
    public void fluestere(String nachricht, String empfaenger){
        super.send("WHSP " + empfaenger + " " + nachricht);
    }
    
    public void beenden(){
        super.send("QUIT");
    }
    
    public boolean isConnected() {
        return true;
    }
}
