import greenfoot.*;

public class PinkBloon extends Bloon {
    private static final double SPEED = 3.5; // inherent speed for Pink Bloon

    public PinkBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, YellowBloon.class); // nextTier handled by getChildTier()
        GreenfootImage bloon = new GreenfootImage("Pink_Bloon.png");
        bloon.scale(63, 84);
        setImage(bloon);
    }

    @Override
    protected Class<? extends Bloon> getChildTier() {
        return YellowBloon.class; // next tier
    }

}
