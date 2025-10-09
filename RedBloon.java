import greenfoot.*;

public class RedBloon extends Bloon {
    private static final double SPEED = 1.0;

    public RedBloon(int direction, int laneY) {
        super(SPEED, 1, direction, laneY, null); // final tier
        GreenfootImage bloon = new GreenfootImage("Red_Bloon.png");
        bloon.scale(45, 60);
        setImage(bloon);
    }

    @Override
    protected void pop() {
        World world = getWorld();
        if (world != null) {
            world.removeObject(this);
        }
    }

}
