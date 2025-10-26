import greenfoot.*;

public class BloodEffect extends Actor {
    private static final int FRAME_COUNT = 15;
    private GreenfootImage[] frames = new GreenfootImage[FRAME_COUNT];
    private int currentFrame = 0;
    private int frameDelay = 2;
    private int delayCounter = 0;

    public BloodEffect() {
        for (int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = new GreenfootImage("blood/Blood" + (i + 1) + ".png");
        }
        setImage(frames[0]);
        GreenfootSound hit = new GreenfootSound("hit.wav");
        hit.setVolume(50);
        hit.play();
    }

    @Override
    public void act() {
        animate();
    }

    private void animate() {
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
