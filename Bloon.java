import greenfoot.*;

/**
 * Base abstract class representing a Bloon (enemy) in the game.
 * <p>
 * A {@code Bloon} moves horizontally along a lane, can take and respond to various
 * types of {@link DamageType} damage, and may spawn child bloons when popped.
 * Bloons can freeze, change lanes to avoid slower traffic, and deal contact
 * damage to monkeys.
 * </p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Supports multiple damage immunities and temporary freeze effects.</li>
 *   <li>Handles lane-following and lane-changing traffic logic.</li>
 *   <li>Spawns child bloons when popped.</li>
 *   <li>Can temporarily harm monkeys on contact with cooldowns.</li>
 * </ul>
 */
public abstract class Bloon extends SuperSmoothMover {
    /** Movement speed in pixels per frame. */
    protected double speed;
    /** Current health of the bloon. */
    protected int health;
    /** The next-tier bloon class to spawn when this one pops. */
    protected Class<? extends Bloon> nextTier;
    /** Direction of travel: {@code 1 = right}, {@code -1 = left}. */
    protected int direction;
    /** Vertical position representing the lane's center Y-coordinate. */
    protected int laneY;
    /** Amount of damage dealt to a monkey on contact. */
    protected int contactDamage = 1;
    /** Cooldown (in frames) before this bloon can damage a monkey again. */
    private int contactCooldown = 0; 
    /** Whether the bloon is currently frozen. */
    protected boolean frozen = false;
    /** Time remaining (in frames) before a frozen bloon thaws. */
    protected int freezeTimer = 0;
    /** The original (unfrozen) image of the bloon for later restoration. */
    protected GreenfootImage originalImage;
    /** Temporary immunities (e.g., during freeze effects). */
    private java.util.EnumMap<DamageType, Boolean> tempImmunities = new java.util.EnumMap<>(DamageType.class);
    /** True if the bloon is currently performing a lane change. */
    protected boolean changingLane = false;
    /** The Y-coordinate of the lane this bloon is moving toward. */
    protected int targetLaneY;
    /** Speed of lane changes in pixels per frame. */
    protected double laneChangeSpeed = 2.0; 
    
    /**
     * Constructs a new {@code Bloon} instance.
     *
     * @param speed     movement speed in pixels per frame
     * @param health    starting health of the bloon
     * @param direction travel direction ({@code 1 = right}, {@code -1 = left})
     * @param laneY     vertical lane position
     * @param nextTier  the next-tier bloon class to spawn upon popping
     */
    public Bloon(double speed, int health, int direction, int laneY, Class<? extends Bloon> nextTier) {
        this.speed = speed;
        this.health = health;
        this.direction = direction;
        this.laneY = laneY;
        this.nextTier = nextTier;
        if (direction == -1) {
            GreenfootImage img = getImage();
            if (img != null) {
                img.mirrorHorizontally(); 
                setImage(img);
            }
        }
    
    }
    

    /**
     * Main update method — called once per frame.
     * Handles freezing, movement, lane logic, collisions, and cleanup.
     */
    @Override
    public void act() {
        if (frozen) {
            freezeTimer--;
            if (freezeTimer <= 0) {
                frozen = false;
                setTemporaryImmunity(DamageType.NORMAL, false);
                setImage(new GreenfootImage(originalImage)); 
                updateImageDirection();
            } else {
                return; 
            }
        }
    
        handleTrafficLogic();
        move(speed * direction);
        checkCollisionWithMonkey();
        checkOutOfBounds();
    }
    /**
     * Applies damage to this bloon based on the damage type.
     *
     * @param dmg  the amount of damage
     * @param type the {@link DamageType} applied
     */
    public void takeDamage(int dmg, DamageType type) {
        if (isImmuneTo(type)) {
            return;
        }
    
        Boolean tempImmune = tempImmunities.get(type);
        if (tempImmune != null && tempImmune) return;
    
        health -= dmg;
        if (health <= 0) pop();
    }
    
    /**
     * Handles what happens when a bloon is destroyed (popped).
     * <p>
     * - Spawns its child tier (if any).<br>
     * - Transfers freeze effects if applicable.<br>
     * - Plays pop visual effect and removes this object.
     * </p>
     */
    protected void pop() {
        World world = getWorld();
        if (world == null) return;
    
        int x = getX();
        int y = getY();
    
        boolean wasFrozen = frozen;
        int remainingFreeze = freezeTimer;
    
        Class<? extends Bloon> child = getChildTier();
        if (child != null) {
            try {
                Bloon next = child
                    .getConstructor(int.class, int.class)
                    .newInstance(direction, laneY);
                world.addObject(next, x, y);
    
                if (wasFrozen && remainingFreeze > 0) {
                    next.applyFreeze(remainingFreeze);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        PopEffect pop = new PopEffect();
        world.addObject(pop, x, y);
        world.removeObject(this);
    }
    
    /**
     * Returns the next-tier bloon type that should spawn when this one pops.
     * Subclasses override this to define their child type.
     *
     * @return the class of the next-tier bloon, or {@code null} if none
     */
    protected Class<? extends Bloon> getChildTier() {
        return null; 
    }
    /**
     * Checks and applies contact damage to monkeys.
     */
    protected void checkCollisionWithMonkey() {
        Monkey monkey = (Monkey) getOneIntersectingObject(Monkey.class);
        if (monkey != null && contactCooldown == 0) {
            monkey.takeDamage(contactDamage);
            contactCooldown = 30; // 0.5 seconds if game runs at 60 fps
        }
    }
    
    /**
     * Removes the bloon if it travels out of world bounds.
     */
    protected void checkOutOfBounds() {
        if (getWorld() == null) return;
        if (getX() < 0 || getX() > getWorld().getWidth()) {
            getWorld().removeObject(this);
        }
    }
    
    /**
     * Checks whether this bloon is immune to a given damage type.
     *
     * @param type the damage type to test
     * @return {@code true} if this bloon is immune; {@code false} otherwise
     */
    public boolean isImmuneTo(DamageType type) {
        if (this instanceof LeadBloon && (type != DamageType.EXPLOSIVE)) return true;
        if (this instanceof WhiteBloon && type == DamageType.ICE) return true;
        if (this instanceof ZebraBloon && type == DamageType.ICE) return true;
        if (this instanceof BlackBloon && type == DamageType.EXPLOSIVE) return true;
        if (this instanceof ZebraBloon && type == DamageType.EXPLOSIVE) return true;
        if (this instanceof PurpleBloon && type == DamageType.MAGIC) return true;
        return false;
    }
    /**
     * Freezes this bloon for a given duration.
     *
     * @param duration freeze duration in frames
     */
    public void applyFreeze(int duration) {
        if (isImmuneTo(DamageType.ICE) || frozen) return;
    
        frozen = true;
        freezeTimer = duration;
        setTemporaryImmunity(DamageType.NORMAL, true);
    
        // store the non-frozen base image for later restoration
        originalImage = new GreenfootImage(getImage());
    
        // create and show the frozen visual
        GreenfootImage frozenImg = makeFrozenCopy(originalImage);
        setImage(frozenImg);
    }

    /**
     * Restores the correct image orientation after thawing or direction change.
     */
    protected void updateImageDirection() {
        if (originalImage == null) return;
        GreenfootImage img = new GreenfootImage(originalImage);
        if (direction == -1) { // facing left
            img.mirrorHorizontally();
        }
        setImage(img);
    }

    /**
     * Sets the bloon's travel direction and updates its image accordingly.
     *
     * @param newDirection the new direction ({@code 1 = right}, {@code -1 = left})
     */    
    public void setDirection(int newDirection) {
        direction = newDirection;
        updateImageDirection();
    }
    
    /**
     * Handles lane-following and lane-changing behavior when blocked.
     */
    protected void handleTrafficLogic() {
        if (getWorld() == null) return;
    
        int lookAhead = 100; 
        Bloon frontBloon = getBloonAhead(lookAhead);
    
        if (frontBloon != null && frontBloon != this) {
            if (frontBloon instanceof Moab) {
                this.speed = Math.max(frontBloon.speed, this.speed * 0.97);
    
                if (!changingLane) tryLaneChange();
            } 
        } else {
            this.speed = Math.abs(speed);
        }
    
        if (changingLane) {
            int dy = targetLaneY - getY();
            if (Math.abs(dy) < 2) {
                changingLane = false; 
            } else {
                setLocation(getX(), getY() + (int) Math.signum(dy) * laneChangeSpeed);
            }
        }
    }
    /**
     * Returns the bloon directly ahead within a given look-ahead distance.
     *
     * @param distance distance ahead to check
     * @return the bloon detected ahead, or {@code null} if none found
     */   
    private Bloon getBloonAhead(int distance) {
        return (Bloon) getOneObjectAtOffset(direction * distance, 0, Bloon.class);
    }
    /**
     * Attempts to change lanes to avoid slower traffic.
     */    
    protected void tryLaneChange() {
        BloonWorld world = (BloonWorld) getWorld();
        int[] lanes = world.getLanePositions(); 
        int currentY = laneY;
    
        int currentLane = -1;
        for (int i = 0; i < lanes.length; i++) {
            if (Math.abs(lanes[i] - currentY) < 5) {
                currentLane = i;
                break;
            }
        }
    
        if (currentLane == -1) return; 
    
        int[] offsets = {-2, -1, 1, 2};
        for (int offset : offsets) {
            int newLane = currentLane + offset;
            if (newLane < 0 || newLane >= lanes.length) continue;
    
            boolean sameDirection = (newLane < 3 && direction == -1) || (newLane >= 3 && direction == 1);
            if (!sameDirection) continue;
    
            java.util.List<Bloon> laneBloons = world.getObjectsAt(getX(), lanes[newLane], Bloon.class);
            if (laneBloons.isEmpty()) {
                targetLaneY = lanes[newLane];
                changingLane = true;
                laneY = targetLaneY; 
                return;
            }
        }
    }

    /**
     * Creates an icy-blue copy of a given image to visually represent a frozen bloon.
     *
     * @param base the base image to modify
     * @return a new frozen-tinted {@link GreenfootImage}
     */
    protected GreenfootImage makeFrozenCopy(GreenfootImage base) {
        if (base == null) return null;
        GreenfootImage frozenImg = new GreenfootImage(base);
        Color freezeColor = new Color(100, 180, 255, 80);     
        for (int x = 0; x < frozenImg.getWidth(); x++) {
            for (int y = 0; y < frozenImg.getHeight(); y++) {
                Color pixel = frozenImg.getColorAt(x, y);
                if (pixel.getAlpha() > 0) {
                    int r = (pixel.getRed() + freezeColor.getRed()) / 2;
                    int g = (pixel.getGreen() + freezeColor.getGreen()) / 2;
                    int b = (pixel.getBlue() + freezeColor.getBlue()) / 2;
                    int a = pixel.getAlpha();
                    frozenImg.setColorAt(x, y, new Color(r, g, b, a));
                }
            }
        }
        return frozenImg;
    }

    /**
     * Assigns or removes a temporary immunity to this bloon.
     *
     * @param type   the damage type to modify
     * @param active {@code true} to enable immunity, {@code false} to disable it
     */
    public void setTemporaryImmunity(DamageType type, boolean active) {
        tempImmunities.put(type, active);
    }
}
