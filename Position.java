
/**
 * Write a description of class Position here.
 * 
 * @author Heiko Dudzus
 * @version 2016-07-13
 */
public class Position
{
    int x;
    int y;

    /**
     * Constructor for objects of class Position
     */
    public Position(int pX, int pY)
    {
        x = pX;
        y = pY;
    }

    public int gibX() {
        return x;
    }
    
    public int gibY() {
        return y;
    }
}
