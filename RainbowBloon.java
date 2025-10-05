import greenfoot.*;

public class RainbowBloon extends Bloon {
    private static final double SPEED = 2.2;

    public RainbowBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null); // direction = 1, nextTier handled manually in pop
        setImage("Rainbow_Bloon.png");
        contactDamage = 4;
    }

    @Override
    protected void pop() {
        World world = getWorld();
        if (world == null) return;

        int x = getX();
        int y = getY();

        // Spawn 2 Zebra Bloons slightly offset horizontally
        int offset = 12;
        ZebraBloon zebra1 = new ZebraBloon(1, laneY);
        ZebraBloon zebra2 = new ZebraBloon(1, laneY);

        world.addObject(zebra1, x - offset, y);
        world.addObject(zebra2, x + offset, y);

        world.removeObject(this);
    }
}
