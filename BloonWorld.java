import greenfoot.*;
import java.util.ArrayList;
import java.util.Collections;

public class BloonWorld extends World {
    private GreenfootImage background;
    public static Color GREY_BORDER = new Color(108, 108, 108);
    public static Color GREY_PATH = new Color(120, 120, 120);
    private int laneHeight = 64;
    private int laneCount = 6;
    private int spaceBetweenLanes = 6;
    private int[] lanePositionsY;
    private BloonSpawner[] laneSpawners;
    public static boolean SHOW_SPAWNERS = false; // put this back at the top of BloonWorld

    private int spawnTimer = 0;
    private int simulationTime = 0; // global progression counter
    private static final int BASE_MONKEY_INTERVAL = 40; // doubled from 20 to reduce spawn rate
    
    private static final int BASE_BLOON_INTERVAL = 240; // slower spawn per lane
    private int[] laneSpawnTimers = new int[laneCount];
    private int monkeySpawnTimer = 0;
    private int bloonSpawnTimer = 0;
    public BloonWorld() {
        super(1024, 800, 1, false);
        background = new GreenfootImage("background01.png");
        setBackground(background);

        laneSpawners = new BloonSpawner[laneCount];
        lanePositionsY = prepareLanes(this, background, laneSpawners, 232, laneHeight, laneCount, spaceBetweenLanes);
    }

    public void act() {
        simulationTime++;       // track overall simulation progression
        spawnBloons();
        spawnMonkeys();
        zSort((ArrayList<Actor>) getObjects(Actor.class), this);
    }

private void spawnBloons() {
    spawnTimer++;

    for (int lane = 0; lane < laneCount; lane++) {
        laneSpawnTimers[lane]++;
        BloonSpawner spawner = laneSpawners[lane];

        // Dynamic interval: early game slower, late game faster
        int dynamicInterval = (int)(BASE_BLOON_INTERVAL - (BASE_BLOON_INTERVAL - 60) * Math.min(1.0, simulationTime / 3600.0));

        if (laneSpawnTimers[lane] < dynamicInterval || spawner.isTouchingBloon()) continue;
        laneSpawnTimers[lane] = 0; // reset timer

        // Determine tier progression (slower ramp)
        ArrayList<Integer> allowedTiers = new ArrayList<>();
        if (simulationTime < 600) allowedTiers.add(4); // Red only
        else if (simulationTime < 1200) { allowedTiers.add(4); allowedTiers.add(1); } // Red + Blue
        else if (simulationTime < 1800) { allowedTiers.add(4); allowedTiers.add(1); allowedTiers.add(2); } // + Green
        else if (simulationTime < 2400) { allowedTiers.add(4); allowedTiers.add(1); allowedTiers.add(2); allowedTiers.add(3); } // + Yellow
        else if (simulationTime < 3000) { allowedTiers.add(4); allowedTiers.add(1); allowedTiers.add(2); allowedTiers.add(3); allowedTiers.add(0); } // + Pink
        else { // everything unlocked progressively
            for (int i = 0; i <= 8; i++) allowedTiers.add(i);
        }

        int bloonType = allowedTiers.get(Greenfoot.getRandomNumber(allowedTiers.size()));
        Bloon b;
        switch (bloonType) {
            case 0: b = new PinkBloon(1, spawner.getY()); break;
            case 1: b = new BlueBloon(1, spawner.getY()); break;
            case 2: b = new GreenBloon(1, spawner.getY()); break;
            case 3: b = new YellowBloon(1, spawner.getY()); break;
            case 4: b = new RedBloon(1, spawner.getY()); break;
            case 5: b = new WhiteBloon(1, spawner.getY()); break;
            case 6: b = new BlackBloon(1, spawner.getY()); break;
            case 7: b = new ZebraBloon(1, spawner.getY()); break;
            case 8: b = new RainbowBloon(1, spawner.getY()); break;
            default: b = new RedBloon(1, spawner.getY()); break;
        }

        addObject(b, 0, spawner.getY());
    }
}




private void spawnMonkeys() {
    monkeySpawnTimer++;
    
    // Dynamic modifier: reduce interval as simulation progresses (max 50% faster)
    int dynamicInterval = (int)(BASE_MONKEY_INTERVAL * Math.max(0.5, 1.0 - simulationTime / 4800.0));
    if (monkeySpawnTimer < dynamicInterval) return;
    monkeySpawnTimer = 0;

    int currentDarts = getObjects(DartMonkey.class).size();
    int currentBoomerangs = getObjects(BoomerangMonkey.class).size();
    int currentSupers = getObjects(SuperMonkey.class).size();

    // Determine phase based on simulationTime
    int phase = 1;
    if (simulationTime >= 1200 && simulationTime < 2400) phase = 2;
    if (simulationTime >= 2400) phase = 3;

    // Decide monkey type to spawn based on phase
    int monkeyType = -1;
    int roll = Greenfoot.getRandomNumber(100);

    switch (phase) {
        case 1: // Darts dominate
            if (roll < 30) monkeyType = 0; // Dart common, reduced from 60%
            break;
        case 2: // Boomers dominate
            if (roll < 25) monkeyType = 1; // Boomerang common, reduced from 50%
            else if (roll < 35) monkeyType = 0; // Dart rare, reduced from 65%
            break;
        case 3: // Supers dominate
            if (roll < 20) monkeyType = 2; // Super common, reduced from 40%
            else if (roll < 30) monkeyType = 1; // Boomerang uncommon, reduced from 60%
            else if (roll < 37) monkeyType = 0; // Dart uncommon, reduced from 75%
            break;
    }

    if (monkeyType == -1) return;

    Monkey m;
    switch (monkeyType) {
        case 0: m = new DartMonkey(); break;
        case 1: m = new BoomerangMonkey(); break;
        case 2: m = new SuperMonkey(); break;
        default: return;
    }

    addMonkey(m);
}


    private void addMonkey(Monkey m) {
        boolean spawnAtTop = Greenfoot.getRandomNumber(2) == 0;
        int ySpawn;
        if (spawnAtTop) {
            ySpawn = m.getImage().getHeight() / 2;
            m.setRotation(90);
        } else {
            ySpawn = getHeight() - m.getImage().getHeight() / 2;
            m.setRotation(270);
        }
        int xSpawn = Greenfoot.getRandomNumber(getWidth() - 200) + 100;
        addObject(m, xSpawn, ySpawn);
    }

    // Lane preparation
    public int[] prepareLanes(World world, GreenfootImage target, BloonSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing) {
        int[] lanePositions = new int[lanes];
        int heightOffset = heightPerLane / 2;

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

        target.setColor(GREY_BORDER);
        target.fillRect(0, lanePositions[lanes - 1] + heightOffset, target.getWidth(), spacing);

        return lanePositions;
    }

    // Z-sort
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
