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
    private int pierce = 5; // how many bloons it can hit before disappearing

    public Boomerang(Monkey source, Bloon target) {
        super(source, target);
        this.source = source;
        setImage("Boomerang.png");
        setRotation(0);
    }

    @Override
    public void act() {
        // Move forward or return
        if (!returning) {
            move(speed);
            traveled += speed;
            if (traveled >= maxDistance) {
                returning = true;
                hitBloons.clear();
            }
        } else {
            if (source.getWorld() != null) {
                turnTowards(source.getX(), source.getY());
                move(speed);

                if (distanceTo(source) < 5) {
                    getWorld().removeObject(this);
                    return;
                }
            }
        }

        // Check for bloons
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

                break; // only hit one bloon per act tick
            }
        }
    }

    private double distanceTo(Actor a) {
        return Math.hypot(getX() - a.getX(), getY() - a.getY());
    }
}
