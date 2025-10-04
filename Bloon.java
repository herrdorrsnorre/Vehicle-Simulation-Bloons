import greenfoot.*;

/**
 * Base Bloon class.
 */
public abstract class Bloon extends SuperSmoothMover {
    protected double speed;
    protected int health;
    protected Class<? extends Bloon> nextTier;
    protected int direction; // 1 = right, -1 = left
    protected int laneY;     // y-position of lane center
    protected int contactDamage = 1;

    public Bloon(double speed, int health, int direction, int laneY, Class<? extends Bloon> nextTier) {
        this.speed = speed;
        this.health = health;
        this.direction = direction;
        this.laneY = laneY;
        this.nextTier = nextTier;
        setRotation(direction == 1 ? 0 : 180);
    }

    @Override
    public void act() {
        move(speed * direction);
        checkCollisionWithMonkey();
        checkOutOfBounds();
    }

    /** Reduce health when hit */
    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0) {
            pop();
        }
    }

protected void pop() {
    World world = getWorld();
    if (world == null) return;

    int x = getX();
    int y = getY();

    // Spawn child tier if exists
    Class<? extends Bloon> child = getChildTier();
    if (child != null) {
        try {
            Bloon next = child
                .getConstructor(int.class, int.class)
                .newInstance(direction, laneY); // <-- correct order
            world.addObject(next, x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    PopEffect pop = new PopEffect();
    world.addObject(pop, x, y);
    world.removeObject(this);
}




    /** Child tier class for next tier (override if needed) */
    protected Class<? extends Bloon> getChildTier() {
        return null; // default: last tier
    }

    /** Deal damage to monkey on contact but don’t pop immediately */
    protected void checkCollisionWithMonkey() {
        Monkey monkey = (Monkey) getOneIntersectingObject(Monkey.class);
        if (monkey != null) {
            monkey.takeDamage(contactDamage);
            // DO NOT call pop() here — health controls that
        }
    }

    /** Remove if out of world bounds */
    protected void checkOutOfBounds() {
        if (getWorld() == null) return;
        if (getX() < 0 || getX() > getWorld().getWidth()) {
            getWorld().removeObject(this);
        }
    }
}
