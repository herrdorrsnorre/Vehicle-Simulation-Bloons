import greenfoot.*;
import java.util.List;
/**
 * A homing projectile that tracks and damages bloons using magical energy.
 * <p>
 * The {@code MagicProjectile} seeks out nearby bloons and turns toward its target
 * as it travels. It can pierce multiple bloons before disappearing, unless it
 * hits a bloon that is immune to magic damage (e.g., a Purple Bloon).
 * </p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Automatically homes in on nearby bloons.</li>
 *   <li>Deals {@code MAGIC} damage to non-immune bloons.</li>
 *   <li>Destroyed immediately upon hitting a magic-immune bloon.</li>
 *   <li>Can pierce through several bloons before vanishing.</li>
 * </ul>
 */
public class MagicProjectile extends Projectile {
    /** How many bloons this projectile can hit before disappearing. */
    private int pierce = 12;
    /** Maximum turn rate in degrees per act step when homing. */
    private double turnRate = 6;
    /** The current target bloon this projectile is tracking. */
    private Bloon currentTarget;

    /**
     * Constructs a {@code MagicProjectile} originating from a tower.
     *
     * @param source the tower (monkey) that launched this projectile
     * @param target the initial bloon target
     */
    public MagicProjectile(Monkey source, Bloon target) {
        super(source, target);
        GreenfootImage magic = new GreenfootImage("magic.png");
        magic.scale(30, 30);
        setImage(magic);
        speed = 7;
        currentTarget = target;
    }


    /**
     * Performs homing movement, target acquisition, and collision handling.
     * <p>
     * Each frame, the projectile attempts to track a nearby bloon,
     * turns toward it, moves forward, and checks for collisions.
     * </p>
     */
    @Override
    public void act() {
        if (getWorld() == null) return;

        if (currentTarget == null || currentTarget.getWorld() == null) {
            currentTarget = getNearestBloon();
        }

        if (currentTarget != null && currentTarget.getWorld() != null) {
            turnTowards(currentTarget.getX(), currentTarget.getY());
        }

        move(speed);

        checkCollision();
    }

    /**
     * Finds the nearest bloon in the world to home in on.
     *
     * @return the nearest {@code Bloon} to this projectile, or {@code null} if none exist
     */
    private Bloon getNearestBloon() {
        World world = getWorld();
        if (world == null) return null; 
        List<Bloon> bloons = world.getObjects(Bloon.class);
        if (bloons == null || bloons.isEmpty()) return null;

        Bloon nearest = null;
        double minDist = Double.MAX_VALUE;

        for (Bloon b : bloons) {
            if (b == null || b.getWorld() == null) continue;
            double d = getDistance(getX(), getY(), b.getX(), b.getY());
            if (d < minDist) {
                minDist = d;
                nearest = b;
            }
        }
        return nearest;
    }
    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param x1 x-coordinate of the first point
     * @param y1 y-coordinate of the first point
     * @param x2 x-coordinate of the second point
     * @param y2 y-coordinate of the second point
     * @return the straight-line distance between the two points
     */
    private double getDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks for collisions with bloons and applies magic damage when appropriate.
     * <p>
     * - If a bloon is immune to magic, a sound plays and the projectile is destroyed.<br>
     * - Otherwise, the bloon takes one point of {@code MAGIC} damage.<br>
     * - Each hit decreases pierce; the projectile is removed once pierce reaches zero.
     * </p>
     */
    @Override  
    protected void checkCollision() {
        World world = getWorld();
        if (world == null) return;

        List<Bloon> bloons = getIntersectingObjects(Bloon.class);
        if (bloons == null || bloons.isEmpty()) return;

        for (Bloon b : bloons) {
            if (b == null) continue;
        
            if (b.isImmuneTo(DamageType.MAGIC)) {
                GreenfootSound immuneSound = new GreenfootSound("PurpleSound.wav");
                immuneSound.setVolume(40);
                immuneSound.play();
        
                if (getWorld() != null) {
                    world.removeObject(this);
                    return;
                }
            } else {
                b.takeDamage(1, DamageType.MAGIC);
                pierce--;
                if (pierce <= 0 && getWorld() != null) {
                    world.removeObject(this);
                    return;
                }
            }
        }

    }
}
