import greenfoot.*;
/**
 * Represents a projectile fired by a {@link Monkey} toward a {@link Bloon}.
 * <p>
 * This abstract class provides base functionality for all projectiles in the game,
 * including movement, target tracking, and collision detection. Subclasses can
 * customize behavior by overriding {@link #onHit(Bloon)} or by modifying speed,
 * damage type, and pierce properties.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Moves toward its assigned target each frame.</li>
 *   <li>Removes itself when colliding with a Bloon or leaving the world bounds.</li>
 *   <li>Prevents double-hit issues using an internal {@code hasHit} flag.</li>
 * </ul>
 *
 * <p><b>Usage:</b> Extend this class (e.g. {@code Dart}, {@code SniperBullet}, {@code FlameShot})
 * and implement unique hit or visual effects via {@link #onHit(Bloon)}.</p>
 */
public abstract class Projectile extends Actor {

    /** The tower that fired this projectile. */
    protected Monkey source;

    /** The target Bloon this projectile is tracking. */
    protected Bloon target;

    /** The movement speed of the projectile (in pixels per frame). */
    protected int speed = 8;

    /** Cached reference to the world this projectile exists in. */
    protected World worldRef;

    /** Flag to ensure the projectile only hits once. */
    private boolean hasHit = false;

    /**
     * Constructs a projectile fired from a given source toward a target Bloon.
     *
     * @param source The {@link Monkey} that fired this projectile.
     * @param target The {@link Bloon} that this projectile is intended to hit.
     */
    public Projectile(Monkey source, Bloon target) {
        this.source = source;
        this.target = target;
    }

    /**
     * Called automatically when the projectile is added to the world.
     * Stores a world reference and rotates the projectile toward its initial target.
     *
     * @param world The {@link World} this projectile was added to.
     */
    @Override
    public void addedToWorld(World world) {
        worldRef = world;
        if (target != null && target.getWorld() != null) {
            turnTowards(target.getX(), target.getY());
        }
    }

    /**
     * Called every frame to update projectile movement and handle collisions.
     * <ul>
     *   <li>Tracks and faces its target if available.</li>
     *   <li>Moves forward at its defined speed.</li>
     *   <li>Checks for collisions with Bloons and removes itself when hitting or exiting the world.</li>
     * </ul>
     */
    @Override
    public void act() {
        if (worldRef == null) return;

        if (target != null && target.getWorld() != null) {
            turnTowards(target.getX(), target.getY());
        }

        move(speed);
        checkCollision();
    }

    /**
     * Checks for collisions with Bloons and applies damage if contact occurs.
     * Prevents multiple hits using {@link #hasHit}.
     */
    protected void checkCollision() {
        if (worldRef == null || hasHit) return;

        Bloon b = (Bloon) getOneIntersectingObject(Bloon.class);
        if (b != null) {
            b.takeDamage(1, DamageType.NORMAL);
            hasHit = true; 
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
        }
    }
}
