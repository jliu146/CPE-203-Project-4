import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Slime extends AnimatedEntity {
    private Random rng = new Random();


    public Slime(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod){
        setId(id);
        setPosition(position);
        setImages(images);
        setImageIndex(imageIndex);
        setActionPeriod(actionPeriod);
        setAnimationPeriod(animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        int dir = rng.nextInt(4);
        boolean spawnBlob = rng.nextInt(15) == 0;
        int x = getPosition().x;
        int y = getPosition().y;
        Point nextPos = new Point(x, y);
        switch(dir){
            case 0:
                nextPos = new Point(x+1, y);
                break;
            case 1:
                nextPos = new Point(x-1, y);
                break;
            case 2:
                nextPos = new Point(x, y+1);
                break;
            case 3:
                nextPos = new Point(x, y-1);
                break;

        }

        if(!world.isOccupied(nextPos)) {
            world.moveEntity(this, nextPos);
            if(spawnBlob){
                Ore_Blob blob = WorldModel.createOreBlob("blob", new Point(x, y), 20000, 20, imageStore.getImageList("blob") );
                world.addEntity(blob);
                blob.scheduleActions(scheduler, world, imageStore);
            }

        }

        scheduler.scheduleEvent(this,
                Activity.createActivityAction(this, world, imageStore),
                getActionPeriod());
    }
}
