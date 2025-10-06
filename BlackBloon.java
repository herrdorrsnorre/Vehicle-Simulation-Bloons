import greenfoot.*;

public class BlackBloon extends Bloon {
    private static final double SPEED = 1.8; // constant speed

    public BlackBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null); // direction = 1, nextTier handled by getChildTier
        setImage("Black_Bloon.png");
        contactDamage = 2;
    }

    @Override
    protected void pop() {
        World world = getWorld();
        if (world == null) return;

        int x = getX();
        int y = getY();

        PinkBloon Pink1 = new PinkBloon(1, laneY);
        PinkBloon Pink2 = new PinkBloon(1, laneY);

        int offset = 12;
        world.addObject(Pink1, x - offset, y);
        world.addObject(Pink2, x + offset, y);

        world.removeObject(this);
    }
 
}
