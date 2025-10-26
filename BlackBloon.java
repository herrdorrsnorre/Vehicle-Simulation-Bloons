import greenfoot.*;

public class BlackBloon extends Bloon {
    private static final double SPEED = 1.8; 

    public BlackBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null);
        GreenfootImage bloon = new GreenfootImage("Black_Bloon.png");
        bloon.scale(36, 48);
        setImage(bloon);
        
        contactDamage = 2;
    }

    @Override
    protected void pop() {
        World world = getWorld();
        if (world == null) return;

        int x = getX();
        int y = getY();

        PinkBloon Pink1 = new PinkBloon(direction, laneY);
        PinkBloon Pink2 = new PinkBloon(direction, laneY);

        int offset = 12;
        world.addObject(Pink1, x - offset, y);
        world.addObject(Pink2, x + offset, y);
        PopEffect pop = new PopEffect();
        world.removeObject(this);
    }
 
}
