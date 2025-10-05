import greenfoot.*;

public class WhiteBloon extends Bloon {
    private static final double SPEED = 2; // constant speed

    public WhiteBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, PinkBloon.class); // direction = 1, nextTier handled by getChildTier
        setImage("White_Bloon.png");
        contactDamage = 2;
    }

    @Override
    protected Class<? extends Bloon> getChildTier() {
        return PinkBloon.class; // next tier after popping
    }
}
