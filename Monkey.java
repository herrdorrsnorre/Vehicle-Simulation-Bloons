import greenfoot.*;
import java.util.List;

public abstract class Monkey extends SuperSmoothMover {
    protected int health;
    protected int range;
    protected int fireRate; // frames between shots
    protected int fireTimer;
    protected Class<? extends Projectile> projectileType;
    
    // Movement
    protected int speed; // normal move speed
    protected int moveDirection = 1; // 1 = downward (90°), -1 = upward (270°)

    protected boolean waiting = false;
    private boolean initialized = false;
    @Override
    public void act() {
        if (getWorld() == null) return;
        if (!initialized) {
            int r = getRotation();
            moveDirection = (r >= 180) ? -1 : 1;
            initialized = true;
        }
        fireTimer++;

        // Detect if there are bloons ahead
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

    /** Returns true if there are any bloons roughly ahead within a certain distance. */
    private boolean bloonsInFront() {
        int lookDistance = 800; // how far ahead the monkey checks
        int checkWidth = 150; // vertical tolerance for "in the way"

        List<Bloon> bloons = getWorld().getObjects(Bloon.class);
        for (Bloon b : bloons) {
            double dx = b.getX() - getX();
            double dy = Math.abs(b.getY() - getY());

            if (dx > 0 && dx < lookDistance && dy < checkWidth) {
                return true; // something ahead
            }
        }
        return false;
    }

    /** Handles attacking logic */
    private void attackNearest() {
        if (projectileType == null) return; // no projectile monkey

        Bloon target = getNearestBloon();
        if (target != null && fireTimer >= fireRate) {
            faceTarget(target);
            fireAt(target);
            fireTimer = 0;
        }
    }

    /** Move forward if coast is clear */
    private void walkAcrossStreet() {
        // Determine lane direction: 90° (down) or 270° (up)
        // So they don’t get stuck after shooting
        if (!waiting) {
            // ensure they face their walking direction again
            if (moveDirection == -1) setRotation(270); // bottom→top
            else setRotation(90); // top→bottom
        }

        move(speed);

        // Remove if off-screen
        if (getX() < -getImage().getWidth()/2 || getX() > getWorld().getWidth() + getImage().getWidth()/2) {
            getWorld().removeObject(this);
        }
    }

    /** Find nearest Bloon within attack range */
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

    protected double distanceTo(Actor a) {
        return Math.hypot(getX() - a.getX(), getY() - a.getY());
    }

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

    public void takeDamage(int dmg) {
        health -= dmg;
    
        if (getWorld() != null) {
            getWorld().addObject(new BloodEffect(), getX(), getY());
        }
    
        if (health <= 0) die();
    }


    protected void die() {
        World world = getWorld();
        if (world != null) {
            world.removeObject(this);
        }
    }

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
    /** Rotates the image to face the target, but keeps its movement direction unchanged */
    private void faceTarget(Bloon target) {
        if (target == null) return;
    
        // Calculate angle to target
        double dx = target.getX() - getX();
        double dy = target.getY() - getY();
        int angle = (int) Math.toDegrees(Math.atan2(dy, dx));
    
        // Set rotation for aiming visuals only
        setRotation(angle);
    }

    private int despawnY = -1;
    public void setDespawnY(int y) { despawnY = y; }
    }
