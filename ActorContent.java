import greenfoot.Actor;
public class ActorContent implements Comparable<ActorContent> {
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