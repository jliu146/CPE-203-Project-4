public abstract class ActiveEntity extends Entity {

    private int actionPeriod;

    protected int getActionPeriod() {
        return actionPeriod;
    }

    protected void setActionPeriod(int actionPeriod) {
        this.actionPeriod = actionPeriod;
    }

    protected void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this,
                Activity.createActivityAction(this, world, imageStore),
                actionPeriod);
    }

    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

}
