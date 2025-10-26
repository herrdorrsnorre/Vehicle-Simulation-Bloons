import greenfoot.*;
import java.util.List;
/**
 * The {@code Monkey} class is the abstract base for all monkey units in the game.
 * <p>
 * Monkeys patrol vertically across their lane and automatically detect and attack
 * nearby bloons within their range. When no bloons are nearby, they continue moving.
 * <p>
 * This base class handles movement, targeting, firing, health/damage,
 * and despawning logic. Subclasses only need to define visuals and specific projectile types.
 *
 * <p><b>Key features:</b>
 * <ul>
 *   <li>Automatic targeting and projectile firing</li>
 *   <li>Dynamic movement logic when bloons are or aren’t present</li>
 *   <li>Health and death management</li>
 *   <li>Flexible projectile system using reflection or subclass creation</li>
 * </ul>
 */
public abstract class Monkey extends SuperSmoothMover {
    /** Current health of the monkey. When 0 or below, it dies. */
    protected int health;
    /** Maximum attack range (in pixels). */
    protected int range;
    /** Frames between each shot (lower = faster fire rate). */
    protected int fireRate; 
    /** Frame counter to track when the next shot can be fired. */
    protected int fireTimer;
    /** The type of projectile this monkey fires. Must have a (Monkey, Bloon) constructor. */
    protected Class<? extends Projectile> projectileType;
    
    /** Walking speed of the monkey when not attacking. */
    protected int speed;     
    /** Vertical movement direction: 1 = downward, -1 = upward. */
    protected int moveDirection = 1; 
    /** Whether the monkey is currently stopping to shoot. */
    protected boolean waiting = false;
    /** Whether this monkey has completed its initial setup (rotation → direction). */
    private boolean initialized = false;
    /** Y-coordinate at which monkey should despawn (optional) */
    private int despawnY = -1;

    /**
     * Default act method called every frame.
     * Handles movement, targeting, firing, death, and despawning.
     */
    @Override
    public void act() {
        if (getWorld() == null) return;
        if (!initialized) {
            int r = getRotation();
            moveDirection = (r >= 180) ? -1 : 1;
            initialized = true;
        }
        fireTimer++;
        boolean bloonsAhead = bloonsInFront();

        if (bloonsAhead) {
            waiting = true;
            attackNearest();
        } else {
            waiting = false;
            walkAcrossStreet();
        }

        checkDeath();
        checkOutOfBounds();
        if (despawnY != -1 && ((getRotation() == 90 && getY() >= despawnY) || (getRotation() == 270 && getY() <= despawnY))) {
            getWorld().removeObject(this);
        }
    }


    /**
     * Checks if any bloons are roughly ahead within a certain distance.
     *
     * @return true if there are bloons ahead, false otherwise
     */
    private boolean bloonsInFront() {
        int lookDistance = 800; 
        int checkWidth = 150; 

        List<Bloon> bloons = getWorld().getObjects(Bloon.class);
        for (Bloon b : bloons) {
            double dx = b.getX() - getX();
            double dy = Math.abs(b.getY() - getY());

            if (dx > 0 && dx < lookDistance && dy < checkWidth) {
                return true; 
            }
        }
        return false;
    }

    /** Handles attacking logic */
    private void attackNearest() {
        if (projectileType == null) return;

        Bloon target = getNearestBloon();
        if (target != null && fireTimer >= fireRate) {
            faceTarget(target);
            fireAt(target);
            fireTimer = 0;
        }
    }

    /** Move forward if coast is clear */
    private void walkAcrossStreet() {
        if (!waiting) {
            if (moveDirection == -1) setRotation(270); 
            else setRotation(90); 
        }
        move(speed);
            if (getX() < -getImage().getWidth()/2 || getX() > getWorld().getWidth() + getImage().getWidth()/2) {
            getWorld().removeObject(this);
        }
    }

    /**
     * Returns the nearest bloon within attack range.
     *
     * @return nearest Bloon or null if none in range
     */
    protected Bloon getNearestBloon() {
        List<Bloon> bloons = getObjectsInRange(range, Bloon.class);
        if (bloons.isEmpty()) return null;

        Bloon nearest = bloons.get(0);
        double minDist = distanceTo(nearest);

        for (Bloon b : bloons) {
            double d = distanceTo(b);
            if (d < minDist) {
                minDist = d;
                nearest = b;
            }
        }
        return nearest;
    }

    /**
     * Calculates Euclidean distance to another actor.
     *
     * @param a the target actor
     * @return distance in pixels
     */
    protected double distanceTo(Actor a) {
        return Math.hypot(getX() - a.getX(), getY() - a.getY());
    }
    /**
     * Fires a projectile at the specified target.
     *
     * @param target the bloon to attack
     */
    protected void fireAt(Bloon target) {
        try {
            Projectile p = projectileType
                .getDeclaredConstructor(Monkey.class, Bloon.class)
                .newInstance(this, target);
            getWorld().addObject(p, getX(), getY());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reduces health by the specified damage and plays a blood effect.
     *
     * @param dmg amount of damage taken
     */
    public void takeDamage(int dmg) {
        health -= dmg;
    
        if (getWorld() != null) {
            getWorld().addObject(new BloodEffect(), getX(), getY());
        }
    
        if (health <= 0) die();
    }


    /**
     * Removes this monkey from the world immediately.
     */
    protected void die() {
        World world = getWorld();
        if (world != null) {
            world.removeObject(this);
        }
    }

    /**
     * Checks if the monkey should be removed due to death.
     */
    protected void checkDeath() {
        if (health <= 0 && getWorld() != null) {
            getWorld().removeObject(this);
        }
    }
    /** Remove if out of world bounds */
    protected void checkOutOfBounds() {
        if (getWorld() == null) return;
        if (getX() < 0 || getX() > getWorld().getWidth()) {
            getWorld().removeObject(this);
        }
    }
    /**
     * Rotates the monkey to face a target for aiming visuals only.
     *
     * @param target the bloon to face
     */    
    private void faceTarget(Bloon target) {
        if (target == null) return;
    
        double dx = target.getX() - getX();
        double dy = target.getY() - getY();
        int angle = (int) Math.toDegrees(Math.atan2(dy, dx));
    
        setRotation(angle);
    }

    /**
     * Sets the Y-coordinate at which the monkey will despawn.
     *
     * @param y Y-coordinate for despawn
     */
    public void setDespawnY(int y) { despawnY = y; }
}
