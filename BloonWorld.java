import greenfoot.*;
import java.util.ArrayList;
import java.util.Collections;

public class BloonWorld extends World {
    private static boolean assetsLoaded = false;

    private GreenfootImage background;
    public static Color GREY_BORDER = new Color(108, 108, 108);
    public static Color GREY_PATH = new Color(120, 120, 120);
    public static Color SIDEWALK_COLOR = new Color(160, 160, 160);

    private int laneHeight = 64;
    private int laneCount = 6;
    private int spaceBetweenLanes = 6;
    private int[] lanePositionsY;
    private BloonSpawner[] laneSpawners;
    public static boolean SHOW_SPAWNERS = false;

    private int spawnTimer = 0;
    private int simulationTime = 0;
    private static final int BASE_MONKEY_INTERVAL = 80;
    private static final int BASE_BLOON_INTERVAL = 240;
    private int[] laneSpawnTimers = new int[laneCount];
    private int monkeySpawnTimer = 0;
    private int bloonSpawnTimer = 0;

    // sidewalk bounds
    private int sidewalkTopStart;
    private int sidewalkTopEnd;
    private int sidewalkBottomStart;
    private int sidewalkBottomEnd;

    // --- Developer testing features ---
    private boolean devMode = false;  // toggle developer mode
    private Class<? extends Bloon> devBloon;  // type of bloon to spawn
    private Class<? extends Monkey> devMonkey; // type of monkey to spawn

    public void enableDevMode(Class<? extends Bloon> bloonType, Class<? extends Monkey> monkeyType) {
        devMode = true;
        devBloon = bloonType;
        devMonkey = monkeyType;
    }

    public void disableDevMode() {
        devMode = false;
        devBloon = null;
        devMonkey = null;
    }

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
        //enableDevMode(CeramicBloon.class, BombTower.class);
    }

    public void act() {
        simulationTime++;
        spawnBloons();
        spawnMonkeys();
        zSort((ArrayList<Actor>) getObjects(Actor.class), this);
    }

private void spawnBloons() {
    // --- DEV MODE ---
    if (devMode && devBloon != null) {
        bloonSpawnTimer++;
        if (bloonSpawnTimer < BASE_BLOON_INTERVAL) return;
        bloonSpawnTimer = 0;

        // Spawn the dev bloon in ALL lanes
        for (int lane = 0; lane < laneCount; lane++) {
            BloonSpawner spawner = laneSpawners[lane];
            int direction = (lane < 3) ? -1 : 1;
            int startX = (direction == 1) ? 1 : getWidth() - 1;

            try {
                Bloon b = devBloon
                    .getConstructor(int.class, int.class)
                    .newInstance(direction, spawner.getY());
                addObject(b, startX, spawner.getY());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    // --- NORMAL SPAWNING ---
    simulationTime++;

    for (int lane = 0; lane < laneCount; lane++) {
        BloonSpawner spawner = laneSpawners[lane];
        laneSpawnTimers[lane]++;

        if (Greenfoot.getRandomNumber(100) < 2) { // random chance to spawn
            if (laneSpawnTimers[lane] >= 30 && !spawner.isTouchingBloon()) {
                laneSpawnTimers[lane] = 0;

                // --- Determine allowed tiers based on simulation time ---
                ArrayList<Integer> allowedTiers = new ArrayList<>();
                if (simulationTime < 600) allowedTiers.add(0);
                else if (simulationTime < 1100) { for (int i = 0; i <= 1; i++) allowedTiers.add(i); }
                else if (simulationTime < 1700) { for (int i = 0; i <= 2; i++) allowedTiers.add(i); }
                else if (simulationTime < 2300) { for (int i = 0; i <= 3; i++) allowedTiers.add(i); }
                else if (simulationTime < 2900) { for (int i = 0; i <= 4; i++) allowedTiers.add(i); }
                else if (simulationTime < 4100) { for (int i = 0; i <= 6; i++) allowedTiers.add(i); }
                else if (simulationTime < 5400) { for (int i = 0; i <= 8; i++) allowedTiers.add(i); }
                else if (simulationTime < 6000) { for (int i = 0; i <= 10; i++) allowedTiers.add(i); }
                else { for (int i = 0; i <= 11; i++) allowedTiers.add(i); }

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

    // --- Spawn Monkeys ---
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
                if (roll < 35) monkeyType = 0;
                else if (roll < 50) monkeyType = 1;
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



// Lane preparation with textured sidewalks
public int[] prepareLanes(World world, GreenfootImage target, BloonSpawner[] spawners,
                           int startY, int heightPerLane, int lanes, int spacing) {
    int[] lanePositions = new int[lanes];
    int heightOffset = heightPerLane / 2;

    // --- Adjustable constants ---
    int sidewalkThickness = 80; // thicker sidewalks
    Color SIDEWALK_BASE = new Color(160, 160, 160);
    Color SIDEWALK_LINE = new Color(140, 140, 140);
    Color SIDEWALK_CRACK = new Color(120, 120, 120);

    // --- Draw top textured sidewalk ---
    target.setColor(SIDEWALK_BASE);
    target.fillRect(0, startY - sidewalkThickness, target.getWidth(), sidewalkThickness);

    // Add light texture (lines/cracks)
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

    // --- Draw lanes + dividers ---
    target.setColor(GREY_BORDER);
    target.fillRect(0, startY, target.getWidth(), spacing);

    for (int i = 0; i < lanes; i++) {
        lanePositions[i] = startY + spacing + (i * (heightPerLane + spacing)) + heightOffset;

        target.setColor(GREY_PATH);
        target.fillRect(0, lanePositions[i] - heightOffset, target.getWidth(), heightPerLane);

        spawners[i] = new BloonSpawner(heightPerLane, i);
        world.addObject(spawners[i], 0, lanePositions[i]);

        // Dashed dividers between lanes
        if (i > 0) {
            for (int j = 0; j < target.getWidth(); j += 120) {
                target.setColor(Color.WHITE);
                target.fillRect(j, lanePositions[i] - heightOffset - spacing, 60, spacing);
            }
        }
    }

    // --- Draw bottom textured sidewalk ---
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

    // Border above and below roads
    target.setColor(GREY_BORDER);
    target.fillRect(0, startY - 3, target.getWidth(), 6);
    target.fillRect(0, lastLaneBottom - spacing, target.getWidth(), 6);

    return lanePositions;
}



    public static void zSort(ArrayList<Actor> actors, World world) {
        ArrayList<ActorContent> list = new ArrayList<>();
        for (Actor a : actors) list.add(new ActorContent(a, a.getX(), a.getY()));
        Collections.sort(list);
        for (ActorContent a : list) {
            Actor actor = a.getActor();
            world.removeObject(actor);
            world.addObject(actor, a.getX(), a.getY());
        }
    }
}

class ActorContent implements Comparable<ActorContent> {
    private Actor actor;
    private int xx, yy;

    public ActorContent(Actor actor, int xx, int yy) {
        this.actor = actor;
        this.xx = xx;
        this.yy = yy;
    }

    public int getX() { return xx; }
    public int getY() { return yy; }
    public Actor getActor() { return actor; }

    @Override
    public int compareTo(ActorContent other) { return this.yy - other.yy; }
}
