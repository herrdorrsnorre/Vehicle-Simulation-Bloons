public class BoomerangMonkey extends Monkey {

    public BoomerangMonkey() {
        range = 500;
        fireRate = 45; // faster throwing
        projectileType = Boomerang.class;
        health = 10;
        speed = 3;
        setImage("Boomerang_Monkey.png");
    }
}
 
    