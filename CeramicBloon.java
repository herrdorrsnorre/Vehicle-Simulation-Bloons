import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class CeramicBloon here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CeramicBloon extends Bloon
{
    /**
     * Act - do whatever the CeramicBloon wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private static final double SPEED = 2; 
    public CeramicBloon(int direction, int laneY) {
        super(SPEED, 10, direction, laneY, PinkBloon.class); // direction = 1, nextTier handled by getChildTier
        setImage("Ceramic/Ceramic_Bloon0.png");
        contactDamage = 5;
    }
    @Override
    protected Class<? extends Bloon> getChildTier() {
        return RainbowBloon.class; // returns next tier when popped
    }
    @Override
    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 8) {
            setImage("Ceramic/Ceramic_Bloon1.png");
        } if (health <=6) 
        {
            setImage("Ceramic/Ceramic_Bloon2.png");
        } if (health <= 4) 
        {
            setImage("Ceramic/Ceramic_Bloon3.png");
        }
        if (health <= 2) 
        {
            setImage("Ceramic/Ceramic_Bloon4.png");
        }
        if (health <= 0) {
            pop();
        }
    }
}
