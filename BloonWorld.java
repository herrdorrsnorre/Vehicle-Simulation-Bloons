import greenfoot.*;
import java.util.ArrayList;
import java.util.Collections;
/**
 * The {@code BloonWorld} class represents the main simulation environment for the Bloons-style game.
 * <p>
 * It manages:
 * <ul>
 *   <li>Bloon spawning logic (normal and MOAB)</li>
 *   <li>Monkey spawning logic and timing</li>
 *   <li>Lane preparation and visual rendering</li>
 *   <li>Development testing tools (dev mode)</li>
 *   <li>Random global effects (e.g. Ice Blast)</li>
 *   <li>Z-sorting of actors for proper depth rendering</li>
 * </ul>
 *
 * This class extends {@link World} and serves as the core game world where all
 * gameplay and simulation activity occurs.
 */

public class BloonWorld extends World {
    /** Base background image for the world. */
    private GreenfootImage background;
    // --- Color definitions for map elements ---
    public static Color GREY_BORDER = new Color(108, 108, 108);
    public static Color GREY_PATH = new Color(120, 120, 120);
    public static Color SIDEWALK_COLOR = new Color(160, 160, 160);
    // --- Lane configuration ---
    private int laneHeight = 64;
    private int laneCount = 6;
    private int spaceBetweenLanes = 6;
    private int[] lanePositionsY;
    private BloonSpawner[] laneSpawners;
    public static boolean SHOW_SPAWNERS = false;
    // --- Spawn timing variables ---
    private int spawnTimer = 0;
    private int simulationTime = 0;
    private static final int BASE_MONKEY_INTERVAL = 80;
    private static final int BASE_BLOON_INTERVAL = 240;
    private int[] laneSpawnTimers = new int[laneCount];
    private int monkeySpawnTimer = 0;
    private int bloonSpawnTimer = 0;
    // --- Sidewalk boundaries ---
    private int sidewalkTopStart;
    private int sidewalkTopEnd;
    private int sidewalkBottomStart;
    private int sidewalkBottomEnd;

    // --- Developer testing features ---
    private boolean devMode = false; 
    private Class<? extends Bloon> devBloon1;
    private Class<? extends Bloon> devBloon2;
    private Class<? extends Monkey> devMonkey; 
    // --- Random event timers ---    
    private int iceBlastTimer = 0;
    private boolean iceBlastActive = false;
    /**
     * Constructs the {@code BloonWorld}, initializes the background,
     * prepares lane and sidewalk graphics, and sets up spawners.
     */
    public BloonWorld() {
        super(1024, 800, 1, false);

        GreenfootImage base = new GreenfootImage("background01.png");
        GreenfootImage overlay = new GreenfootImage(base.getWidth(), base.getHeight());
        overlay.setTransparency(255);

        laneSpawners = new BloonSpawner[laneCount];
        lanePositionsY = prepareLanes(this, overlay, laneSpawners, 232, laneHeight, laneCount, spaceBetweenLanes);

        base.drawImage(overlay, 0, 0);
        background = base;
        setBackground(background);

        sidewalkTopStart = 232 - 80;
        sidewalkTopEnd = 232 - 10;
        sidewalkBottomStart = lanePositionsY[laneCount - 1] + (laneHeight / 2) + 10;
        sidewalkBottomEnd = sidewalkBottomStart + 80;
        //enableDevMode(CeramicBloon.class, PinkBloon.class, IceMonkey.class);
    }
    /**
     * Enables developer mode, which spawns custom bloons and monkeys for testing.
     *
     * @param bloonType1 first bloon class to spawn
     * @param bloonType2 second bloon class to spawn
     * @param monkeyType monkey class to spawn
     */    
    public void enableDevMode(Class<? extends Bloon> bloonType1, Class<? extends Bloon> bloonType2, Class<? extends Monkey> monkeyType) {
        devMode = true;
        devBloon1 = bloonType1;
        devBloon2 = bloonType2;
        devMonkey = monkeyType;
    }
    
    /**
     * Main update loop executed once per frame.
     * Handles spawning, random events, and sorting.
     */
    public void act() {
        simulationTime++;
        spawnBloons();
        spawnMonkeys();
        triggerRandomIceBlast();
        zSort((ArrayList<Actor>) getObjects(Actor.class), this);
    }
    /**
     * Handles all bloon spawning logic, including:
     * <ul>
     *   <li>Developer test bloon spawning</li>
     *   <li>Normal bloon tier progression</li>
     *   <li>MOAB spawn frequency scaling</li>
     * </ul>
     */
    private void spawnBloons() {
        //--- Dev Mode ---
        if (devMode && (devBloon1 != null || devBloon2 != null)) {
            bloonSpawnTimer++;
            if (bloonSpawnTimer < BASE_BLOON_INTERVAL) return;
            bloonSpawnTimer = 0;
    
            for (int lane = 0; lane < laneCount; lane++) {
                BloonSpawner spawner = laneSpawners[lane];
                int direction = (lane < 3) ? -1 : 1;
                int startX = (direction == 1) ? 1 : getWidth() - 1;
    
                try {
                    Class<? extends Bloon> bloonClass =
                        (Greenfoot.getRandomNumber(2) == 0 ? devBloon1 : devBloon2);
    
                    if (bloonClass != null) {
                        Bloon b = bloonClass
                            .getConstructor(int.class, int.class)
                            .newInstance(direction, spawner.getY());
                        addObject(b, startX, spawner.getY());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
    
        // --- Normal Spawns ---
        simulationTime++;
    
        // --- Moab Spawns ---
        int currentMoabs = getObjects(Moab.class).size();
    
        int maxMoabs = 1 + (simulationTime / 8000); 
        if (maxMoabs > 5) maxMoabs = 5; 
    
        int baseChance = 1000;
        int moabChance = Math.max(200, baseChance - (simulationTime / 2000) * 100);
    
        boolean canSpawnMoab = currentMoabs < maxMoabs;
        boolean shouldTrySpawnMoab = simulationTime > 6000 && Greenfoot.getRandomNumber(moabChance) == 0;
    
        if (canSpawnMoab && shouldTrySpawnMoab) {
            int lane = Greenfoot.getRandomNumber(laneCount);
            BloonSpawner spawner = laneSpawners[lane];
            int direction = (lane < 3) ? -1 : 1;
            int startX = (direction == 1) ? 1 : getWidth() - 1;
    
            addObject(new Moab(direction, spawner.getY()), startX, spawner.getY());
            return; 
        }
    
        // --- Small Bloon Spawns ---
        int bloonSpawnChance = 2 - Math.min(currentMoabs, 1); 
        bloonSpawnChance = Math.max(1, bloonSpawnChance);
    
        for (int lane = 0; lane < laneCount; lane++) {
            BloonSpawner spawner = laneSpawners[lane];
            laneSpawnTimers[lane]++;
    
            if (Greenfoot.getRandomNumber(100) < bloonSpawnChance) {
                if (laneSpawnTimers[lane] >= 30 && !spawner.isTouchingBloon()) {
                    laneSpawnTimers[lane] = 0;
    
                    // --- Determine allowed tiers ---
                    ArrayList<Integer> allowedTiers = new ArrayList<>();
                    if (simulationTime < 600) { for (int i = 0; i <= 0; i++) allowedTiers.add(i); }
                    else if (simulationTime < 1100) { for (int i = 0; i <= 1; i++) allowedTiers.add(i); }
                    else if (simulationTime < 1700) { for (int i = 0; i <= 2; i++) allowedTiers.add(i); }
                    else if (simulationTime < 2300) { for (int i = 0; i <= 3; i++) allowedTiers.add(i); }
                    else if (simulationTime < 2900) { for (int i = 0; i <= 4; i++) allowedTiers.add(i); }
                    else if (simulationTime < 4100) { for (int i = 0; i <= 6; i++) allowedTiers.add(i); }
                    else if (simulationTime < 5400) { for (int i = 0; i <= 8; i++) allowedTiers.add(i); }
                    else if (simulationTime < 6000) { for (int i = 0; i <= 10; i++) allowedTiers.add(i); }
                    else if (simulationTime < 6400) { for (int i = 0; i <= 11; i++) allowedTiers.add(i); }
                    else { for (int i = 0; i <= 12; i++) allowedTiers.add(i); }
                    
                        
                    int bloonType = allowedTiers.get(Greenfoot.getRandomNumber(allowedTiers.size()));
    
                    int direction = (lane < 3) ? -1 : 1;
                    int startX = (direction == 1) ? 1 : getWidth() - 1;
    
                    Bloon b;
                    switch (bloonType) {
                        case 0: b = new RedBloon(direction, spawner.getY()); break;
                        case 1: b = new BlueBloon(direction, spawner.getY()); break;
                        case 2: b = new GreenBloon(direction, spawner.getY()); break;
                        case 3: b = new YellowBloon(direction, spawner.getY()); break;
                        case 4: b = new PinkBloon(direction, spawner.getY()); break;
                        case 5: b = new BlackBloon(direction, spawner.getY()); break;
                        case 6: b = new WhiteBloon(direction, spawner.getY()); break;
                        case 7: b = new PurpleBloon(direction, spawner.getY()); break;
                        case 8: b = new LeadBloon(direction, spawner.getY()); break;
                        case 9: b = new ZebraBloon(direction, spawner.getY()); break;
                        case 10: b = new RainbowBloon(direction, spawner.getY()); break;
                        case 11: b = new CeramicBloon(direction, spawner.getY()); break;
                        default: b = new RedBloon(direction, spawner.getY()); break;
                    }
    
                    addObject(b, startX, spawner.getY());
                }
            }
        }
    }


    /**
     * Handles automatic spawning of monkeys at dynamic intervals.
     * The spawn rate and type of monkey depend on the current simulation phase.
     */    
    private void spawnMonkeys() {
        monkeySpawnTimer++;

        if (devMode && devMonkey != null) {
            if (monkeySpawnTimer < BASE_MONKEY_INTERVAL) return;
            monkeySpawnTimer = 0;
            try {
                Monkey m = devMonkey.getConstructor().newInstance();
                addMonkey(m);
            } catch (Exception e) { e.printStackTrace(); }
            return;
        }

        int dynamicInterval = (int)(BASE_MONKEY_INTERVAL * Math.max(0.5, 1.0 - simulationTime / 4800.0));
        if (monkeySpawnTimer < dynamicInterval) return;
        monkeySpawnTimer = 0;

        int phase = 1;
        if (simulationTime >= 2900 && simulationTime < 6000) phase = 2;
        if (simulationTime >= 6000) phase = 3;

        int monkeyType = -1;
        int roll = Greenfoot.getRandomNumber(100);

        switch (phase) {
            case 1:
                if (roll < 50) monkeyType = 0;
                else if (roll < 75) monkeyType = 1;
                break;
            case 2:
                if (roll < 20) monkeyType = 0;
                else if (roll < 35) monkeyType = 1;
                else if (roll < 45) monkeyType = 5;
                else if (roll < 60) monkeyType = 3;
                else if (roll < 70) monkeyType = 4;
                break;
            case 3:
                if (roll < 15) monkeyType = 0;
                else if (roll < 25) monkeyType = 1;
                else if (roll < 35) monkeyType = 2;
                else if (roll < 50) monkeyType = 3;
                else if (roll < 65) monkeyType = 4;
                else if (roll < 75) monkeyType = 5;
                break;
        }

        if (monkeyType == -1) return;

        Monkey m;
        switch (monkeyType) {
            case 0: m = new DartMonkey(); break;
            case 1: m = new BoomerangMonkey(); break;
            case 2: m = new SuperMonkey(); break;
            case 3: m = new WizardMonkey(); break;
            case 4: m = new BombTower(); break;
            case 5: m = new IceMonkey(); break;
            default: return;
        }
        addMonkey(m);
    }
    /**
     * Adds a monkey actor to the world at a random sidewalk spawn position.
     * Monkeys traverse vertically across the map and despawn at the opposite sidewalk.
     *
     * @param m the monkey to add
     */
    private void addMonkey(Monkey m) {
        boolean spawnAtTop = Greenfoot.getRandomNumber(2) == 0;

        int ySpawn, yDespawn;
        int sidewalkDepth = 60;

        if (spawnAtTop) {
            ySpawn = sidewalkTopEnd - sidewalkDepth;
            yDespawn = sidewalkBottomStart + sidewalkDepth;
            m.setRotation(90);
        } else {
            ySpawn = sidewalkBottomStart + sidewalkDepth;
            yDespawn = sidewalkTopEnd - sidewalkDepth;
            m.setRotation(270);
        }

        int xSpawn = Greenfoot.getRandomNumber(getWidth() - 200) + 100;
        addObject(m, xSpawn, ySpawn);
        m.setDespawnY(yDespawn);
    }

    /**
     * Prepares and draws all lanes and sidewalks with visual texture.
     *
     * @param world         reference to this {@link World}
     * @param target        target image to draw the lanes on
     * @param spawners      array to store lane {@link BloonSpawner}s
     * @param startY        Y-coordinate for the top of the first lane
     * @param heightPerLane height of each lane
     * @param lanes         total number of lanes
     * @param spacing       pixel spacing between lanes
     * @return array of Y-coordinates for each lane center
     */    
    public int[] prepareLanes(World world, GreenfootImage target, BloonSpawner[] spawners,
                               int startY, int heightPerLane, int lanes, int spacing) {
        int[] lanePositions = new int[lanes];
        int heightOffset = heightPerLane / 2;
    
        int sidewalkThickness = 80; 
        Color SIDEWALK_BASE = new Color(160, 160, 160);
        Color SIDEWALK_LINE = new Color(140, 140, 140);
        Color SIDEWALK_CRACK = new Color(120, 120, 120);
    
        target.setColor(SIDEWALK_BASE);
        target.fillRect(0, startY - sidewalkThickness, target.getWidth(), sidewalkThickness);
    
        target.setColor(SIDEWALK_LINE);
        for (int x = 0; x < target.getWidth(); x += 60) {
            target.fillRect(x, startY - sidewalkThickness, 2, sidewalkThickness);
        }
        for (int y = startY - sidewalkThickness; y < startY; y += 20) {
            target.fillRect(0, y, target.getWidth(), 1);
        }
        target.setColor(SIDEWALK_CRACK);
        for (int x = 30; x < target.getWidth(); x += 120) {
            target.fillRect(x, startY - sidewalkThickness + 10, 1, 20);
        }
    
        target.setColor(GREY_BORDER);
        target.fillRect(0, startY, target.getWidth(), spacing);
    
        for (int i = 0; i < lanes; i++) {
            lanePositions[i] = startY + spacing + (i * (heightPerLane + spacing)) + heightOffset;
    
            target.setColor(GREY_PATH);
            target.fillRect(0, lanePositions[i] - heightOffset, target.getWidth(), heightPerLane);
    
            spawners[i] = new BloonSpawner(heightPerLane, i);
            world.addObject(spawners[i], 0, lanePositions[i]);
    
            if (i > 0) {
                for (int j = 0; j < target.getWidth(); j += 120) {
                    target.setColor(Color.WHITE);
                    target.fillRect(j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                }
            }
        }
    
        int lastLaneBottom = lanePositions[lanes - 1] + heightOffset + spacing;
        target.setColor(SIDEWALK_BASE);
        target.fillRect(0, lastLaneBottom, target.getWidth(), sidewalkThickness);
    
        target.setColor(SIDEWALK_LINE);
        for (int x = 0; x < target.getWidth(); x += 60) {
            target.fillRect(x, lastLaneBottom, 2, sidewalkThickness);
        }
        for (int y = lastLaneBottom; y < lastLaneBottom + sidewalkThickness; y += 20) {
            target.fillRect(0, y, target.getWidth(), 1);
        }
        target.setColor(SIDEWALK_CRACK);
        for (int x = 30; x < target.getWidth(); x += 120) {
            target.fillRect(x, lastLaneBottom + 10, 1, 20);
        }
    
        target.setColor(GREY_BORDER);
        target.fillRect(0, startY - 3, target.getWidth(), 6);
        target.fillRect(0, lastLaneBottom - spacing, target.getWidth(), 6);
    
        return lanePositions;
    }
    
    /**
     * Z-sort so actors with higher Y (lower on screen) render in front.
     * Uses precise Y for SuperSmoothMover when available. Stable for ties.
     */
    public static void zSort(java.util.ArrayList<greenfoot.Actor> actorsToSort, greenfoot.World world) {
        // Local container class (scoped to this method only).
        class Entry implements java.lang.Comparable<Entry> {
            final greenfoot.Actor actor;
            final boolean superSmooth;
            final int order;     // preserve original order for stable ties
            final int xi, yi;    // integer coords snapshot
            final double xd, yd; // precise coords snapshot
    
            // int-based actor
            Entry(greenfoot.Actor a, int x, int y, int order) {
                this.actor = a; this.superSmooth = false; this.order = order;
                this.xi = x; this.yi = y;
                this.xd = x; this.yd = y;
            }
            // precise-based actor
            Entry(greenfoot.Actor a, double x, double y, int order) {
                this.actor = a; this.superSmooth = true; this.order = order;
                this.xi = (int) x; this.yi = (int) y;
                this.xd = x; this.yd = y;
            }
    
            @Override
            public int compareTo(Entry other) {
                double thisY  = superSmooth ? yd : yi;
                double otherY = other.superSmooth ? other.yd : other.yi;
    
                // Handle rare NaN robustly: treat NaN as far back
                if (java.lang.Double.isNaN(thisY) && java.lang.Double.isNaN(otherY)) return java.lang.Integer.compare(order, other.order);
                if (java.lang.Double.isNaN(thisY)) return -1;
                if (java.lang.Double.isNaN(otherY)) return 1;
    
                int cmp = java.lang.Double.compare(thisY, otherY);
                if (cmp != 0) return cmp;
                return java.lang.Integer.compare(this.order, other.order); // stable tie-break
            }
        }
    
        // Snapshot actors and positions first.
        java.util.ArrayList<Entry> list = new java.util.ArrayList<Entry>(actorsToSort.size());
        int order = 0;
        for (greenfoot.Actor a : actorsToSort) {
            if (a instanceof SuperSmoothMover) {
                SuperSmoothMover s = (SuperSmoothMover) a;
                list.add(new Entry(a, s.getPreciseX(), s.getPreciseY(), order++));
            } else {
                list.add(new Entry(a, a.getX(), a.getY(), order++));
            }
        }
    
        // Sort farthest-back (smallest Y) first.
        java.util.Collections.sort(list);
    
        // Re-add in paint order with consistent rounding, then restore precise coords.
        for (Entry e : list) {
            // Remove if currently in any world to ensure paint-order reset
            if (e.actor.getWorld() != null) {
                world.removeObject(e.actor);
            }
            if (e.superSmooth) {
                int rx = roundAwayFromZero(e.xd);
                int ry = roundAwayFromZero(e.yd);
                world.addObject(e.actor, rx, ry);
                // Restore exact double-precision location to avoid drift
                ((SuperSmoothMover) e.actor).setLocation(e.xd, e.yd);
            } else {
                world.addObject(e.actor, e.xi, e.yi);
            }
        }
    }

    /** Helper: symmetric rounding that rounds halves away from zero. */
    private static int roundAwayFromZero(double v) {
        return (int)(v + Math.signum(v) * 0.5);
    }
    
    /** @return Y-coordinates of all lane centers */
    public int[] getLanePositions() {
        return lanePositionsY;
    }
    /**
     * Occasionally triggers a fullscreen {@link IceBlastEffect}.
     * Controlled by a cooldown timer and low random chance.
     */
    private void triggerRandomIceBlast() {
        iceBlastTimer++;
    
        if (iceBlastTimer < 600) return; 
    
        if (iceBlastTimer > 3000) iceBlastTimer = 600; 
    
        if (!iceBlastActive && Greenfoot.getRandomNumber(1000) == 0) {
            iceBlastActive = true;
            iceBlastTimer = 0;
    
            int radius = Math.max(getWidth(), getHeight()) / 2;
            IceBlastEffect blast = new IceBlastEffect(radius);
            addObject(blast, getWidth() / 2, getHeight() / 2);
    
            iceBlastActive = false;
        }
    }
}

