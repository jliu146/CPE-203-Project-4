public class Animation implements Action {

    private AnimatedEntity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Animation(AnimatedEntity entity, WorldModel world,
                  ImageStore imageStore, int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
        executeAnimationAction(scheduler);
    }

    private void executeAnimationAction(EventScheduler scheduler)
    {
        entity.nextImage();

        if (repeatCount != 1)
        {
            scheduler.scheduleEvent(entity,
                    createAnimationAction(entity,
                            Math.max(repeatCount - 1, 0)),
                    entity.getAnimationPeriod());
        }
    }

    public static Animation createAnimationAction(AnimatedEntity entity, int repeatCount)
    {
        return new Animation(entity, null, null, repeatCount);
    }
}
