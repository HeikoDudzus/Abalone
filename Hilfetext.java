
/**
 * Die Klasse Hilfetext dient dazu alle Hilfeseiten anzuzeigen
 * falls ein Servernutzer danach fragt.
 * 
 * @author Peter Scholl - scholl@unterrichtsportal.org 
 * @version 23.07.2016
 */
public class Hilfetext
{
    // Instanzvariablen - ersetzen Sie das folgende Beispiel mit Ihren Variablen
    private int x;

    /**
     * Konstruktor für Objekte der Klasse Hilfetext
     */
    public Hilfetext()
    {
        // Instanzvariable initialisieren
    }

    /**
     * Ein Beispiel einer Methode - ersetzen Sie diesen Kommentar mit Ihrem eigenen
     * 
     * @param  pMessage die Hilfsanfrage (ohne das HELP vorne weg)
     * @return        der Hilfetext als String mit ":" an jedem Zeilenanfang.
     */
    public static String gibHilfeZu(String pMessage)
    {
        String anfrage = pMessage.toUpperCase();
        String result = "::::::::: Hilfetext ::::::::\n";
        if (anfrage.startsWith("PASSWD")) {
            result+=":PASSWD <password> setzt das Passwort neu - damit verliert der Spielr\n";
            result+=": auch den Guest-Status. Bei einer erneuten Anmeldung mit dem\n";
            result+=": gleichen Namen muss dann dieses Passwort angegeben werden\n";
        } else if (anfrage.startsWith("MOVE")) {
            result+=":MOVE <spielnr> <erstesFeld> <zweitesFeld> <drittesFeld>\n:\n";
            result+=": zieht wenn es moeglich und erlaubt ist, die Steine die sich\n";
            result+=": zwischen den Feldern <erstesFeld> und <zweitesFeld> befinden\n";
            result+=": so dass der Stein auf <erstesFeld> nach dem Zug in <drittesFeld>\n";
            result+=": landet\n";
            result+=": Beispiel: MOVE 1 A2 A2 A3\n";
            result+=": zieht den Stein auf A2 zum Feld A3 (sofern moeglich und von diesem\n";
            result+=": Spieler erlaubt\n"; 
        } else if (anfrage.startsWith("QUIT")) {
            result+=":QUIT \n:\n";
            result+=": beendet die Verbindung und alle Spiele\n";
            result+=": als Guest werden die Spiele geloescht und beendet\n";
            result+=": als angemeldeter Spieler kann man später weiterspielen\n";
        } else if (anfrage.startsWith("CREATE")) {
            result+=":CREATE \n:\n";
            result+=": erzeugt ein neues Spiel, das dann von einem anderen\n";
            result+=": Spieler gejoined werden kann\n";
        } else if (anfrage.startsWith("JOIN")) {
            result+=":JOIN <gameNr> \n:\n";
            result+=": um an dem Spiel <gameNr> teilzunehmen, wenn moeglich\n";
        } else if (anfrage.startsWith("SHOW")) {
            result+=":SHOW <liste>\n:\n";
            result+=": zeigt verschiedene Inforamtionen. Moegliche <liste>:\n";
            result+=": ALLGAMES - zeigt alle Spiele auf dem server\n";
            result+=": OPEN GAMES - zeigt alle offenen Spiele, die man joinen kann\n";
            result+=": GAME <gameNr> - uebermittelt das Spiel Nr <gameNr>\n";
            result+=": (to be continued)\n";
        } else {
            //Standard-Hilfetext
            result+=":allgemeine Liste der Befehle, sie erhalten hilfe mittels HELP <befehl>\n:\n";
            result+=":QUIT - verlaesst den Server\n";
            result+=":PASSWD - zum setzen eines Passwortes\n";
            result+=":SHOW - zeigt verschiedene Informationen an (siehe Details)\n";
            result+=":MOVE - um in einem Spiel einen Zug zu machen\n";
            result+=":JOIN - um an einem Spiel teilzunehmen\n";
            result+=":CREATE - erstellt ein neues Spiel\n";

        }
        return result;
    }
}