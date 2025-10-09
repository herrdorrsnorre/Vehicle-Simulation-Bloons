import greenfoot.*;

public abstract class Projectile extends Actor {
    protected Monkey source;
    protected Bloon target;
    protected int speed = 8;
    protected World worldRef;
    private boolean hasHit = false; // track if we've hit a bloon

    public Projectile(Monkey source, Bloon target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void addedToWorld(World world) {
        worldRef = world;
        if (target != null && target.getWorld() != null) {
            turnTowards(target.getX(), target.getY());
        }
    }

    @Override
    public void act() {
        if (worldRef == null) return;

        // Only move if target still exists
        if (target != null && target.getWorld() != null) {
            turnTowards(target.getX(), target.getY());
        }

        move(speed);
        checkCollision();
    }

    protected void checkCollision() {
        if (worldRef == null || hasHit) return;

        Bloon b = (Bloon) getOneIntersectingObject(Bloon.class);
        if (b != null) {
            b.takeDamage(1, DamageType.NORMAL);
            hasHit = true; // mark that we've hit a bloon
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
        }
    }
}
