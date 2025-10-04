import greenfoot.*;

public class YellowBloon extends Bloon {
    private static final double SPEED = 3.2; // inherent speed

    public YellowBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, GreenBloon.class); // nextTier handled by getChildTier()
        setImage("Yellow_Bloon.png");
    }


    @Override
    protected Class<? extends Bloon> getChildTier() {
        return GreenBloon.class; // next tier
    }
}
