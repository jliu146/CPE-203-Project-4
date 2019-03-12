import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class Miner_Full extends Miner_Abstract{

    public Miner_Full(String id, Point position,
                  List<PImage> images, int resourceLimit, int resourceCount,
                  int actionPeriod, int animationPeriod)
    {
        setId(id);
        setPosition(position);
        setImages(images);
        setImageIndex(0);
        setActionPeriod(actionPeriod);
        setAnimationPeriod(animationPeriod);

        setResourceLimit(resourceLimit);
        setResourceCount(resourceCount);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        if(world.getBackgroundCell(getPosition()) != null && world.getBackgroundCell(getPosition()).getId().equals("water")){
            drown(world, imageStore, scheduler);
        }
        Optional<Entity> fullTarget = world.findNearest(getPosition(),
                Blacksmith.class);

        if (fullTarget.isPresent() &&
                moveToFull(world, fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this,
                    Activity.createActivityAction(this, world, imageStore),
                    getActionPeriod());
        }
    }

    private void drown(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        Splash splash = new Splash("splash", getPosition(), imageStore.getImageList("splash"), 0, 20000, 10);
        world.removeEntity(this);
        world.addEntity(splash);
        splash.scheduleActions(scheduler, world, imageStore);
    }

    private void transformFull(WorldModel world,
                               EventScheduler scheduler, ImageStore imageStore)
    {
        AnimatedEntity miner = WorldModel.createMinerNotFull(getId(), getResourceLimit(),
                getPosition(), getActionPeriod(), getAnimationPeriod(),
                getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    private boolean moveToFull(WorldModel world,
                               Entity target, EventScheduler scheduler)
    {
        if (Entity.adjacent(getPosition(), target.getPosition()))
        {
            return true;
        }
        else
        {
            Point nextPos = nextPositionMiner(world, target.getPosition());

            if (!getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    private Point nextPositionMiner(WorldModel world,
                                    Point destPos)
    {
        int horiz = Integer.signum(destPos.x - getPosition().x);
        Point newPos = new Point(getPosition().x + horiz,
                getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos))
        {
            int vert = Integer.signum(destPos.y - getPosition().y);
            newPos = new Point(getPosition().x,
                    getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos))
            {
                newPos = getPosition();
            }
        }

        return newPos;
    }
}
