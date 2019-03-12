import processing.core.PImage;

import java.util.List;

public class Splash extends AnimatedEntity {
    private int life = 5;

    public Splash(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod){
        setId(id);
        setPosition(position);
        setImages(images);
        setImageIndex(imageIndex);
        setActionPeriod(actionPeriod);
        setAnimationPeriod(animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        //System.out.println(life);
        if(life <= 0){
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
        }else {
            life--;
            scheduleActions(scheduler, world, imageStore);
        }

    }
}
