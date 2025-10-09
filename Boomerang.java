import greenfoot.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Boomerang extends Projectile {
    private final int maxDistance = 500;
    private final int speed = 8;
    private final int basePierce = 5;

    private int traveled = 0;
    private int pierce;
    private boolean returning = false;
    private Monkey source;
    private Set<Bloon> hitBloons = new HashSet<>();

    private int fallbackX;
    private int fallbackY;

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

    @Override
    public void act() {
        if (getWorld() == null) return;

        if (!returning) {
            move(speed);
            traveled += speed;

            if (traveled >= maxDistance) {
                returning = true;
                hitBloons.clear(); // reset pierce for return phase
            }
        } else {
            // Update fallback in case monkey moved or died
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

        checkHits();
    }

    private void checkHits() {
        List<Bloon> bloons = getIntersectingObjects(Bloon.class);
        for (Bloon b : bloons) {
            if (!hitBloons.contains(b)) {
                b.takeDamage(1, DamageType.NORMAL);
                hitBloons.add(b);
                pierce--;
                if (pierce <= 0) {
                    if (getWorld() != null) getWorld().removeObject(this);
                    return;
                }
            }
        }
    }

    private double distanceTo(int x, int y) {
        return Math.hypot(getX() - x, getY() - y);
    }
}
