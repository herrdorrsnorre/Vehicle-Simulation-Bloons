import greenfoot.*;

public class ExplosionEffect extends Actor {
    private static final int FRAME_COUNT = 7;
    private GreenfootImage[] frames = new GreenfootImage[FRAME_COUNT];
    private int currentFrame = 0;
    private int frameDelay = 2;
    private int delayCounter = 0;

    private double scaleFactor;

    /**
     * @param radius The explosion radius (used to determine visual size)
     */
    public ExplosionEffect(int radius) {
        // Convert radius to scale factor (you can tweak the 1.0 → 2.0 range)
        scaleFactor = Math.max(0.5, radius / 100.0);

        for (int i = 0; i < FRAME_COUNT; i++) {
            GreenfootImage img = new GreenfootImage("explosion/explosion" + (i + 1) + ".png");

            // Scale each frame according to explosion radius
            int scaledWidth = (int)(img.getWidth() * scaleFactor);
            int scaledHeight = (int)(img.getHeight() * scaleFactor);
            img.scale(scaledWidth, scaledHeight);

            frames[i] = img;
        }

        setImage(frames[0]);

        GreenfootSound sound = new GreenfootSound("explosion.wav");
        sound.setVolume(80);
        sound.play();
    }

    @Override
    public void act() {
        delayCounter++;
        if (delayCounter >= frameDelay) {
            delayCounter = 0;
            currentFrame++;
            if (currentFrame < FRAME_COUNT) {
                setImage(frames[currentFrame]);
            } else if (getWorld() != null) {
                getWorld().removeObject(this);
            }
        }
    }
}
