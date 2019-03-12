import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public abstract class Entity{

   private String id;
   private Point position;
   private List<PImage> images;
   private int imageIndex;

   protected String getId() {
      return id;
   }

   protected Point getPosition(){
      return position;
   }

   protected List<PImage> getImages(){
      return images;
   }

   protected void setId(String id) {
      this.id = id;
   }

   protected void setImages(List<PImage> images) {
      this.images = images;
   }

   protected void setImageIndex(int imageIndex) {
      this.imageIndex = imageIndex;
   }

   protected void setPosition(Point position){
      this.position = position;
   }

   protected int getImageIndex(){
      return imageIndex;
   }

   protected PImage getCurrentImage(){
      return images.get(imageIndex);
   }

   protected static boolean adjacent(Point p1, Point p2)
   {
      return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) ||
              (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
   }




}
