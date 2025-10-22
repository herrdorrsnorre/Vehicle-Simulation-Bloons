import greenfoot.*;

public class IceBlastEffect extends Actor {
    private static final int FRAME_COUNT = 8; // Ice1.png–Ice8.png
    private GreenfootImage[] frames = new GreenfootImage[FRAME_COUNT];
    private int currentFrame = 0;
    private int frameDelay = 2;
    private int delayCounter = 0;

    /**
     * @param radius The freeze radius to visually match.
     */
    public IceBlastEffect(int radius) {
        // Each frame gets scaled to roughly cover the diameter of the AOE
        int targetSize = radius * 2; // full diameter

        for (int i = 0; i < FRAME_COUNT; i++) {
            GreenfootImage img = new GreenfootImage("ice/Ice" + (i + 1) + ".png");
            
            // Make sure it's never smaller than your old size (250×250 baseline)
            int scaledWidth = Math.max(targetSize, 250);
            int scaledHeight = Math.max(targetSize, 250);
            img.scale(scaledWidth, scaledHeight);

            // Add slight transparency to make it feel like an icy aura
            img.setTransparency(200);

            frames[i] = img;
        }

        setImage(frames[0]);

        GreenfootSound sound = new GreenfootSound("IceEffectSound.wav");
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
