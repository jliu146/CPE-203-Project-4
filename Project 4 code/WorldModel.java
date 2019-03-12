import processing.core.PImage;

import java.util.*;

final class WorldModel
{
   private int numRows;
   private int numCols;
   private Background background[][];
   private Entity occupancy[][];
   private Set<Entity> entities;

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }


   public int getNumRows() {
      return numRows;
   }

   public int getNumCols() {
      return numCols;
   }

   public Set<Entity> getEntities() {
      return entities;
   }

   public void load(Scanner in, ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), imageStore))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }

   public Optional<Point> findOpenAround(Point pos)
   {
      for (int dy = -Functions.ORE_REACH; dy <= Functions.ORE_REACH; dy++)
      {
         for (int dx = -Functions.ORE_REACH; dx <= Functions.ORE_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds(newPt) &&
                    !isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   private boolean processLine(String line, ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[Functions.PROPERTY_KEY])
         {
            case Functions.BGND_KEY:
               return parseBackground(properties, imageStore);
            case Functions.MINER_KEY:
               return parseMiner(properties, imageStore);
            case Functions.OBSTACLE_KEY:
               return parseObstacle(properties, imageStore);
            case Functions.ORE_KEY:
               return parseOre(properties, imageStore);
            case Functions.SMITH_KEY:
               return parseSmith(properties, imageStore);
            case Functions.VEIN_KEY:
               return parseVein(properties, imageStore);
         }
      }

      return false;
   }

   private boolean parseBackground(String [] properties, ImageStore imageStore)
   {
      if (properties.length == Functions.BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Functions.BGND_COL]),
                 Integer.parseInt(properties[Functions.BGND_ROW]));
         String id = properties[Functions.BGND_ID];
         setBackground(pt,
                 new Background(id, imageStore.getImageList(id)));
      }

      return properties.length == Functions.BGND_NUM_PROPERTIES;
   }

   private boolean parseMiner(String [] properties, ImageStore imageStore)
   {
      if (properties.length == Functions.MINER_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Functions.MINER_COL]),
                 Integer.parseInt(properties[Functions.MINER_ROW]));
         Entity entity = createMinerNotFull(properties[Functions.MINER_ID],
                 Integer.parseInt(properties[Functions.MINER_LIMIT]),
                 pt,
                 Integer.parseInt(properties[Functions.MINER_ACTION_PERIOD]),
                 Integer.parseInt(properties[Functions.MINER_ANIMATION_PERIOD]),
                 imageStore.getImageList(Functions.MINER_KEY));
         tryAddEntity(entity);
      }

      return properties.length == Functions.MINER_NUM_PROPERTIES;
   }

   private boolean parseObstacle(String [] properties, ImageStore imageStore)
   {
      if (properties.length == Functions.OBSTACLE_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[Functions.OBSTACLE_COL]),
                 Integer.parseInt(properties[Functions.OBSTACLE_ROW]));
         Entity entity = createObstacle(properties[Functions.OBSTACLE_ID],
                 pt, imageStore.getImageList(Functions.OBSTACLE_KEY));
         tryAddEntity(entity);
      }

      return properties.length == Functions.OBSTACLE_NUM_PROPERTIES;
   }

   private boolean parseOre(String [] properties, ImageStore imageStore)
   {
      if (properties.length == Functions.ORE_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Functions.ORE_COL]),
                 Integer.parseInt(properties[Functions.ORE_ROW]));
         Entity entity = createOre(properties[Functions.ORE_ID],
                 pt, Integer.parseInt(properties[Functions.ORE_ACTION_PERIOD]),
                 imageStore.getImageList(Functions.ORE_KEY));
         tryAddEntity(entity);
      }

      return properties.length == Functions.ORE_NUM_PROPERTIES;
   }

   private boolean parseSmith(String [] properties, ImageStore imageStore)
   {
      if (properties.length == Functions.SMITH_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Functions.SMITH_COL]),
                 Integer.parseInt(properties[Functions.SMITH_ROW]));
         Entity entity = createBlacksmith(properties[Functions.SMITH_ID],
                 pt, imageStore.getImageList(Functions.SMITH_KEY));
         tryAddEntity(entity);
      }

      return properties.length == Functions.SMITH_NUM_PROPERTIES;
   }

   private boolean parseVein(String [] properties, ImageStore imageStore)
   {
      if (properties.length == Functions.VEIN_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Functions.VEIN_COL]),
                 Integer.parseInt(properties[Functions.VEIN_ROW]));
         Entity entity = createVein(properties[Functions.VEIN_ID],
                 pt,
                 Integer.parseInt(properties[Functions.VEIN_ACTION_PERIOD]),
                 imageStore.getImageList(Functions.VEIN_KEY));
         tryAddEntity(entity);
      }

      return properties.length == Functions.VEIN_NUM_PROPERTIES;
   }

   private void tryAddEntity(Entity entity)
   {
      if (isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }

   private boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < numRows &&
              pos.x >= 0 && pos.x < numCols;
   }

   public boolean isOccupied(Point pos)
   {
      return withinBounds(pos) &&
              getOccupancyCell(pos) != null;
   }

   private static Optional<Entity> nearestEntity(List<Entity> entities,
                                                Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = distanceSquared(nearest.getPosition(), pos);

         for (Entity other : entities)
         {
            int otherDistance = distanceSquared(other.getPosition(), pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   private static int distanceSquared(Point p1, Point p2)
   {
      int deltaX = p1.x - p2.x;
      int deltaY = p1.y - p2.y;

      return deltaX * deltaX + deltaY * deltaY;
   }

   public Optional<Entity> findNearest(Point pos,
                                              Class kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : entities)
      {
         if (kind.isInstance(entity))
         {
            ofType.add(entity);
         }
      }

      return nearestEntity(ofType, pos);
   }

   /*
      Assumes that there is no entity currently occupying the
      intended destination cell.
   */
   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.setPosition(pos);
      }
   }

   public void removeEntity(Entity entity)
   {
      removeEntityAt(entity.getPosition());
   }

   private void removeEntityAt(Point pos)
   {
      if (withinBounds(pos)
              && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(-1, -1));
         entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(getBackgroundCell(pos).getCurrentImage());
      }
      else
      {
         return Optional.empty();
      }
   }

   public void setBackground(Point pos,
                                    Background background)
   {
      if (withinBounds(pos))
      {
         setBackgroundCell(pos, background);
      }
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   private Entity getOccupancyCell(Point pos)
   {
      return occupancy[pos.y][pos.x];
   }

   private void setOccupancyCell(Point pos,
                                       Entity entity)
   {
      occupancy[pos.y][pos.x] = entity;
   }

   public Background getBackgroundCell(Point pos)
   {
      if(withinBounds(pos)) {
         return background[pos.y][pos.x];
      }
      return null;
   }

   private void setBackgroundCell(Point pos,
                                        Background b)
   {
      background[pos.y][pos.x] = b;
   }



   public static Blacksmith createBlacksmith(String id, Point position,
                                         List<PImage> images)
   {
      return new Blacksmith(id, position, images);
   }

   public static Miner_Full createMinerFull(String id, int resourceLimit,
                                        Point position, int actionPeriod, int animationPeriod,
                                        List<PImage> images)
   {
      return new Miner_Full(id, position, images,
              resourceLimit, resourceLimit, actionPeriod, animationPeriod);
   }

   public static Miner_Not_Full createMinerNotFull(String id, int resourceLimit,
                                           Point position, int actionPeriod, int animationPeriod,
                                           List<PImage> images)
   {
      return new Miner_Not_Full(id, position, images,
              resourceLimit, 0, actionPeriod, animationPeriod);
   }

   public static Obstacle createObstacle(String id, Point position,
                                       List<PImage> images)
   {
      return new Obstacle(id, position, images);
   }

   public static Ore createOre(String id, Point position, int actionPeriod,
                                  List<PImage> images)
   {
      return new Ore(id, position, images, 0, actionPeriod);
   }

   public static Ore_Blob createOreBlob(String id, Point position,
                                      int actionPeriod, int animationPeriod, List<PImage> images)
   {
      return new Ore_Blob(id, position, images,
              0, actionPeriod, animationPeriod);
   }

   public static Quake createQuake(Point position, List<PImage> images)
   {
      return new Quake(Functions.QUAKE_ID, position, images,
              0, Functions.QUAKE_ACTION_PERIOD, Functions.QUAKE_ANIMATION_PERIOD);
   }

   public static Vein createVein(String id, Point position, int actionPeriod,
                                   List<PImage> images)
   {
      return new Vein(id, position, images, 0, actionPeriod);
   }


}
