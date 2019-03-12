import processing.core.PImage;

import java.util.List;

public class Obstacle extends Entity{

    public Obstacle(String id, Point position, List<PImage> images){
        setId(id);
        setPosition(position);
        setImages(images);
        setImageIndex(0);
    }
}
