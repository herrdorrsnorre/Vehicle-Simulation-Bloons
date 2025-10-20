import greenfoot.*;

public class LeadBloon extends Bloon {
    private static final double SPEED = 1;
    public LeadBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null);
        GreenfootImage bloon = new GreenfootImage("Lead_Bloon.png");
        bloon.scale(63, 84);
        setImage(bloon);
    }

    @Override
    protected void pop() {
        World world = getWorld();
        if (world == null) return;

        int x = getX();
        int y = getY();

        // Spawn Black and White Bloons slightly offset horizontally
        BlackBloon Black1 = new BlackBloon(direction, laneY);
        BlackBloon Black2 = new BlackBloon(direction, laneY);

        int offset = 12;
        world.addObject(Black1, x - offset, y);
        world.addObject(Black2, x + offset, y);

        world.removeObject(this);
    }
    public void takeDamage(int dmg, DamageType type) {
    
        if (isImmuneTo(type)) {
        // Play a sound for hitting an immune bloon
        GreenfootSound immuneSound = new GreenfootSound("LeadSound.wav"); // replace with your sound
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
