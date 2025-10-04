import greenfoot.*;

public class RedBloon extends Bloon {
    private static final double SPEED = 1.0;

    public RedBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null); // final tier
        setImage("Red_Bloon.png");
    }

    @Override
    protected void pop() {
        World world = getWorld();
        if (world != null) {
            world.removeObject(this);
        }
    }

}
