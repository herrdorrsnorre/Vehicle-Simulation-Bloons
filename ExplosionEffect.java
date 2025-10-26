import greenfoot.*;
/**
 * Represents a short-lived explosion animation effect with sound.
 * <p>
 * When created, the {@code ExplosionEffect} plays a sequence of preloaded explosion
 * images and an explosion sound, then removes itself from the world when the animation
 * finishes. The explosion’s visual size scales with the provided radius.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Scales automatically based on explosion radius.</li>
 *   <li>Plays a synchronized sound effect on creation.</li>
 *   <li>Animates through multiple frames with adjustable delay.</li>
 *   <li>Automatically cleans up when finished (self-removal).</li>
 * </ul>
 *
 * <p>This effect is purely visual—it does not apply damage. Damage should be handled
 * by the projectile or tower that spawns it.</p>
 */
public class ExplosionEffect extends Actor {

    /** Total number of animation frames (explosion1.png through explosion7.png). */
    private static final int FRAME_COUNT = 7;

    /** Array holding all preloaded animation frames for the explosion. */
    private final GreenfootImage[] frames = new GreenfootImage[FRAME_COUNT];

    /** The current animation frame index. */
    private int currentFrame = 0;

    /** Number of act cycles to wait before changing frames. */
    private int frameDelay = 2;

    /** Counter tracking act cycles since the last frame change. */
    private int delayCounter = 0;

    /** Factor by which explosion images are scaled relative to their default size. */
    private final double scaleFactor;

    /**
     * Constructs a new explosion visual effect.
     * <p>
     * The explosion animation is scaled according to the given radius, and
     * automatically plays a sound effect when spawned.
     * </p>
     *
     * @param radius The radius of the explosion (used only to determine visual scale).
     */
    public ExplosionEffect(int radius) {
        scaleFactor = Math.max(0.5, radius / 100.0);

        for (int i = 0; i < FRAME_COUNT; i++) {
            GreenfootImage img = new GreenfootImage("explosion/explosion" + (i + 1) + ".png");

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

    /**
     * Called each frame to animate the explosion.
     * <ul>
     *   <li>Progresses through animation frames based on {@link #frameDelay}.</li>
     *   <li>Removes this object from the world once the animation completes.</li>
     * </ul>
     */
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
