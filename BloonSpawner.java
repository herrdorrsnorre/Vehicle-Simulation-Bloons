import greenfoot.*;

/**
 * A BloonSpawner is a point where Bloons enter the track.
 * It ensures spacing between Bloons.
 */
public class BloonSpawner extends Actor {
    public static final int DIST_BETWEEN_BLOONS = 128;

    private int laneNumber;

    public BloonSpawner(int laneHeight, int laneNumber) {
        this.laneNumber = laneNumber;

        int height = (int)(laneHeight * 0.75);
        int width = DIST_BETWEEN_BLOONS;

        GreenfootImage image = new GreenfootImage(width, height);
        if (BloonWorld.SHOW_SPAWNERS) {
            image.setColor(new Color(255, 0, 0, 128));
            image.fillRect(0, 0, width - 1, height - 1);
            image.setColor(Color.WHITE);
            image.drawString("" + laneNumber, 10, (int)(height * 0.8));
        }
        setImage(image);
    }

    public boolean isTouchingBloon() {
        return isTouching(Bloon.class);
    }

    public int getLaneNumber() {
        return laneNumber;
    }
}
