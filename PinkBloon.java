import greenfoot.*;

public class PinkBloon extends Bloon {
    private static final double SPEED = 3.5; // inherent speed for Pink Bloon

    public PinkBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, YellowBloon.class); // nextTier handled by getChildTier()
        setImage("Pink_Bloon.png");
    }

    @Override
    protected Class<? extends Bloon> getChildTier() {
        return YellowBloon.class; // next tier
    }

}
