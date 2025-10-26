import greenfoot.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Represents a returning projectile that behaves like a boomerang.
 * <p>
 * The {@code Boomerang} travels forward a set distance, damaging bloons it collides with.
 * Once it reaches its maximum distance, it reverses direction and returns to its source.
 * It can pierce through multiple bloons, damaging each once per flight direction.
 * </p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Travels up to a maximum distance before returning.</li>
 *   <li>Deals {@code NORMAL} damage to non-immune bloons.</li>
 *   <li>Resets pierce count when returning to allow for additional hits.</li>
 *   <li>Returns to the original tower (monkey), and disappears upon arrival.</li>
 * </ul>
 */
public class Boomerang extends Projectile {
    /** Maximum distance the boomerang travels before returning. */
    private final int maxDistance = 500;

    /** Movement speed of the boomerang per act cycle. */
    private final int speed = 8;

    /** Number of bloons the boomerang can hit before being destroyed (per phase). */
    private final int basePierce = 5;

    /** Tracks how far the boomerang has traveled. */
    private int traveled = 0;

    /** Current remaining pierce count. */
    private int pierce;

    /** Whether the boomerang is currently returning to its source. */
    private boolean returning = false;

    /** Reference to the source tower that fired this projectile. */
    private Monkey source;

    /** Set of bloons already hit, to prevent multiple hits per pass. */
    private Set<Bloon> hitBloons = new HashSet<>();

    /** Fallback coordinates in case the source tower moves or is removed. */
    private int fallbackX, fallbackY;

    /**
     * Constructs a new {@code Boomerang} projectile.
     *
     * @param source the tower that launched this boomerang
     * @param target the bloon initially targeted by this projectile
     */
    public Boomerang(Monkey source, Bloon target) {
        super(source, target);
        this.source = source;
        this.pierce = basePierce;
        setImage("Boomerang.png");

        if (source != null) {
            setRotation(source.getRotation());
            fallbackX = source.getX();
            fallbackY = source.getY();
        }
        
    }
    /**
     * Updates the projectile each frame.
     * <p>
     * The boomerang either travels outward or returns to its source.
     * It checks for collisions with bloons and removes itself upon
     * hitting too many targets or reaching its source again.
     * </p>
     */
    @Override
    public void act() {
        if (getWorld() == null) return;

        if (!returning) {
            move(speed);
            traveled += speed;

            if (traveled >= maxDistance) {
                returning = true;
                hitBloons.clear(); 
            }
        } else {
            if (source != null && source.getWorld() != null) {
                fallbackX = source.getX();
                fallbackY = source.getY();
            }

            turnTowards(fallbackX, fallbackY);
            move(speed);

            if (distanceTo(fallbackX, fallbackY) < speed + 2) {
                getWorld().removeObject(this);
                return;
            }
        }

        checkCollision();
    }
    /**
     * Checks for collisions with bloons and applies damage if appropriate.
     * <p>
     * - Deals {@code NORMAL} damage to non-immune bloons.<br>
     * - Plays a sound and is destroyed if it hits an immune bloon.<br>
     * - Tracks previously hit bloons to avoid double damage per phase.<br>
     * - Removes itself if pierce count reaches zero.
     * </p>
     */
    @Override    
    protected void checkCollision() {
        World world = getWorld();
        if (world == null) return;
        List<Bloon> bloons = getIntersectingObjects(Bloon.class);
        if (bloons == null || bloons.isEmpty()) return;
        for (Bloon b : bloons) {
            if (b == null || hitBloons.contains(b)) continue;
    
            if (b.isImmuneTo(DamageType.NORMAL)) {
                GreenfootSound immuneSound = new GreenfootSound("LeadSound.wav");
                immuneSound.setVolume(40);
                immuneSound.play();
                if (getWorld() != null) {
                    world.removeObject(this);
                    return;
                }
            } else {
                b.takeDamage(1, DamageType.NORMAL);
                hitBloons.add(b);
                pierce--;
                if (pierce <= 0 && getWorld() != null) {
                    world.removeObject(this);
                    return;
                }
            }
        }
    }
    /**
     * Calculates the Euclidean distance from the boomerang to a given point.
     *
     * @param x the target x-coordinate
     * @param y the target y-coordinate
     * @return the straight-line distance to the specified point
     */
    private double distanceTo(int x, int y) {
        return Math.hypot(getX() - x, getY() - y);
    }
}
