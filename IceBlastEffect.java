import greenfoot.*;
import java.util.List;

public class IceBlastEffect extends Actor {
    private static final int FRAME_COUNT = 8; // Ice1.png–Ice8.png
    private GreenfootImage[] frames = new GreenfootImage[FRAME_COUNT];
    private int currentFrame = 0;
    private int frameDelay = 2;
    private int delayCounter = 0;
    private final int radius;
    private boolean triggered = false; // ensure damage+freeze runs once
    private int freezeDuration = 150; // same as IceMonkey

    public IceBlastEffect(int radius) {
        this.radius = radius;
        int targetSize = radius * 2;

        for (int i = 0; i < FRAME_COUNT; i++) {
            GreenfootImage img = new GreenfootImage("ice/Ice" + (i + 1) + ".png");
            int scaledW = Math.max(targetSize, 250);
            int scaledH = Math.max(targetSize, 250);
            img.scale(scaledW, scaledH);
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
        // Run damage+freeze exactly once on the first act tick after added to world
        if (!triggered) {
            triggered = true;
            World w = getWorld();
            if (w != null) {
                // Step 1: deal 1 damage to all non-immune bloons in range
                List<Bloon> bloons = getObjectsInRange(radius, Bloon.class);
                for (Bloon b : bloons) {
                    if (!b.isImmuneTo(DamageType.ICE)) {
                        b.takeDamage(1, DamageType.ICE);
                    }
                }
                // Step 2: apply freeze to remaining (or same) bloons in range
                List<Bloon> bloons2 = getObjectsInRange(radius, Bloon.class);
                for (Bloon b : bloons2) {
                    if (!b.isImmuneTo(DamageType.ICE)) {
                        b.applyFreeze(freezeDuration);
                    }
                }
            }
        }

        // Animation timeline
        delayCounter++;
        if (delayCounter >= frameDelay) {
            delayCounter = 0;
            currentFrame++;
            if (currentFrame < FRAME_COUNT) {
                setImage(frames[currentFrame]);
            } else {
                if (getWorld() != null) getWorld().removeObject(this);
            }
        }
    }
}
