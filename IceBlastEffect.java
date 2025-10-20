import greenfoot.*;

public class IceBlastEffect extends Actor {
    private static final int FRAME_COUNT = 8; // matches your files: Ice1.png–Ice8.png
    private GreenfootImage[] frames = new GreenfootImage[FRAME_COUNT];
    private int currentFrame = 0;
    private int frameDelay = 2;
    private int delayCounter = 0;

    public IceBlastEffect() {
        for (int i = 0; i < FRAME_COUNT; i++) {
            GreenfootImage img = new GreenfootImage("ice/Ice" + (i + 1) + ".png");
            img.scale(250, 250); // much larger visual radius
            frames[i] = img;
        }
        setImage(frames[0]);

        GreenfootSound sound = new GreenfootSound("IceEffectSound.wav");
        sound.setVolume(35);
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
