import greenfoot.*;
import java.util.List;

public class BombProjectile extends Projectile {
    private int explosionRadius = 175;

    public BombProjectile(Monkey source, Bloon target) {
        super(source, target);
        GreenfootImage bomb = new GreenfootImage("bomb.png");
        bomb.scale(56, 56);
        setImage(bomb);
        speed = 6;
    }

    @Override
    protected void checkCollision() {
        if (worldRef == null) return;
        Bloon hit = (Bloon) getOneIntersectingObject(Bloon.class);
        if (hit != null) {
            explode();
        }
    }

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
