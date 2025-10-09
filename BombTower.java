import greenfoot.*;

public class BombTower extends Monkey {

    public BombTower() {
        fireRate = 90;
        range = 300;
        projectileType = BombProjectile.class;
        health = 8;
        speed = 2;

        GreenfootImage monkey = new GreenfootImage("Bomb_Tower.png");
        monkey.scale(67, 70);
        setImage(monkey);
    }
}
