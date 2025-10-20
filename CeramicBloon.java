import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class CeramicBloon extends Bloon {
    private static final double SPEED = 2;
    private static final GreenfootImage[] DAMAGE_IMAGES = new GreenfootImage[5];
    private int lastDamageStage = 0;

    static {
        // Load and scale images once
        for (int i = 0; i < 5; i++) {
            DAMAGE_IMAGES[i] = new GreenfootImage("Ceramic/Ceramic_Bloon" + i + ".png");
            DAMAGE_IMAGES[i].scale(63, 84);
        }
    }

    public CeramicBloon(int direction, int laneY) {
        super(SPEED, 10, direction, laneY, PinkBloon.class);
        setImage(DAMAGE_IMAGES[0]);
        contactDamage = 5;
    }

    @Override
    protected Class<? extends Bloon> getChildTier() {
        return RainbowBloon.class;
    }

    @Override
    public void takeDamage(int dmg, DamageType type) {
        
        health -= dmg;
        GreenfootSound Ceramic = new GreenfootSound("CeramicSound.wav");
        Ceramic.setVolume(30);
        Ceramic.play();
        if (health <= 0) {
            pop();
            return;
        }
        
        // Determine damage stage (every 2 HP)
        int stage = Math.min(4, (10 - health) / 2);
        if (stage != lastDamageStage) {
            setImage(DAMAGE_IMAGES[stage]);
            lastDamageStage = stage;
        }
    }
    @Override
    protected void pop() 
    {
        World world = getWorld();
        if (world == null) return;

        int x = getX();
        int y = getY();

        // Spawn Black and White Bloons slightly offset horizontally
        RainbowBloon rainbow = new RainbowBloon(direction, laneY);
        RainbowBloon rainbow2 = new RainbowBloon(direction, laneY);

        int offset = 12;
        world.addObject(rainbow, x - offset, y);
        world.addObject(rainbow2, x + offset, y);
        PopEffect pop = new PopEffect();
        world.removeObject(this);
    }
}
