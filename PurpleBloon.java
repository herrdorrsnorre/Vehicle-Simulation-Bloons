import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class PurpleBloon here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PurpleBloon extends Bloon
{
    private static final double SPEED = 3.6; // inherent speed for Pink Bloon

    public PurpleBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null); // nextTier handled by getChildTier()
        setImage("Purple_Bloon.png");
        contactDamage = 2;
    }
    @Override
    protected void pop() {
        World world = getWorld();
        if (world == null) return;

        int x = getX();
        int y = getY();

        // Spawn Black and White Bloons slightly offset horizontally
        PinkBloon Pink1 = new PinkBloon(1, laneY);
        PinkBloon Pink2 = new PinkBloon(1, laneY);

        int offset = 12;
        world.addObject(Pink1, x - offset, y);
        world.addObject(Pink2, x + offset, y);

        world.removeObject(this);
    }
    
}
