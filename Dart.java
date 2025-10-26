import greenfoot.*;

public class Dart extends Projectile {

    public Dart(Monkey source, Bloon target) {
        super(source, target);
        setImage("Dart.png");
    }

    @Override
    protected void checkCollision() {
        if (getWorld() == null) return;

        Bloon b = (Bloon) getOneIntersectingObject(Bloon.class);
        if (b != null) {
            b.takeDamage(1, DamageType.NORMAL);
            if (getWorld() != null) {
                getWorld().removeObject(this);  
            }
        }
    }
}
