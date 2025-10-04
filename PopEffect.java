import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class x here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PopEffect extends Actor
{
    private int timer = 0; // counts frames

    {
        setImage("Pop.png"); // make sure this file is in the images folder
        GreenfootSound pop = new GreenfootSound("Pop.mp3");
        pop.setVolume(30);
        pop.play();
    }

    @Override
    public void act() {
        timer++;
        if (timer > 5) { // stays for 10 frames (~0.16s at 60fps)
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
        }
    }
}

