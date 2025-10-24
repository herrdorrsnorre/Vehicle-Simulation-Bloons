import greenfoot.*;
import java.util.List;

public class IceMonkey extends Monkey {
    private int freezeRange = 200;   // area of effect
    private int cooldown = 120;      // delay between blasts
    private int timer = 0;

    public IceMonkey() {
        fireRate = cooldown;
        range = freezeRange;
        health = 6;
        speed = 2;
        projectileType = null; // no projectile

        GreenfootImage monkey = new GreenfootImage("Ice_Monkey.png");
        monkey.scale(67, 70);
        setImage(monkey);
    }

    @Override
    public void act() {
        super.act();

        World world = getWorld();
        if (world == null) return;

        // Handle cooldown
        if (timer > 0) {
            timer--;
            return;
        }

        // Check for any non-immune bloon nearby
        if (isBloonInRange()) {
            // Spawn the visual + functional freeze effect
            world.addObject(new IceBlastEffect(freezeRange), getX(), getY());
            timer = cooldown; // reset cooldown
        }
    }

    /** Checks if any non-immune bloon is within range. */
    private boolean isBloonInRange() {
        List<Bloon> bloons = getObjectsInRange(freezeRange, Bloon.class);
        for (Bloon b : bloons) {
            if (!b.isImmuneTo(DamageType.ICE)) return true;
        }
        return false;
    }
}
