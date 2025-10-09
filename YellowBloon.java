import greenfoot.*;

public class YellowBloon extends Bloon {
    private static final double SPEED = 3.2; // inherent speed

    public YellowBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, GreenBloon.class); // nextTier handled by getChildTier()
        GreenfootImage bloon = new GreenfootImage("Yellow_Bloon.png");
        bloon.scale(59, 78);
        setImage(bloon);
    }


    @Override
    protected Class<? extends Bloon> getChildTier() {
        return GreenBloon.class; // next tier
    }
}
