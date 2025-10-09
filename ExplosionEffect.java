import greenfoot.*;

public class ExplosionEffect extends Actor {
    private static final int FRAME_COUNT = 7;
    private GreenfootImage[] frames = new GreenfootImage[FRAME_COUNT];
    private int currentFrame = 0;
    private int frameDelay = 2;
    private int delayCounter = 0;

    public ExplosionEffect() {
        // Load frames named Explosion1.png to Explosion20.png
        for (int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = new GreenfootImage("explosion/explosion" + (i + 1) + ".png");
        }
        setImage(frames[0]);
        GreenfootSound sound = new GreenfootSound("explosion.mp3");
        sound.setVolume(50);
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
