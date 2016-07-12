
/**
 * Write a description of class Stein here.
 * 
 * @author Heiko Dudzus
 * @version 2016-07-12
 */
public class Stein
{
    private Spieler besitzer;
    
    /**
     * Constructor for objects of class Stein
     */
    public Stein(Spieler pSpieler)
    {
        besitzer = pSpieler;
    }

    public Spieler gibBesitzer()
    {
        return besitzer;
    }
}
