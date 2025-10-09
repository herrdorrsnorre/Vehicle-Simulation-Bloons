import greenfoot.*;

public class Dart extends Projectile {

    public Dart(Monkey source, Bloon target) {
        super(source, target);
        setImage("Dart.png");
    }

    @Override
    protected void checkCollision() {
        if (getWorld() == null) return;

        // Only hit one bloon and then remove
        Bloon b = (Bloon) getOneIntersectingObject(Bloon.class);
        if (b != null) {
            b.takeDamage(1, DamageType.NORMAL);
             // kill that bloon or reduce health
            if (getWorld() != null) {
                getWorld().removeObject(this);  // disappear immediately
            }
        }
    }
}
