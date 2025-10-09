public class SuperMonkey extends Monkey {

    public SuperMonkey() {
        range = 500;
        fireRate = 7; // shoots very fast
        projectileType = Dart.class; // or you can make a new Laser projectile
        health = 30;
        speed = 2;
        setImage("Super_Monkey.png");
    }
}

