import greenfoot.*;

public class WhiteBloon extends Bloon {
    private static final double SPEED = 2; // constant speed

    public WhiteBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null); // direction = 1, nextTier handled by getChildTier
        setImage("White_Bloon.png");
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
