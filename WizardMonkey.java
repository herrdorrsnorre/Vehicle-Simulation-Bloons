import greenfoot.*;

public class WizardMonkey extends Monkey {

    public WizardMonkey() {
        fireRate = 30;
        range = 300;
        projectileType = MagicProjectile.class;
        health = 5;
        speed = 3;

        GreenfootImage monkey = new GreenfootImage("Wizard_Monkey.png");
        monkey.scale(67, 70);
        setImage(monkey);
    }
}
