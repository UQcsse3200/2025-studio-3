package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.areas.terrain.MapFactory;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.currency.CurrencyGeneratorComponent;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.components.gameover.GameOverWindow;
import com.csse3200.game.components.hotbar.HotbarDisplay;
import com.csse3200.game.components.items.ItemComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.DefenceFactory;
import com.csse3200.game.entities.factories.GridFactory;
import com.csse3200.game.entities.factories.ItemFactory;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.entities.factories.RobotFactory.RobotType;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.DragOverlay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a level in the game, creates the map, a tiled grid for the playing area and a player unit
 * inventory allowing the player to add units to the grid.
 */
public class LevelGameArea extends GameArea implements AreaAPI, EnemySpawner {
  private static final Logger logger = LoggerFactory.getLogger(LevelGameArea.class);
  private static final int LEVEL_ONE_ROWS = 5;
  private static final int LEVEL_ONE_COLS = 10;
  private static final String BACKGROUND_MUSIC = "sounds/BGM_03_mp3.mp3";
  private static final String[] levelTextures = {
    "images/level-1-map-v2.png",
    "images/selected_star.png",
    "images/sling_shooter_1.png",
    "images/sling_shooter_front.png",
    "images/items/grenade.png",
    "images/items/coffee.png",
    "images/items/emp.png",
    "images/items/buff.png",
    "images/items/nuke.png",
    "images/items/shield.png",
    "images/items/charmHack.png",
    "images/items/scrapper.png",
    "images/items/conscriptionOrder.png",
    "images/items/doomHack.png",
    "images/grenade.png",
    "images/coffee.png",
    "images/emp.png",
    "images/buff.png",
    "images/nuke.png",
  };

  private static final String[] levelTextureAtlases = {
    "images/sling_shooter.atlas",
    "images/robot_placeholder.atlas",
    "images/basic_robot.atlas",
    "images/ghost.atlas",
    "images/ghostKing.atlas",
    "images/sling_shooter.atlas",
    "images/basic_robot.atlas",
    "images/grenade.atlas",
    "images/coffee.atlas",
    "images/emp.atlas",
    "images/buff.atlas",
    "images/nuke.atlas",
    "images/blue_robot.atlas",
    "images/red_robot.atlas"
  };

  private static final String[] levelSounds = {"sounds/Impact4.ogg"};
  private static final String[] levelMusic = {BACKGROUND_MUSIC};

  private final TerrainFactory terrainFactory;

  // Offset values
  private float xOffset;
  private float yOffset;
  private float tileSize;
  private float stageHeight;
  private float stageToWorldRatio;
  private LevelGameGrid grid;
  private final Entity[] spawnedUnits;
  private Entity selectedUnit;
  private boolean isGameOver = false;
  private final ArrayList<Entity> robots = new ArrayList<>();
  private Entity ui;
  private final Map<String, Supplier<Entity>> unitList = new HashMap<>();
  private final Map<String, Supplier<Entity>> itemList = new HashMap<>();

  // Initialising an Entity
  private Entity gameOverEntity;
  // Drag and drop variables
  private DragOverlay dragOverlay;
  private boolean characterSelected = false;

  /**
   * Initialise this LevelGameArea to use the provided TerrainFactory.
   *
   * @param terrainFactory TerrainFactory used to create the terrain for the GameArea.
   */
  public LevelGameArea(TerrainFactory terrainFactory) {
    super();
    setScaling();

    this.terrainFactory = terrainFactory;
    selectedUnit = null; // None selected at level load
    spawnedUnits = new Entity[LEVEL_ONE_ROWS * LEVEL_ONE_COLS];
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
  }

  /** Creates the game area by calling helper methods as required. */
  @Override
  public void create() {
    loadAssets();

    displayUI();

    spawnMap(1);
    spawnSun();
    spawnGrid(LEVEL_ONE_ROWS, LEVEL_ONE_COLS);

    Entity overlayEntity = new Entity();
    dragOverlay = new DragOverlay(this);
    overlayEntity.addComponent(dragOverlay);
    spawnEntity(overlayEntity);

    spawnRobot(7, 2, RobotType.TANKY);
    spawnRobot(10, 1, RobotType.STANDARD);
    spawnRobot(10, 4, RobotType.FAST);

    playMusic();
  }

  /** Uses the {@link ResourceService} to load the assets for the level. */
  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(levelTextures);
    resourceService.loadTextureAtlases(levelTextureAtlases);
    resourceService.loadSounds(levelSounds);
    resourceService.loadMusic(levelMusic);
    resourceService.loadAll();

    while (!resourceService.loadForMillis(10)) {
      // This could be upgraded to a loading screen
      logger.info("Loading... {}%", resourceService.getProgress());
    }
  }

  /** Spawns the level UI */
  private void displayUI() {
    ui = new Entity();
    // add components here for additional UI Elements
    unitList.put(
        "images/sling_shooter_front.png",
        () -> DefenceFactory.createSlingShooter(new ArrayList<>()));

    for (String itemKey :
        ServiceLocator.getProfileService().getProfile().getInventory().getKeys()) {
      if (itemKey.equals("grenade")) {
        itemList.put("images/items/grenade.png", ItemFactory::createGrenade);
      }
      if (itemKey.equals("coffee")) {
        itemList.put("images/items/coffee.png", ItemFactory::createCoffee);
      }
      if (itemKey.equals("buff")) {
        itemList.put("images/items/buff.png", ItemFactory::createBuff);
      }
      if (itemKey.equals("emp")) {
        itemList.put("images/items/emp.png", ItemFactory::createEmp);
      }
      if (itemKey.equals("nuke")) {
        itemList.put("images/items/nuke.png", ItemFactory::createNuke);
      }
    }

    ui.addComponent(new GameAreaDisplay("Level One"))
        .addComponent(new HotbarDisplay(this, tileSize, unitList, itemList));

    spawnEntity(ui);

    // Creates a game over entity to handle the game over window UI
    this.gameOverEntity = new Entity();
    gameOverEntity.addComponent(new GameOverWindow());
    spawnEntity(this.gameOverEntity);
  }

  /** Creates the map in the {@link TerrainFactory} and spawns it in the correct position. */
  /** Creates the map as a single image using MapFactory and spawns it in the correct position. */
  private void spawnMap(int level) {
    logger.debug("Spawning level one map");

    // Use MapFactory for single-entry map creation
    MapFactory mapFactory = new MapFactory(terrainFactory);
    Entity mapEntity = mapFactory.createLevelMap(level);

    if (mapEntity != null) {
      spawnEntity(mapEntity);
    }
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
      int col = i / cols;
      float tileY = yOffset + tileSize * col;

      tile = GridFactory.createTile(tileSize, tileX, tileY, this);
      tile.setPosition(tileX, tileY);
      tile.getComponent(TileStorageComponent.class).setPosition(i);
      grid.addTile(i, tile);
      spawnEntity(tile);
    }
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
    ServiceLocator.getResourceService().getAsset(BACKGROUND_MUSIC, Music.class).stop();
    this.unloadAssets();
  }

  /** Starts the music */
  private void playMusic() {
    Music music = ServiceLocator.getResourceService().getAsset(BACKGROUND_MUSIC, Music.class);
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

  public void spawnRobot(int col, int row, RobotType robotType) {
    Entity unit = RobotFactory.createRobotType(robotType);

    // Get and set position coords
    col = Math.clamp(col, 0, LEVEL_ONE_COLS - 1);
    row = Math.clamp(row, 0, LEVEL_ONE_ROWS - 1);

    // place on that grid cell (bottom-left of the tile)
    float tileX = xOffset + tileSize * col;
    float tileY = yOffset + tileSize * row;

    unit.setPosition(tileX, tileY);

    // Add to list of all spawned units

    // set scale to render as desired
    unit.scaleHeight(tileSize);
    spawnEntity(unit);
    robots.add(unit);
    unit.getEvents()
        .addListener(
            "entityDeath",
            () -> {
              requestDespawn(unit);
              // Persistence.addCoins(3); //commented out since broken
              robots.remove(unit);
            });
    logger.info("Unit spawned at position {} {}", col, row);
  }

  /**
   * Spawns a robot directly on top of an existing defence (placed unit) on the grid. If no defence
   * exists, does nothing and logs a warning.
   */
  public void spawnRobotOnDefence(RobotType robotType) {
    if (grid == null) {
      logger.warn("Grid not initialised; cannot spawn robot on defence.");
      return;
    }

    int bestRow = -1;
    int bestCol = -1;

    final int total = LEVEL_ONE_ROWS * LEVEL_ONE_COLS;

    for (int i = 0; i < total; i++) {
      int row = i / LEVEL_ONE_COLS;
      int col = i % LEVEL_ONE_COLS;

      float cx = xOffset + tileSize * col + tileSize * 0.5f;
      float cy = yOffset + tileSize * row + tileSize * 0.5f;
      Entity tile = grid.getTileFromXY(cx, cy);

      boolean hasDefence =
          (tile != null
                  && tile.getComponent(TileStorageComponent.class) != null
                  && tile.getComponent(TileStorageComponent.class).getTileUnit() != null)
              || (i < spawnedUnits.length && spawnedUnits[i] != null);

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
    return selectedUnit;
  }

  /**
   * Setter for selected_unit
   *
   * @param unit Entity in the inventory
   */
  @Override
  public void setSelectedUnit(Entity unit) {
    selectedUnit = unit;
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
    int row = position / LEVEL_ONE_COLS; // line required to make Sonarqube happy
    float tileY = yOffset + tileSize * row;
    Vector2 entityPos = new Vector2(tileX, tileY);

    Supplier<Entity> entitySupplier =
        selectedUnit.getComponent(DeckInputComponent.class).getEntitySupplier();
    Entity newEntity = entitySupplier.get();
    if (newEntity == null) {
      logger.error("Entity fetched was NULL");
      return;
    }
    newEntity.setPosition(entityPos);

    // Get the tile at the spawn coordinates
    Entity selectedTile = grid.getTileFromXY(tileX, tileY);

    // Where entity to be spawned is an Item and the player has such item in their inventory
    ItemComponent item = newEntity.getComponent(ItemComponent.class);
    if (item != null
        && !ServiceLocator.getProfileService()
            .getProfile()
            .getInventory()
            .contains(item.getType().toString().toLowerCase(Locale.ROOT))) {
      // Clear Item from tile storage
      selectedTile.getComponent(TileStorageComponent.class).removeTileUnit();
      String itemType = item.getType().toString();
      logger.info("Not spawning item {} since none in player's inventory", itemType);
      return;
    }
    if (item != null && selectedTile != null) {
      String itemType = item.getType().toString();
      logger.info("Spawning item {}", itemType);
      String key = item.getType().toString().toLowerCase(Locale.ROOT);

      // Remove one instance of the Item from the inventory
      ServiceLocator.getProfileService().getProfile().getInventory().removeItem(key);

      // Spawn effect
      // Currently just effect displays, not entity itself then effect after a delay
      Vector2 spawnPosition = new Vector2(tileX, tileY);
      ServiceLocator.getItemEffectsService()
          .playEffect(
              key,
              spawnPosition,
              (int) tileSize,
              new Vector2(
                  (float) (xOffset * 0.25 + LEVEL_ONE_COLS * tileSize),
                  (float) (tileSize * -0.75)));

      // ~ HANDLE DAMAGING ROBOTS (WHEN APPLICABLE) ~
      Set<String> damagingItems = Set.of("GRENADE", "EMP", "NUKE");
      if (damagingItems.contains(item.getType().toString())) {
        // Window query (3x3)
        float radius = 1.5f * tileSize;

        List<Entity> toRemove = new ArrayList<>(); // targets
        for (Entity r : robots) {
          Vector2 pos = r.getPosition();
          if (Math.abs(entityPos.x - pos.x) <= radius && Math.abs(entityPos.y - pos.y) <= radius) {
            // for logger
            int grenadeCol = (int) ((entityPos.x - xOffset) / tileSize);
            int grenadeRow = (int) ((entityPos.y - yOffset) / tileSize);
            int robotCol = (int) ((pos.x - xOffset) / tileSize);
            int robotRow = (int) ((pos.y - yOffset) / tileSize);
            logger.info(
                "Grenade at ({}, {}) hits robot at ({}, {})",
                grenadeCol,
                grenadeRow,
                robotCol,
                robotRow);
            toRemove.add(r);
          }
        }
        // can't remove from a list while iterating through it
        for (Entity r : toRemove) {
          // trigger entityDeath does NOT work
          requestDespawn(r);
          robots.remove(r);
        }
      }

      // Clear Item from tile storage
      selectedTile.getComponent(TileStorageComponent.class).removeTileUnit();
      return;
    }

    // Add entity to tile unless it is an Item
    if (selectedTile != null) {
      selectedTile.getComponent(TileStorageComponent.class).setTileUnit(newEntity);
    }

    // Add to list of all spawned units
    spawnedUnits[position] = newEntity;
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
    spawnedUnits[position].dispose();
    spawnedUnits[position] = null;

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
      if (gridX <= -1) {
        isGameOver = true;
        logger.info("GAME OVER - Robot reached the left edge at grid x: {}", gridX);
        // Window activation trigger
        gameOverEntity.getEvents().trigger("gameOver");
      }
    }
  }

  /**
   * Begins a drag operation with the given texture.
   *
   * @param texture the texture to display while dragging
   */
  @Override
  public void beginDrag(Texture texture) {
    if (dragOverlay != null && texture != null) {
      dragOverlay.begin(texture);
    }
  }

  /** Cancels an ongoing drag operation. */
  @Override
  public void cancelDrag() {
    if (dragOverlay != null) {
      dragOverlay.cancel();
    }
  }

  /**
   * Sets whether a character is currently selected.
   *
   * @param status true to indicate a character is selected, false otherwise
   */
  public void setIsCharacterSelected(boolean status) {
    this.characterSelected = status;
  }

  /**
   * Checks if a character is currently selected.
   *
   * @return true if a character is selected, false otherwise
   */
  public boolean isCharacterSelected() {
    return characterSelected;
  }
}
