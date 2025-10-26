import greenfoot.*;
import java.util.List;
/**
 * Represents an animated ice explosion effect that damages and freezes nearby {@link Bloon}s.
 * <p>
 * When spawned, this effect:
 * <ul>
 *   <li>Plays an animation using sequential ice frames ({@code Ice1.png}–{@code Ice8.png}).</li>
 *   <li>Deals {@link DamageType#ICE} damage to all Bloons within a specified radius.</li>
 *   <li>Applies a temporary freeze status effect to affected Bloons.</li>
 *   <li>Plays a sound effect and removes itself after the animation finishes.</li>
 * </ul>
 *
 * <p>This actor is typically spawned by a projectile (e.g. an Ice Tower shot) when it hits
 * a target, producing a visual and gameplay impact.</p>
 */
public class IceBlastEffect extends Actor {
    /** Total number of animation frames (Ice1.png through Ice8.png). */
    private static final int FRAME_COUNT = 8; 
    /** Array of preloaded animation frames for the ice effect. */    
    private GreenfootImage[] frames = new GreenfootImage[FRAME_COUNT];
    /** The current animation frame index. */
    private int currentFrame = 0;
    /** Number of act cycles to wait before switching frames. */    
    private int frameDelay = 2;
    /** Tracks cycles since the last frame change. */
    private int delayCounter = 0;
    /** The blast radius (in pixels). */
    private final int radius;
    /** Whether the blast effect has already applied damage and freeze effects. */
    private boolean triggered = false;
    /** Duration (in frames) that affected Bloons remain frozen. */
    private int freezeDuration = 150;

    /**
     * Creates a new IceBlastEffect with the specified radius.
     * <p>
     * The constructor preloads animation frames, scales them according to the radius,
     * and plays a one-time sound effect.
     * </p>
     *
     * @param radius The radius of the ice blast (in pixels).
     */
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


    /**
     * Called every frame to control the ice effect’s animation and timing.
     * <p>
     * On the first frame, applies damage and freeze effects to all {@link Bloon}s within
     * the blast radius. Then cycles through the animation frames until finished, at which
     * point the object removes itself from the world.
     * </p>
     */
    @Override
    public void act() {
        if (!triggered) {
            triggered = true;
            World w = getWorld();
            if (w != null) {
                List<Bloon> bloons = getObjectsInRange(radius, Bloon.class);
                for (Bloon b : bloons) {
                    if (!b.isImmuneTo(DamageType.ICE)) {
                        b.takeDamage(1, DamageType.ICE);
                    }
                }
                List<Bloon> bloons2 = getObjectsInRange(radius, Bloon.class);
                for (Bloon b : bloons2) {
                    if (!b.isImmuneTo(DamageType.ICE)) {
                        b.applyFreeze(freezeDuration);
                    }
                }
            }
        }

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
