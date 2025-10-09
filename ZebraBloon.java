import greenfoot.*;

public class ZebraBloon extends Bloon {
    private static final double SPEED = 1.8;
    
    public ZebraBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null); // direction = 1, nextTier handled manually in pop
        GreenfootImage bloon = new GreenfootImage("Zebra_Bloon.png");
        bloon.scale(59, 78);
        setImage(bloon);
        contactDamage = 3;
    }

    @Override
    protected void pop() {
        World world = getWorld();
        if (world == null) return;

        int x = getX();
        int y = getY();

        // Spawn Black and White Bloons slightly offset horizontally
        BlackBloon black = new BlackBloon(1, laneY);
        WhiteBloon white = new WhiteBloon(1, laneY);

        int offset = 12;
        world.addObject(black, x - offset, y);
        world.addObject(white, x + offset, y);

        world.removeObject(this);
    }
}
