

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class Testklasse.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class Testklasse
{
    private Spieler spieler1;
    private Spieler spieler2;
    private Spiel spiel1;
    private Vektor v;
    private Vektor runter;
    private Vektor rechtsOben;
    private Vektor linksUnten;
    private Vektor hoch;
    private Vektor runter2;
    private Vektor rechts;
    private Vektor links;
    private AbaloneGUI abaloneG1;

    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    

    
    
    
    

    /**
     * Default constructor for test class Testklasse
     */
    public Testklasse()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
        spieler1 = new Spieler("", 0);
        spieler2 = new Spieler("", 0);
        spieler1.setzeName("Heiko");
        spieler2.setzeName("Mona");
        spieler1.setzeSymbol('X');
        spieler2.setzeSymbol('O');
        spiel1 = new Spiel(spieler1, spieler2);
        //rechtsOben = spiel1.rechtsOben;
        //spiel1.schiebeStein(1, 1, v);
        runter = spiel1.runter;
        rechtsOben = spiel1.rechtsOben;
        linksUnten = spiel1.linksUnten;
        hoch = spiel1.hoch;
        runter2 = spiel1.runter;
        rechts = spiel1.rechts;
        links = spiel1.links;
        abaloneG1 = new AbaloneGUI();
        abaloneG1.setzeSpiel(spiel1);
        abaloneG1.zeigeSpiel();
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }
}
