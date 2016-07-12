/**
 * Write a description of class Feld here.
 * 
 * @author Heiko Dudzus
 * @version 2016-07-11
 */
public class Feld
{
    private Stein inhalt;

    /**
     * Constructor for objects of class Feld
     */
    public Feld()
    {
        inhalt = null;
    }

    public void setzeStein(Stein pStein) {
        inhalt = pStein;
    }
    
    public Stein gibStein() {
        return inhalt;
    }
    
    public boolean istFrei() {
        return inhalt == null;
    }
}
