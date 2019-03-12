public class Activity implements Action {

    private ActiveEntity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Activity(ActiveEntity entity, WorldModel world,
                  ImageStore imageStore, int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler){
        executeActivityAction(scheduler);
    }

    private void executeActivityAction(EventScheduler scheduler)
    {
        entity.executeActivity(world, imageStore, scheduler);
    }

    public static Activity createActivityAction(ActiveEntity entity, WorldModel world,
                                              ImageStore imageStore)
    {
        return new Activity(entity, world, imageStore, 0);
    }
}
