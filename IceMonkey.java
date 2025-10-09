import greenfoot.*;
import java.util.List;

public class IceMonkey extends Monkey {
    private int freezeRange = 200;   // area of effect
    private int cooldown = 120;       // min delay between attacks
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

    // Ensure IceMonkey is in the world
    World world = getWorld();
    if (world == null) return;

    // Cooldown timer
    if (timer > 0) {
        timer--;
        return;
    }

    // Check for any bloons in range
    List<Bloon> bloons = getObjectsInRange(freezeRange, Bloon.class);
    boolean hasTarget = false;
    for (Bloon b : bloons) {
        if (!b.isImmuneTo(DamageType.ICE)) {
            hasTarget = true;
            break;
        }
    }

    // Only attack if there is a target
    if (hasTarget) {
        freezeNearby();
        timer = cooldown;  // reset cooldown
    }
}



    // Check if any non-immune bloon is within freeze radius
    private boolean isBloonInRange() {
        List<Bloon> bloons = getObjectsInRange(freezeRange, Bloon.class);
        for (Bloon b : bloons) {
            if (!b.isImmuneTo(DamageType.ICE)) return true;
        }
        return false;
    }

 private void freezeNearby() {
    World world = getWorld();
    if (world == null) return;

    // Deal 1 damage to all in range
    List<Bloon> bloons = getObjectsInRange(freezeRange, Bloon.class);
    for (Bloon b : bloons) {
        if (!b.isImmuneTo(DamageType.ICE)) {
            b.takeDamage(1, DamageType.ICE);
        }
    }

    // Freeze remaining bloons, including children
    List<Bloon> remaining = getObjectsInRange(freezeRange, Bloon.class);
    for (Bloon b : remaining) {
        if (!b.isImmuneTo(DamageType.ICE)) {
            b.applyFreeze(150);
        }
    }

    world.addObject(new IceBlastEffect(), getX(), getY());
}

}
