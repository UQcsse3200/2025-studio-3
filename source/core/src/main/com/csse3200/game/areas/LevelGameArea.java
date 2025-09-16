package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.InventoryUnitInputComponent;
import com.csse3200.game.components.currency.CurrencyGeneratorComponent;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.GridFactory;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.csse3200.game.areas.terrain.MapFactory;

/**
 * Creates a level in the game, creates the map, a tiled grid for the playing area and a player unit
 * inventory allowing the player to add units to the grid.
 */
public class LevelGameArea extends GameArea implements AreaAPI {
  private static final Logger logger = LoggerFactory.getLogger(LevelGameArea.class);

  private static final int LEVEL_ONE_ROWS = 5;
  private static final int LEVEL_ONE_COLS = 10;

  private final TerrainFactory terrainFactory;
  private static final String CURRENT_MAP = "level1";

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
    MapFactory.loadAssets(CURRENT_MAP);

    displayUI();

    spawnMap();
    spawnSun();
    spawnGrid(LEVEL_ONE_ROWS, LEVEL_ONE_COLS);
    spawnRobot(7, 2, "tanky");
    spawnRobot(10, 1, "standard");
    spawnRobot(10, 4, "fast");
    spawnInventory();

    playMusic();
  }

  /** Spawns the level UI */
  private void displayUI() {
    Entity ui = new Entity();

    ui.addComponent(new GameAreaDisplay("Level One"));
    spawnEntity(ui);
  }

  /** Creates the map from MapFactory. */
  private void spawnMap() {
    logger.debug("Spawning map: {}", CURRENT_MAP);
    Entity mapEntity = MapFactory.createMap(CURRENT_MAP, terrainFactory);

    // Position the map appropriately - you might need to adjust this based on your game layout
    mapEntity.setPosition(0, 0);
    spawnEntity(mapEntity);
  }

  /** Determines inventory units to spawn for the level and calls method to place them. */
  private void spawnInventory() {
    placeInventoryUnit(1, "images/ghost_1.png"); // start at one for 0 to represent none selected
    placeInventoryUnit(2, "images/ghost_king.png");
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
   * Creates and Spawns the Units in the inventory
   *
   * @param pos the position of the unit in the inventory, pos >= 1
   * @param image the file path of the unit image
   */
  private void placeInventoryUnit(int pos, String image) {
    Entity unit =
        new Entity()
            .addComponent(new InventoryUnitInputComponent(this))
            .addComponent(new TextureRenderComponent(image));
    unit.setPosition(invStartX + (pos - 1) * (tileSize * 1.5f), invY);
    unit.scaleHeight(tileSize);
    spawnEntity(unit);
  }

  /** Extends the super method to stop music and unload assets. */
  @Override
  public void dispose() {
    super.dispose();
    ServiceLocator.getResourceService()
        .getAsset(MapFactory.getBackgroundMusic(CURRENT_MAP), Music.class)
        .stop();
    MapFactory.unloadAssets(CURRENT_MAP);
  }

  /** Starts the music */
  private void playMusic() {
    Music music =
        ServiceLocator.getResourceService()
            .getAsset(MapFactory.getBackgroundMusic(CURRENT_MAP), Music.class);
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

  public void spawnRobot(int x, int y, String robotType) {
    Entity unit = RobotFactory.createRobotType(robotType);

    // Get and set position coords
    float tileX = xOffset + tileSize * (x % (LEVEL_ONE_COLS + 10));
    float tileY = yOffset + tileSize * (float) (y % LEVEL_ONE_COLS);
    unit.setPosition(tileX, tileY);

    unit.scaleHeight(tileSize);
    spawnEntity(unit);
    logger.info("Unit spawned at position {} {}", x, y);
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
    Entity unit = new Entity();

    // Match the texture of the inventory unit - placeholder
    Texture texture = selected_unit.getComponent(TextureRenderComponent.class).getTexture();
    unit.addComponent(new TextureRenderComponent(texture));

    // Get and set position coords
    float tileX = xOffset + tileSize * (position % LEVEL_ONE_COLS);
    float tileY = yOffset + tileSize * (float) (position / LEVEL_ONE_COLS);
    unit.setPosition(tileX, tileY);

    Entity selectedTile = grid.getTileFromXY(tileX, tileY);
    if (selectedTile != null) {
      selectedTile.getComponent(TileStorageComponent.class).setTileUnit(unit);
    }

    // Add to list of all spawned units
    spawned_units[position] = unit;

    // set scale to render as desired
    unit.getComponent(TextureRenderComponent.class).scaleEntity();
    unit.scaleHeight(tileSize);

    spawnEntity(unit);
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
}