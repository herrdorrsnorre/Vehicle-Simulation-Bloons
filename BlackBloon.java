import greenfoot.*;

public class BlackBloon extends Bloon {
    private static final double SPEED = 1.8; // constant speed

    public BlackBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, PinkBloon.class); // direction = 1, nextTier handled by getChildTier
        setImage("Black_Bloon.png");
    }

    @Override
    protected Class<? extends Bloon> getChildTier() {
        return PinkBloon.class; // next tier after popping
    }
}
