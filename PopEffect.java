import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class x here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PopEffect extends Actor
{
    private int timer = 0; 

    {
        GreenfootImage effect = new GreenfootImage("Pop.png");
        effect.scale(100, 91);
        setImage(effect);
        GreenfootSound pop = new GreenfootSound("Pop.wav");
        pop.setVolume(20);
        pop.play();
    }

    @Override
    public void act() {
        timer++;
        if (timer > 5) { 
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
        }
    }
}

