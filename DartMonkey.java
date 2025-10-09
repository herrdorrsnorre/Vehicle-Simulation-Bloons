import greenfoot.*;

public class DartMonkey extends Monkey {
    
    public DartMonkey() {
        range = 450;
        fireRate = 50;
        projectileType = Dart.class;
        health = 5;
        speed = 5;
        GreenfootImage monkey = new GreenfootImage("Dart_Monkey.png");
        monkey.scale(67, 70);
        setImage(monkey);
    }
    
}
