import greenfoot.*;

public class BlueBloon extends Bloon {
    private static final double SPEED = 1.4; // inherent speed

    public BlueBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, RedBloon.class); // pass null here; next tier handled in getChildTier()
        GreenfootImage bloon = new GreenfootImage("Blue_Bloon.png");
        bloon.scale(50, 66);
        setImage(bloon);
    }

    @Override
    protected Class<? extends Bloon> getChildTier() {
        return RedBloon.class; // returns next tier when popped
    }
}
