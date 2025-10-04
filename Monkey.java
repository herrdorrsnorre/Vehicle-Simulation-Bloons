import greenfoot.*;
import java.util.List;

public abstract class Monkey extends SuperSmoothMover {
    protected int health = 5;
    protected int range = 150;
    protected int fireRate = 100; // frames between shots
    protected int fireTimer = 0;
    protected Class<? extends Projectile> projectileType;

    // Pedestrian vertical movement
    protected int speed = 2;      // vertical speed
    protected int direction = 1;  // 1 = down, -1 = up

    public Monkey() {
        setRotation(0);
    }

    @Override
    public void act() {
        fireTimer++;
        if (fireTimer >= fireRate) {
            Bloon target = getNearestBloon();
            if (target != null) {
                fireAt(target);
                fireTimer = 0;
            }
        }

        walkAcrossStreet();
        checkDeath();
        /*Bloon target = getNearestBloon();
if (target != null) {
    turnTowards(target.getX(), target.getY());
}
*/
    }

    private void walkAcrossStreet() {
    if (getWorld() == null) return;

    // Move horizontally in the direction of rotation
    move(speed); // move() moves in the direction of setRotation()

    // Remove monkey if it leaves the world horizontally
    if (getX() < -getImage().getWidth()/2 || getX() > getWorld().getWidth() + getImage().getWidth()/2) {
        getWorld().removeObject(this);
    }
}

    /** Locate nearest Bloon within range */
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
}
