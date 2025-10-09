import greenfoot.*;

/**
 * Base Bloon class.
 */
public abstract class Bloon extends SuperSmoothMover {
    protected double speed;
    protected int health;
    protected Class<? extends Bloon> nextTier;
    protected int direction; // 1 = right, -1 = left
    protected int laneY;     // y-position of lane center
    protected int contactDamage = 1;
    private int contactCooldown = 0; // frames until this bloon can hurt a monkey again
    private boolean frozen = false;
    private int freezeTimer = 0;
    private GreenfootImage originalImage;
    // temporary immunity tracker
private java.util.EnumMap<DamageType, Boolean> tempImmunities = new java.util.EnumMap<>(DamageType.class);

public void setTemporaryImmunity(DamageType type, boolean active) {
    tempImmunities.put(type, active);
}

    public Bloon(double speed, int health, int direction, int laneY, Class<? extends Bloon> nextTier) {
        this.speed = speed;
        this.health = health;
        this.direction = direction;
        this.laneY = laneY;
        this.nextTier = nextTier;
   // Do not rotate bloons — just flip if moving left
if (direction == -1) {
    GreenfootImage img = getImage();
    if (img != null) {
        img.mirrorHorizontally(); // ✅ horizontal flip, not vertical
        setImage(img);
    }
}

    }

    @Override
    public void act() {
if (frozen) {
    freezeTimer--;
    if (freezeTimer <= 0) {
        frozen = false;
        setTemporaryImmunity(DamageType.NORMAL, false); // remove lead-like immunity
        setImage(new GreenfootImage(originalImage)); // restore normal sprite
    } else {
        return; // stay frozen
    }
}

        move(speed * direction);
        checkCollisionWithMonkey();
        checkOutOfBounds();
    }

    public void takeDamage(int dmg, DamageType type) {
    // Check permanent immunity first
    if (isImmuneTo(type)) {
        // Play a sound for hitting an immune bloon
        return;
    }

    // Check temporary immunity
    Boolean tempImmune = tempImmunities.get(type);
    if (tempImmune != null && tempImmune) return;

    health -= dmg;
    if (health <= 0) pop();
}


protected void pop() {
    World world = getWorld();
    if (world == null) return;

    int x = getX();
    int y = getY();

    // Track whether this bloon was frozen before popping
    boolean wasFrozen = frozen;
    int remainingFreeze = freezeTimer;

    // Spawn child tier if exists
    Class<? extends Bloon> child = getChildTier();
    if (child != null) {
        try {
            Bloon next = child
                .getConstructor(int.class, int.class)
                .newInstance(direction, laneY);
            world.addObject(next, x, y);

            // ✅ If the parent was frozen, carry over the freeze to the child
            if (wasFrozen && remainingFreeze > 0) {
                next.applyFreeze(remainingFreeze);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Pop visual and remove parent
    PopEffect pop = new PopEffect();
    world.addObject(pop, x, y);
    world.removeObject(this);
}




    /** Child tier class for next tier (override if needed) */
    protected Class<? extends Bloon> getChildTier() {
        return null; // default: last tier
    }

   protected void checkCollisionWithMonkey() {
        Monkey monkey = (Monkey) getOneIntersectingObject(Monkey.class);
        if (monkey != null && contactCooldown == 0) {
            monkey.takeDamage(contactDamage);
            contactCooldown = 30; // 0.5 seconds if game runs at 60 fps
        }
    }

    /** Remove if out of world bounds */
    protected void checkOutOfBounds() {
        if (getWorld() == null) return;
        if (getX() < 0 || getX() > getWorld().getWidth()) {
            getWorld().removeObject(this);
        }
    }


    public boolean isImmuneTo(DamageType type) {
        if (this instanceof LeadBloon && (type != DamageType.EXPLOSIVE)) return true;
        if (this instanceof WhiteBloon && type == DamageType.ICE) return true;
        if (this instanceof ZebraBloon && type == DamageType.ICE) return true;
        if (this instanceof BlackBloon && type == DamageType.EXPLOSIVE) return true;
        if (this instanceof ZebraBloon && type == DamageType.EXPLOSIVE) return true;
        if (this instanceof PurpleBloon && type == DamageType.MAGIC) return true;
        return false;
    }
public void applyFreeze(int duration) {
    if (isImmuneTo(DamageType.ICE) || frozen) return;

    frozen = true;
    freezeTimer = duration;

    // temporarily acts like a lead (immune to normal)
    setTemporaryImmunity(DamageType.NORMAL, true);

    if (originalImage == null)
        originalImage = new GreenfootImage(getImage());

    GreenfootImage frozenImg = new GreenfootImage(originalImage);

    Color freezeColor = new Color(100, 180, 255, 80); // semi-transparent icy blue

    for (int x = 0; x < frozenImg.getWidth(); x++) {
        for (int y = 0; y < frozenImg.getHeight(); y++) {
            Color pixel = frozenImg.getColorAt(x, y);
            if (pixel.getAlpha() > 0) {
                // blend the freeze color on top of the original
                int r = (pixel.getRed() + freezeColor.getRed()) / 2;
                int g = (pixel.getGreen() + freezeColor.getGreen()) / 2;
                int b = (pixel.getBlue() + freezeColor.getBlue()) / 2;
                int a = pixel.getAlpha(); // keep original alpha
                frozenImg.setColorAt(x, y, new Color(r, g, b, a));
            }
        }
    }

    setImage(frozenImg);
}




}
