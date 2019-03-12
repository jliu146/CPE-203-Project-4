import processing.core.PImage;

import java.util.List;

public class Quake extends AnimatedEntity{

    public Quake(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod){
        setId(id);
        setPosition(position);
        setImages(images);
        setImageIndex(imageIndex);
        setActionPeriod(actionPeriod);
        setAnimationPeriod(animationPeriod);

    }

    public void executeActivity(WorldModel world,
                                     ImageStore imageStore, EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    //do not remove, this is different
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent((Entity)this,
                Activity.createActivityAction(this, world, imageStore),
                getActionPeriod());
        scheduler.scheduleEvent((Entity)this,
                Animation.createAnimationAction(this, Functions.QUAKE_ANIMATION_REPEAT_COUNT),
                getAnimationPeriod());
    }
}
