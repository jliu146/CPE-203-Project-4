import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Vein extends ActiveEntity{

    public Vein(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod){
        setId(id);
        setPosition(position);
        setImages(images);
        setImageIndex(imageIndex);
        setActionPeriod(actionPeriod);
    }

    public void executeActivity(WorldModel world,
                                ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(getPosition());

        if (openPt.isPresent())
        {
            Ore ore = WorldModel.createOre(Functions.ORE_ID_PREFIX + getId(),
                    openPt.get(), Functions.ORE_CORRUPT_MIN +
                            Functions.rand.nextInt(Functions.ORE_CORRUPT_MAX - Functions.ORE_CORRUPT_MIN),
                    imageStore.getImageList(Functions.ORE_KEY));
            world.addEntity(ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                Activity.createActivityAction(this, world, imageStore),
                getActionPeriod());
    }
}
