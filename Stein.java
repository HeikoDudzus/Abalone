
/**
 * Write a description of class Stein here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
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
