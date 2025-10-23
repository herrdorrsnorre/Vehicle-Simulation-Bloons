import greenfoot.*;

public class Moab extends Bloon {
    private static final double SPEED = 1.0;
    private static final int MAX_HEALTH = 200;
    private static final int STAGE_COUNT = 5;
    private static final GreenfootImage[] DAMAGE_IMAGES = new GreenfootImage[STAGE_COUNT];
    private int lastDamageStage = -1;

    static {
        for (int i = 0; i < STAGE_COUNT; i++) {
            DAMAGE_IMAGES[i] = new GreenfootImage("moab/moab" + i + ".png");
            DAMAGE_IMAGES[i].scale(206, 135);
        }
    }

    public Moab(int direction, int laneY) {
        super(SPEED, MAX_HEALTH, direction, laneY, null);
        setImage(DAMAGE_IMAGES[0]);
        contactDamage = 100;
    }

    @Override
    public void takeDamage(int dmg, DamageType type) {
        health -= dmg;
        GreenfootSound Ceramic = new GreenfootSound("moabdamage.wav");
        Ceramic.setVolume(75);
        Ceramic.play();
        if (health <= 0) {
            pop();
            return;
        }

        int stage = Math.min(STAGE_COUNT - 1, (MAX_HEALTH - health) / 40);
        if (stage != lastDamageStage) {
            setImage(DAMAGE_IMAGES[stage]);
            lastDamageStage = stage;
        }
    }

    @Override
    protected void pop() {
        World world = getWorld();
        if (world == null) return;

        int x = getX();
        int y = getY();

        int offset = 18;
        world.addObject(new CeramicBloon(direction, laneY), x - offset * 2, y);
        world.addObject(new CeramicBloon(direction, laneY), x - offset, y);
        world.addObject(new CeramicBloon(direction, laneY), x, y);
        world.addObject(new CeramicBloon(direction, laneY), x + offset, y);
        GreenfootSound Ceramic = new GreenfootSound("moabpop.wav");
        Ceramic.setVolume(75);
        Ceramic.play();
        world.addObject(new PopEffect(), x, y);
        world.removeObject(this);
    }
}
