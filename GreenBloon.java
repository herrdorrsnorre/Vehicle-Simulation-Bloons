import greenfoot.*;

public class GreenBloon extends Bloon {
    private static final double SPEED = 1.8; // inherent speed

    public GreenBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, BlueBloon.class); // next tier handled by getChildTier()
        setImage("Green_Bloon.png");
    }

    @Override
    protected Class<? extends Bloon> getChildTier() {
        return BlueBloon.class; // next tier when popped
    }
}
