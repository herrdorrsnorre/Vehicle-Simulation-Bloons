import greenfoot.*;

public class BoomerangMonkey extends Monkey {

    public BoomerangMonkey() {
        range = 500;
        fireRate = 45; 
        projectileType = Boomerang.class;
        health = 10;
        speed = 3;
        GreenfootImage monkey = new GreenfootImage("Boomerang_Monkey.png");
        monkey.scale(71, 77);
        setImage(monkey);
    }
}
 
    