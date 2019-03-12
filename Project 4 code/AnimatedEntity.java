public abstract class AnimatedEntity extends ActiveEntity {

    private int animationPeriod;

    protected int getAnimationPeriod() {
        return animationPeriod;
    }

    protected void nextImage()
    {
        setImageIndex((getImageIndex() + 1) % getImages().size());
    }

    protected void setAnimationPeriod(int animationPeriod) {
        this.animationPeriod = animationPeriod;
    }

    protected void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){

        super.scheduleActions(scheduler, world, imageStore);

        scheduler.scheduleEvent(this,
                Animation.createAnimationAction(this, 0), getAnimationPeriod());
    }


}
