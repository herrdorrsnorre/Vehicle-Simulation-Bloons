import greenfoot.*;
import java.util.List;

/**
 * Represents a bomb projectile fired by a tower.
 * <p>
 * When the bomb collides with a bloon, it creates an explosion that
 * damages all nearby bloons within a specified radius. The explosion
 * also creates a visual and sound effect via {@link ExplosionEffect}.
 * </p>
 */
public class BombProjectile extends Projectile {
    /** The radius within which bloons are damaged by the explosion. */
    private int explosionRadius = 175;
    /**
     * Constructs a new {@code BombProjectile} originating from the given source tower
     * and targeting the specified bloon.
     *
     * @param source the tower (monkey) that fired this projectile
     * @param target the bloon this projectile is homing toward
     */
    public BombProjectile(Monkey source, Bloon target) {
        super(source, target);
        GreenfootImage bomb = new GreenfootImage("bomb.png");
        bomb.scale(56, 56);
        setImage(bomb);
        speed = 6;
    }
    /**
     * Checks for collision with any bloon. If a collision occurs, the projectile
     * detonates and triggers an explosion effect.
     */
    @Override
    protected void checkCollision() {
        if (worldRef == null) return;
        Bloon hit = (Bloon) getOneIntersectingObject(Bloon.class);
        if (hit != null) {
            explode();
        }
    }

    /**
     * Handles the explosion effect and area-of-effect (AOE) damage to bloons.
     * <p>
     * - Damages all bloons within the explosion radius that are not immune to {@code EXPLOSIVE} damage.<br>
     * - Spawns a corresponding {@link ExplosionEffect} at the impact location.<br>
     * - Removes the projectile from the world afterward.
     * </p>
     */
    private void explode() {
        if (getWorld() == null) return;
        List<Bloon> bloons = getObjectsInRange(explosionRadius, Bloon.class);
        for (Bloon b : bloons) {
            if (!b.isImmuneTo(DamageType.EXPLOSIVE)) {
                b.takeDamage(1, DamageType.EXPLOSIVE);
            }
        }
        getWorld().addObject(new ExplosionEffect(explosionRadius), getX(), getY());
        getWorld().removeObject(this);
    }
}
