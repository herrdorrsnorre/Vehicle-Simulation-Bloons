import greenfoot.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Boomerang extends Projectile {
    private int maxDistance = 500;
    private int traveled = 0;
    private boolean returning = false;
    private Monkey source;
    private Set<Bloon> hitBloons = new HashSet<>();
    private int speed = 8;
    private int pierce = 5;

    // Fallback position for when the source dies
    private int fallbackX;
    private int fallbackY;

    public Boomerang(Monkey source, Bloon target) {
        super(source, target);
        this.source = source;
        setImage("Boomerang.png");
        setRotation(0);

        if (source != null) {
            fallbackX = source.getX();
            fallbackY = source.getY();
        }
    }

    @Override
    public void act() {
        if (!returning) {
            move(speed);
            traveled += speed;
            if (traveled >= maxDistance) {
                returning = true;
                hitBloons.clear();
            }
        } else {
            // Update fallback position if source is still alive
            if (source != null && source.getWorld() != null) {
                fallbackX = source.getX();
                fallbackY = source.getY();
            }

            // Move toward fallback point
            turnTowards(fallbackX, fallbackY);
            move(speed);

            // Remove if close enough to fallback or out of bounds
            if (distanceTo(fallbackX, fallbackY) < 5 || isOutOfBounds()) {
                if (getWorld() != null) getWorld().removeObject(this);
                return;
            }
        }

        // Check for bloon collisions
        List<Bloon> bloons = getIntersectingObjects(Bloon.class);
        for (Bloon b : bloons) {
            if (!hitBloons.contains(b)) {
                b.takeDamage(1);
                hitBloons.add(b);
                pierce--;

                if (pierce <= 0 && getWorld() != null) {
                    getWorld().removeObject(this);
                    return;
                }

                break; // one hit per frame
            }
        }
    }

    private double distanceTo(int x, int y) {
        return Math.hypot(getX() - x, getY() - y);
    }

    private boolean isOutOfBounds() {
        World world = getWorld();
        if (world == null) return true;
        return getX() < 0 || getX() > world.getWidth() || getY() < 0 || getY() > world.getHeight();
    }
}
