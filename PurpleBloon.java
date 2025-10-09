import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class PurpleBloon here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PurpleBloon extends Bloon
{
    private static final double SPEED = 3;

    public PurpleBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null); // nextTier handled by getChildTier()
        GreenfootImage bloon = new GreenfootImage("Purple_Bloon.png");
        bloon.scale(59, 78);
        setImage(bloon);
        contactDamage = 2;
    }
    @Override
    protected void pop() {
        World world = getWorld();
        if (world == null) return;

        int x = getX();
        int y = getY();

        // Spawn Black and White Bloons slightly offset horizontally
        PinkBloon Pink1 = new PinkBloon(direction, laneY);
        PinkBloon Pink2 = new PinkBloon(direction, laneY);

        int offset = 12;
        world.addObject(Pink1, x - offset, y);
        world.addObject(Pink2, x + offset, y);

        world.removeObject(this);
    }
    public void takeDamage(int dmg, DamageType type) {
    if (isImmuneTo(type)) {
        // Play a sound for hitting an immune bloon
        GreenfootSound immuneSound = new GreenfootSound("PurpleSound.mp3"); // replace with your sound
        immuneSound.setVolume(40);
        immuneSound.play();
        return; // no damage applied
    }

    health -= dmg;
    if (health <= 0) {
        pop();
    }
}

    
}
