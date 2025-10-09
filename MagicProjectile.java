import greenfoot.*;
import java.util.List;

public class MagicProjectile extends Projectile {
    private int pierce = 12; // how many bloons it can hit before disappearing
    private double turnRate = 6; // degrees per frame for homing
    private Bloon currentTarget;

    public MagicProjectile(Monkey source, Bloon target) {
        super(source, target);
        GreenfootImage magic = new GreenfootImage("magic.png");
        magic.scale(30, 30);
        setImage(magic);
        speed = 7;
        currentTarget = target;
    }

    @Override
    public void act() {
        // ensure the world exists before doing anything
        if (getWorld() == null) return;

        // Acquire or maintain target
        if (currentTarget == null || currentTarget.getWorld() == null) {
            currentTarget = getNearestBloon();
        }

        // Homing rotation
        if (currentTarget != null && currentTarget.getWorld() != null) {
            turnTowards(currentTarget.getX(), currentTarget.getY());
        }

        // Move forward
        move(speed);

        // Handle collisions and cleanup
        checkCollision();
    }

    private Bloon getNearestBloon() {
        World world = getWorld();
        if (world == null) return null; // safeguard

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

    private double getDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    protected void checkCollision() {
        World world = getWorld();
        if (world == null) return;

        List<Bloon> bloons = getIntersectingObjects(Bloon.class);
        if (bloons == null || bloons.isEmpty()) return;

        for (Bloon b : bloons) {
    if (b == null) continue;

    if (b.isImmuneTo(DamageType.MAGIC)) {
        // Hit an immune bloon → play sound and destroy projectile
        GreenfootSound immuneSound = new GreenfootSound("PurpleSound.mp3");
        immuneSound.setVolume(40);
        immuneSound.play();

        if (getWorld() != null) {
            world.removeObject(this);
            return;
        }
    } else {
        // Normal damage
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
