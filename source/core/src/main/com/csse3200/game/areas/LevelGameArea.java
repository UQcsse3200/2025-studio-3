package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.areas.terrain.TerrainFactory.TerrainType;
import com.csse3200.game.components.InventoryUnitInputComponent;
import com.csse3200.game.components.currency.CurrencyGeneratorComponent;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.TeleportRobotConfig;
import com.csse3200.game.entities.factories.DefenceFactory;
import com.csse3200.game.entities.factories.GridFactory;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a level in the game, creates the map, a tiled grid for the playing area and a player unit
 * inventory allowing the player to add units to the grid.
 */
public class LevelGameArea extends GameArea implements AreaAPI {
  private static final Logger logger = LoggerFactory.getLogger(LevelGameArea.class);
  private static final int LEVEL_ONE_ROWS = 5;
  private static final int LEVEL_ONE_COLS = 10;
  private static final String[] levelTextures = {
    "images/box_boy_leaf.png",
    "images/level-1-map-v1.png",
    "images/ghost_king.png",
    "images/ghost_1.png",
    "images/olive_tile.png",
    "images/green_tile.png",
    "images/box_boy.png",
    "images/selected_star.png",
    "images/sling_shooter_1.png",
    "images/sling_shooter_front.png"
  };

  private static final String[] levelTextureAtlases = {
    "images/ghost.atlas",
    "images/ghostKing.atlas",
    "images/sling_shooter.atlas",
    "images/robot_placeholder.atlas"
  };

  private static final String[] levelSounds = {"sounds/Impact4.ogg"};
  private static final String backgroundMusic = "sounds/BGM_03_mp3.mp3";
  private static final String[] levelMusic = {backgroundMusic};

  private final TerrainFactory terrainFactory;

  // Offset values
  private float xOffset;
  private float yOffset;
  private float tileSize;
  private float invStartX;
  private float invY;
  private float invSelectedY;
  private float stageHeight;
  private float stageToWorldRatio;
  private LevelGameGrid grid;
  private final Entity[] spawned_units;
  private Entity selected_unit;
  private Entity selection_star;
  private boolean isGameOver = false;
  private final ArrayList<Entity> robots = new ArrayList<>();

  // May have to use a List<Entity> instead if we need to know what entities are at what position
  // But for now it doesn't matter
  private int inventoryUnitCount;

  /**
   * Initialise this LevelGameArea to use the provided TerrainFactory.
   *
   * @param terrainFactory TerrainFactory used to create the terrain for the GameArea.
   */
  public LevelGameArea(TerrainFactory terrainFactory) {
    super();
    setScaling();

    this.terrainFactory = terrainFactory;
    selected_unit = null; // None selected at level load
    spawned_units = new Entity[LEVEL_ONE_ROWS * LEVEL_ONE_COLS];
    selection_star = null;
  }

  /**
   * Uses stage height and width (screen resolution from {@link
   * com.csse3200.game.rendering.RenderService}) to set variables relating to tile, grid and
   * character sizing and placement.
   */
  public void setScaling() {
    stageHeight = ServiceLocator.getRenderService().getStage().getHeight();
    float stageWidth = ServiceLocator.getRenderService().getStage().getWidth();
    stageToWorldRatio = Renderer.GAME_SCREEN_WIDTH / stageWidth;

    float gridHeight = (stageHeight * stageToWorldRatio) / 8f * LEVEL_ONE_ROWS;
    tileSize = gridHeight / LEVEL_ONE_ROWS;
    xOffset = 2f * tileSize;
    yOffset = tileSize;
    invStartX = xOffset;
    invY = yOffset + (LEVEL_ONE_ROWS + 0.5f) * tileSize;
    invSelectedY = yOffset + (LEVEL_ONE_ROWS + 0.5f) * tileSize;
  }

  /** Creates the game area by calling helper methods as required. */
  @Override
  public void create() {
    loadAssets();

    displayUI();

    spawnMap();
    spawnSun();
    spawnGrid(LEVEL_ONE_ROWS, LEVEL_ONE_COLS);
    spawnRobot(7, 2, "tanky");
    spawnRobot(10, 1, "standard");
    spawnRobot(10, 4, "fast");
    spawnRobot(8, 4, "standard");
    spawnRobot(9, 3, "teleportation");


      spawnInventory();

    playMusic();
  }

    private float[] computeLaneYs() {
        float[] ys = new float[LEVEL_ONE_ROWS];
        for (int r = 0; r < LEVEL_ONE_ROWS; r++) {
            ys[r] = yOffset + tileSize * r;   // EXACT same formula used to place units
        }
        return ys;
    }

  /** Uses the {@link ResourceService} to load the assets for the level. */
  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(levelTextures);
    resourceService.loadTextureAtlases(levelTextureAtlases);
    resourceService.loadSounds(levelSounds);
    resourceService.loadMusic(levelMusic);

    while (!resourceService.loadForMillis(10)) {
      // This could be upgraded to a loading screen
      logger.info("Loading... {}%", resourceService.getProgress());
    }
  }

  /** Spawns the level UI */
  private void displayUI() {
    Entity ui = new Entity();
    // add components here for additional UI Elements
    ui.addComponent(new GameAreaDisplay("Level One"));
    spawnEntity(ui);
  }

  /** Creates the map in the {@link TerrainFactory} and spawns it in the correct position. */
  private void spawnMap() {
    logger.debug("Spawning level one map");

    // Create the background terrain (single image map)
    terrain = terrainFactory.createTerrain(TerrainType.LEVEL_ONE_MAP);

    // Wrap in an entity
    Entity mapEntity = new Entity().addComponent(terrain);

    // Compute world size
    float tileWidth = terrain.getTileSize();
    float tileHeight = terrain.getTileSize();
    GridPoint2 bounds = terrain.getMapBounds(0);
    float worldWidth = bounds.x * tileWidth;
    float worldHeight = bounds.y * tileHeight;
    mapEntity.setPosition(worldWidth / 2f, worldHeight / 2f);

    spawnEntity(mapEntity);
  }

  /** Determines inventory units to spawn for the level and calls method to place them. */
  private void spawnInventory() {
    inventoryUnitCount = 0;
    placeInventoryUnit(() -> null, "images/ghost_1.png");
    placeInventoryUnit(() -> null, "images/ghost_king.png");
    placeInventoryUnit(
        () -> DefenceFactory.createSlingShooter(new ArrayList<>()),
        "images/sling_shooter_front.png");
  }

  private void spawnSun() {
    Entity sunSpawner = new Entity();
    CurrencyGeneratorComponent currencyGenerator =
        new CurrencyGeneratorComponent(5f, 25, "images/normal_sunlight.png");
    sunSpawner.addComponent(currencyGenerator);
    spawnEntity(sunSpawner);
  }

  /**
   * Spawns the grid of tiles for the game
   *
   * @param rows an int that is the number of rows wanted for the grid
   * @param cols an int that is the number of columns wanted for the grid
   */
  private void spawnGrid(int rows, int cols) {
    grid = new LevelGameGrid(rows, cols);
    for (int i = 0; i < rows * cols; i++) {
      Entity tile;
      // Calc tile position
      float tileX = xOffset + tileSize * (i % cols);
      float tileY = yOffset + tileSize * (float) (i / cols);
      // logic for alternating tile images
      if ((i / cols) % 2 == 1) {
        tile = GridFactory.createTile(i % 2, tileSize, tileX, tileY, this);
      } else {
        tile = GridFactory.createTile(1 - (i % 2), tileSize, tileX, tileY, this);
      }
      tile.setPosition(tileX, tileY);
      tile.getComponent(TileStorageComponent.class).setPosition(i);
      grid.addTile(i, tile);
      spawnEntity(tile);
    }
  }

  /**
   * Places a unit with its supplier in the inventory
   *
   * @param supplier function returning a copy of that unit
   * @param image sprite image for how it will be displayed in the inventory
   */
  private void placeInventoryUnit(Supplier<Entity> supplier, String image) {
    int pos = ++inventoryUnitCount;
    Entity unit =
        new Entity()
            .addComponent(new InventoryUnitInputComponent(this, supplier))
            .addComponent(new TextureRenderComponent(image));
    unit.setPosition(invStartX + (pos - 1) * (tileSize * 1.5f), invY);
    unit.scaleHeight(tileSize);
    spawnEntity(unit);
  }

  /** Unloads the level assets form the {@link ResourceService} */
  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(levelTextures);
    resourceService.unloadAssets(levelTextureAtlases);
    resourceService.unloadAssets(levelSounds);
    resourceService.unloadAssets(levelMusic);
  }

  /** Extends the super method to stop music and unload assets. */
  @Override
  public void dispose() {
    super.dispose();
    ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class).stop();
    this.unloadAssets();
  }

  /** Starts the music */
  private void playMusic() {
    Music music = ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class);
    music.setLooping(true);
    music.setVolume(0.3f);
    music.play();
  }

  /**
   * Getter for grid
   *
   * @return grid
   */
  public LevelGameGrid getGrid() {
    return grid;
  }

  public void setGrid(LevelGameGrid newGrid) {
    this.grid = newGrid;
  }

  public void spawnRobot(int col, int row, String robotType) {
    Entity unit;

      if ("teleportation".equalsIgnoreCase(robotType)) {
          float[] laneYs = computeLaneYs();   // real row Y’s
          unit = RobotFactory.createTeleportRobot(new TeleportRobotConfig(), laneYs);
      } else {
          unit = RobotFactory.createRobotType(robotType);
      }

      // Get and set position coords
    col = Math.max(0, Math.min(col, LEVEL_ONE_COLS - 1));
    row = Math.max(0, Math.min(row, LEVEL_ONE_ROWS - 1));

    // place on that grid cell (bottom-left of the tile)
    float tileX = xOffset + tileSize * col;
    float tileY = yOffset + tileSize * row;
    unit.setPosition(tileX, tileY);

    // Add to list of all spawned units

    // set scale to render as desired
    unit.scaleHeight(tileSize);
    spawnEntity(unit);
    robots.add(unit);
    logger.info("Unit spawned at position {} {}", col, row);
  }

  /**
   * Spawns a robot directly on top of an existing defence (placed unit) on the grid. If no defence
   * exists, does nothing and logs a warning.
   */
  public void spawnRobotOnDefence(String robotType) {
    if (grid == null) {
      logger.warn("Grid not initialised; cannot spawn robot on defence.");
      return;
    }

    int bestRow = -1, bestCol = -1;
    final int total = LEVEL_ONE_ROWS * LEVEL_ONE_COLS;

    for (int i = 0; i < total; i++) {
      int row = i / LEVEL_ONE_COLS, col = i % LEVEL_ONE_COLS;

      float cx = xOffset + tileSize * col + tileSize * 0.5f;
      float cy = yOffset + tileSize * row + tileSize * 0.5f;
      Entity tile = grid.getTileFromXY(cx, cy);

      boolean hasDefence =
          (tile != null
                  && tile.getComponent(TileStorageComponent.class) != null
                  && tile.getComponent(TileStorageComponent.class).getTileUnit() != null)
              || (i < spawned_units.length && spawned_units[i] != null);

      if (hasDefence && col > bestCol) {
        bestCol = col;
        bestRow = row;
      }
    }

    if (bestCol < 0) {
      logger.info("No defence tiles found to spawn {} robot on.", robotType);
      return;
    }

    float spawnCol = Math.min(bestCol + 0.5f, LEVEL_ONE_COLS - 0.01f); // avoid going off-map
    Entity unit = RobotFactory.createRobotType(robotType);

    float worldX = xOffset + tileSize * spawnCol;
    float worldY = yOffset + tileSize * bestRow; // same row as the defence

    unit.setPosition(worldX, worldY);
    unit.scaleHeight(tileSize);
    spawnEntity(unit);

    robots.add(unit);

    logger.info("Spawned {} robot at row={}, col+0.5={}", robotType, bestRow, spawnCol);
  }

  /**
   * Getter for selected_unit
   *
   * @return selected_unit
   */
  @Override
  public Entity getSelectedUnit() {
    return selected_unit;
  }

  /**
   * Setter for selected_unit
   *
   * @param unit Entity in the inventory
   */
  @Override
  public void setSelectedUnit(Entity unit) {
    selected_unit = unit;

    // if no star, create one
    if (selection_star == null) {
      selection_star = new Entity();
      selection_star.addComponent(new TextureRenderComponent("images/selected_star.png"));
      selection_star.scaleHeight(tileSize / 2f);
      spawnEntity(selection_star);
    }

    // if no unit selected remove star
    if (selected_unit == null) {
      selection_star.setPosition(-100f, -100f); // offscreen
      return; // break from method
    }

    // set star to correct position and size
    selection_star.setPosition(unit.getCenterPosition().x, invSelectedY);
  }

  /**
   * Adds a unit to the grid
   *
   * @param position the grid tile for spawning
   */
  @Override
  public void spawnUnit(int position) {
    // Get and set position coords
    float tileX = xOffset + tileSize * (position % LEVEL_ONE_COLS);
    float tileY = yOffset + tileSize * (float) (position / LEVEL_ONE_COLS);
    Vector2 entityPos = new Vector2(tileX, tileY);

    Supplier<Entity> entitySupplier =
        selected_unit.getComponent(InventoryUnitInputComponent.class).getEntitySupplier();
    Entity newEntity = entitySupplier.get();
    if (newEntity == null) {
      logger.error("Entity fetched was NULL");
      return;
    }
    newEntity.setPosition(entityPos);

    Entity selectedTile = grid.getTileFromXY(tileX, tileY);
    if (selectedTile != null) {
      selectedTile.getComponent(TileStorageComponent.class).setTileUnit(newEntity);
    }

    // Add to list of all spawned units
    spawned_units[position] = newEntity;
    // set scale to render as desired
    newEntity.scaleHeight(tileSize);

    spawnEntity(newEntity);
    // trigger the animation - this will change with more entities
    newEntity.getEvents().trigger("attackStart");
    newEntity
        .getEvents()
        .addListener(
            "entityDeath",
            () -> {
              requestDespawn(newEntity);
              robots.remove(newEntity);
            });
    logger.info("Unit spawned at position {}", position);
  }

  /**
   * Remove a unit form a tile
   *
   * @param position of the tile
   */
  @Override
  public void removeUnit(int position) {
    spawned_units[position].dispose();
    spawned_units[position] = null;

    logger.info("Unit deleted at position {}", position);
  }

  /**
   * Getter for tile size in world units
   *
   * @return tileSize the size of the tiles
   */
  @Override
  public float getTileSize() {
    return tileSize;
  }

  /**
   * Converts stage (mouse/screen) coordinates into world (entity placement) coordinates
   *
   * @param pos a and y coordinates in the stage
   * @return GridPoint 2 containing x and y coordinates in the world
   */
  @Override
  public GridPoint2 stageToWorld(GridPoint2 pos) {
    float x = pos.x * stageToWorldRatio;
    float y = (stageHeight - pos.y) * stageToWorldRatio;

    return new GridPoint2((int) x, (int) y);
  }

  /**
   * Converts world (entity placement) coordinates into stage (mouse/screen) coordinates
   *
   * @param pos a and y coordinates in the world
   * @return GridPoint 2 containing x and y coordinates in the stage
   */
  @Override
  public GridPoint2 worldToStage(GridPoint2 pos) {
    float x = pos.x / stageToWorldRatio;
    float y = stageHeight - (pos.y / stageToWorldRatio);
    return new GridPoint2((int) x, (int) y);
  }

  /** Method to reset game entity size/position on window resize. */
  public void resize() {
    setScaling();
  }

  /** Checks the game over condition when a robot reaches the end of the grid */
  public void checkGameOver() {
    // check if the game is already over
    if (isGameOver) {
      return; // game is already over don't check again
    }
    // calculate the robot's position
    for (Entity robot : robots) {
      Vector2 worldPos = robot.getPosition();
      int gridX = (int) ((worldPos.x - xOffset) / tileSize);

      // check if robot has reached the end
      if (gridX <= 0) {
        isGameOver = true;
        // TODO: add UI component here
        // placeholder for now
        logger.info("GAME OVER - Robot reached the left edge at grid x: {}", gridX);
        System.out.println("GAME OVER - Robot reached the left edge at grid x: " + gridX);
      }
    }
  }
}
