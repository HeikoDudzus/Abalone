

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
    private Spiel.Vektor v;
    private Spiel.Vektor runter;
    private Spiel.Vektor rechtsOben;
    private Spiel.Vektor linksUnten;
    private Spiel.Vektor hoch;
    private Spiel.Vektor runter2;
    private Spiel.Vektor rechts;
    private Spiel.Vektor links;

    
    
    
    
    

    
    
    
    

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
        spieler1 = new Spieler("Heiko");
        spieler2 = new Spieler("Mona");
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
