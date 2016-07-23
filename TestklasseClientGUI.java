

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Die Test-Klasse TestklasseClientGUI.
 *
 * @author  (Ihr Name)
 * @version (eine Versionsnummer oder ein Datum)
 */
public class TestklasseClientGUI
{
    private GameServer gameServ1;
    private ClientGUI clientGU1;
    private ClientGUI clientGU2;

    /**
     * Konstruktor fuer die Test-Klasse TestklasseClientGUI
     */
    public TestklasseClientGUI()
    {
    }

    /**
     *  Setzt das Testgerüst fuer den Test.
     *
     * Wird vor jeder Testfall-Methode aufgerufen.
     */
    @Before
    public void setUp()
    {
        gameServ1 = new GameServer(55555);
        clientGU1 = new ClientGUI();
        clientGU2 = new ClientGUI();
        clientGU1.setLogin("peter");
        clientGU1.setPasswd("p");
        clientGU2.setLogin("hans");
        clientGU2.setPasswd("h");
    }

    /**
     * Gibt das Testgerüst wieder frei.
     *
     * Wird nach jeder Testfall-Methode aufgerufen.
     */
    @After
    public void tearDown()
    {
    }
}
