package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.GeneratorStatsComponent;
import com.csse3200.game.components.LevelCompleted.LevelCompletedWindow;
import com.csse3200.game.components.ProjectileComponent;
import com.csse3200.game.components.ProjectileTagComponent;
import com.csse3200.game.components.currency.CurrencyGeneratorComponent;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.components.gameover.GameOverWindow;
import com.csse3200.game.components.hotbar.HotbarDisplay;
import com.csse3200.game.components.items.ItemComponent;
import com.csse3200.game.components.projectiles.MoveDirectionComponent;
import com.csse3200.game.components.projectiles.PhysicsProjectileComponent;
import com.csse3200.game.components.tasks.TargetDetectionTasks;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.ProjectileType;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.factories.*;
import com.csse3200.game.entities.factories.RobotFactory.RobotType;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.rendering.BackgroundMapComponent;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.DragOverlay;
import java.util.*;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a level in the game, creates the map, a tiled grid for the playing area and a player unit
 * inventory allowing the player to add units to the grid.
 */
public class LevelGameArea extends GameArea implements AreaAPI, EnemySpawner {
  private static final float X_MARGIN_TILES = 2f;
  private static final float Y_MARGIN_TILES = 1f;
  private static final float MAP_HEIGHT_TILES = 8f;
  private static final String ENTITY_DEATH_EVENT = "entityDeath";
  private static final String HEAL = "heal";
  private static final Logger logger = LoggerFactory.getLogger(LevelGameArea.class);
  private float xOffset;
  private float yOffset;
  private float tileSize;
  private float stageHeight;
  private float stageToWorldRatio;
  private LevelGameGrid grid;
  private Entity selectedUnit;
  private final ArrayList<Entity> robots = new ArrayList<>();
  private final Map<String, Supplier<Entity>> unitList = new HashMap<>();
  private final Map<String, Supplier<Entity>> itemList = new HashMap<>();

  Entity gameOverEntity;
  private Entity levelCompleteEntity;
  private boolean isLevelComplete = false;
  private boolean isGameOver = false;

  // Drag and drop variables
  private DragOverlay dragOverlay;
  private boolean characterSelected = false;

  // Level configuration
  private final String currentLevelKey;
  private int levelRows = 5; // Default fallback
  private int levelCols = 10; // Default fallback
  private float worldWidth; // background map world width
  private String mapFilePath; // from level config
  private final ItemHandler itemHandler = new ItemHandler(this);
  private final WavePreviewManager wavePreview = new WavePreviewManager(this);

  private static final List<String> levelOrder =
      List.of("levelOne", "levelTwo", "levelThree", "levelFour", "levelFive");

  /**
   * Initialise this LevelGameArea for a specific level.
   *
   * @param levelKey the level key to load
   */
  public LevelGameArea(String levelKey) {
    super();
    this.currentLevelKey = levelKey != null ? levelKey : "levelOne";
    loadLevelConfiguration(); // rows, cols, and mapFilePath
    setScaling();
    selectedUnit = null;

    ServiceLocator.getDiscordRichPresenceService()
        .updateGamePresence(currentLevelKey.split("level")[1], 1);
  }

  /** Loads level configuration from ConfigService. */
  private void loadLevelConfiguration() {
    logger.debug(
        "[LevelGameArea] Attempting to load configuration for level key: '{}'", currentLevelKey);
    ConfigService configService = ServiceLocator.getConfigService();

    if (configService == null) {
      logger.error("[LevelGameArea] ConfigService is null!");
      return;
    }

    BaseLevelConfig levelConfig = configService.getLevelConfig(currentLevelKey);

    if (levelConfig != null) {
      levelRows = levelConfig.getRows();
      levelCols = levelConfig.getCols();
      mapFilePath = levelConfig.getMapFile(); // add this
      if (mapFilePath == null || mapFilePath.isEmpty()) {
        mapFilePath = "images/backgrounds/level_map_grass.png";
      }
      logger.info(
          "[LevelGameArea] Loaded level {} configuration: {}x{} grid",
          currentLevelKey,
          levelRows,
          levelCols);
    } else {
      logger.warn(
          "[LevelGameArea] Could not load configuration for level {}, using defaults: {}x{}",
          currentLevelKey,
          levelRows,
          levelCols);
    }
  }

  /**
   * Uses stage height and width to set variables relating to tile, grid and character sizing and
   * placement.
   */
  public void setScaling() {
    stageHeight = ServiceLocator.getRenderService().getStage().getHeight();
    float stageWidth = ServiceLocator.getRenderService().getStage().getWidth();
    stageToWorldRatio = Renderer.GAME_SCREEN_WIDTH / stageWidth;

    // Camera viewport height in world units:
    float viewportHeight = stageHeight * stageToWorldRatio;
    tileSize = viewportHeight / MAP_HEIGHT_TILES;
    xOffset = X_MARGIN_TILES * tileSize;
    yOffset = Y_MARGIN_TILES * tileSize;
  }

  /** Creates the game area by calling helper methods as required. */
  @Override
  public void create() {
    displayUI();
    spawnMap();
    spawnGrid(levelRows, levelCols);
    Entity overlayEntity = new Entity();
    dragOverlay = new DragOverlay(this);
    overlayEntity.addComponent(dragOverlay);
    spawnEntity(overlayEntity);
  }

  /** Spawns the level UI, including hotbar, item/defence lists, and game-over window. */
  protected void displayUI() {
    Profile profile = ServiceLocator.getProfileService().getProfile();
    ConfigService configService = ServiceLocator.getConfigService();

    populateUnitList(profile, configService);
    populateItemList(profile.getInventory(), configService);

    Entity ui =
        new Entity()
            .addComponent(new GameAreaDisplay("Level One"))
            .addComponent(new HotbarDisplay(this, tileSize, unitList, itemList));
    spawnEntity(ui);

    createGameOverEntity();

    // Handles the level completion window UI
    this.levelCompleteEntity = new Entity();
    levelCompleteEntity.addComponent(new LevelCompletedWindow());
    spawnEntity(this.levelCompleteEntity);
  }

  /** Unlocks all entities that are listed as playing on the current game level */
  private void unlockAllEntities(Profile profile) {
    for (String level : levelOrder) {
      for (String key : Arsenal.ALL_DEFENCES.keySet()) {
        if (Arsenal.ALL_DEFENCES.get(key).equals(level) && !profile.getArsenal().contains(key)) {
          profile.getArsenal().unlockDefence(key);
        }
      }
      for (String key : Arsenal.ALL_GENERATORS.keySet()) {
        if (Arsenal.ALL_GENERATORS.get(key).equals(level) && !profile.getArsenal().contains(key)) {
          profile.getArsenal().unlockGenerator(key);
        }
      }
      if (level.equals(currentLevelKey)) {
        break;
      }
    }
  }

  /** Populates unitList with all available defenders and generators from the player's arsenal. */
  private void populateUnitList(Profile profile, ConfigService configService) {
    unlockAllEntities(profile);
    for (String defenceKey : profile.getArsenal().getDefenders()) {
      BaseDefenderConfig config = configService.getDefenderConfig(defenceKey);
      if (config != null) {
        unitList.put(config.getAssetPath(), () -> DefenceFactory.createDefenceUnit(config));
      } else {
        logger.warn("Missing defender config for key {}", defenceKey);
      }
    }

    for (String generatorKey : profile.getArsenal().getGenerators()) {
      BaseGeneratorConfig config = configService.getGeneratorConfig(generatorKey);
      if (config != null) {
        unitList.put(config.getAssetPath(), () -> GeneratorFactory.createGeneratorUnit(config));
      } else {
        logger.warn("Missing generator config for key {}", generatorKey);
      }
    }
  }

  /** Populates itemList for all items in inventory that have corresponding configs. */
  private void populateItemList(Inventory inventory, ConfigService configService) {
    Map<String, Supplier<Entity>> itemFactories =
        Map.of(
            "grenade", ItemFactory::createGrenade,
            "coffee", ItemFactory::createCoffee,
            "buff", ItemFactory::createBuff,
            "emp", ItemFactory::createEmp,
            "nuke", ItemFactory::createNuke);

    for (Map.Entry<String, Supplier<Entity>> entry : itemFactories.entrySet()) {
      String key = entry.getKey();
      if (inventory.contains(key)) {
        BaseItemConfig config = configService.getItemConfig(key);
        if (config != null && config.getAssetPath() != null) {
          itemList.put(config.getAssetPath(), entry.getValue());
        } else {
          logger.warn("Item config missing or invalid for {}", key);
        }
      }
    }
  }

  /** Creates and spawns the game-over UI entity. */
  void createGameOverEntity() {
    gameOverEntity = new Entity().addComponent(new GameOverWindow());
    spawnEntity(gameOverEntity);
  }

  /** Creates and spawns the game background map and its boundary wall. */
  private void spawnMap() {
    Texture texture = loadMapTexture(mapFilePath);
    float viewportHeight = stageHeight * stageToWorldRatio;

    Entity map = new Entity();
    map.addComponent(new BackgroundMapComponent(texture, viewportHeight));
    map.setPosition(0f, 0f);

    spawnEntity(map);

    // Cache world width from component for camera/clamping
    this.worldWidth = map.getComponent(BackgroundMapComponent.class).getWorldWidth();

    spawnWall();
  }

  /** Spawns a static defensive wall at the left edge of the map. */
  void spawnWall() {
    float tileY = yOffset - tileSize / 5;
    float wallSize = tileSize * 6;

    Entity wall = DefenceFactory.createWall();
    wall.setPosition(xOffset + tileSize * -1, tileY);
    wall.scaleHeight(wallSize);

    spawnEntity(wall);
    wall.getEvents().trigger("idleStart");
  }

  private void spawnScrap(Entity entity) {
    Entity scrapSpawner = new Entity();
    CurrencyGeneratorComponent currencyGenerator =
        new CurrencyGeneratorComponent(entity, "images/entities/currency/scrap_metal.png");
    scrapSpawner.addComponent(currencyGenerator);
    // if furnace dies, dispose of its currency generator
    entity.getEvents().addListener(ENTITY_DEATH_EVENT, scrapSpawner::dispose);
    spawnEntity(scrapSpawner);
  }

  /** Creates the tiled grid for the playable area. */
  private void spawnGrid(int rows, int cols) {
    grid = new LevelGameGrid(rows, cols);

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        float tileX = xOffset + tileSize * col;
        float tileY = yOffset + tileSize * row;

        Entity tile = GridFactory.createTile(tileSize, tileX, tileY, this);
        tile.setPosition(tileX, tileY);

        tile.getComponent(TileStorageComponent.class).setPosition(row * cols + col);

        grid.addTile(row * cols + col, tile);
        spawnEntity(tile);
      }
    }
  }

  /** Loads a map texture safely, ensuring it exists in the resource service. */
  private Texture loadMapTexture(String path) {
    Texture tex = ServiceLocator.getResourceService().getAsset(path, Texture.class);
    if (tex == null) {
      logger.warn("Map texture '{}' not preloaded, loading dynamically.", path);
      ServiceLocator.getResourceService().loadTextures(new String[] {path});
      ServiceLocator.getResourceService().loadAll();
      tex = ServiceLocator.getResourceService().getAsset(path, Texture.class);
    }
    return tex;
  }

  public void spawnRobot(int col, int row, RobotType robotType) {
    if (robotType == RobotType.BUNGEE) {
      spawnRobotOnDefence(robotType);
      return;
    }

    col = Math.clamp(col, 0, levelCols - 1);
    row = Math.clamp(row, 0, levelRows - 1);

    Vector2 pos = tileToWorld(col, row);
    registerRobot(robotType, pos.x, pos.y);

    logger.info("Robot {} spawned at position {} {}", robotType, row, col);
  }

  /**
   * Spawns a robot directly on top of an existing defence (placed unit) on the grid. If no defence
   * exists, does nothing and logs a warning.
   */
  public void spawnRobotOnDefence(RobotFactory.RobotType robotType) {
    if (grid == null) {
      logger.warn("Grid not initialised; cannot spawn robot on defence.");
      return;
    }

    GridPoint2 rc = findRightmostDefenceCell(); // x = col, y = row
    if (rc == null) {
      logger.info("No defence tiles found to spawn {} robot on.", robotType);
      return;
    }

    int bestCol = rc.x;
    int bestRow = rc.y;
    int cols = grid.getCols();

    // Place slightly to the right of the right-most defence, but clamp inside the map
    float spawnCol = Math.min(bestCol + 0.5f, cols - 0.01f);
    float worldX = xOffset + tileSize * spawnCol;
    float worldY = yOffset + tileSize * bestRow;

    registerRobot(robotType, worldX, worldY);
    logger.info("Spawned {} robot at row={}, col+0.5={}", robotType, bestRow, spawnCol);
  }

  /** Convert tile grid coordinates to world coordinates (bottom-left of tile). */
  private Vector2 tileToWorld(int col, int row) {
    float x = xOffset + tileSize * col;
    float y = yOffset + tileSize * row;
    return new Vector2(x, y);
  }

  /** Shared creation, placement, scaling, spawning, and listener wiring for robots. */
  private void registerRobot(RobotType type, float worldX, float worldY) {
    Entity unit = RobotFactory.createRobotType(type);
    unit.setPosition(worldX, worldY);
    unit.scaleHeight(tileSize);

    spawnEntity(unit);
    robots.add(unit);

    unit.getEvents()
        .addListener(
            ENTITY_DEATH_EVENT,
            () -> {
              requestDespawn(unit);
              ServiceLocator.getWaveService().onEnemyDispose();
              robots.remove(unit);
            });

    // Keep list in sync if something else despawns the robot
    unit.getEvents().addListener("despawned", () -> robots.remove(unit));
  }

  /**
   * Finds the right-most occupied grid cell (i.e., a placed defence).
   *
   * @return GridPoint2(col, row), or null if none exist.
   */
  private GridPoint2 findRightmostDefenceCell() {
    int rows = grid.getRows();
    int cols = grid.getCols();
    int total = rows * cols;

    int bestCol = -1;
    int bestRow = -1;

    for (int i = 0; i < total; i++) {
      Entity occ = grid.getOccupantIndex(i);
      if (occ == null) continue;

      int row = i / cols;
      int col = i % cols;
      if (col > bestCol) {
        bestCol = col;
        bestRow = row;
      }
    }
    return (bestCol >= 0) ? new GridPoint2(bestCol, bestRow) : null;
  }

  public void spawnProjectile(
      Vector2 spawnPos, Entity projectile, TargetDetectionTasks.AttackDirection direction) {
    // Safety check
    if (projectile == null || spawnPos == null || direction == null) {
      logger.warn("Invalid projectile spawn parameters");
      return;
    }

    projectile.setPosition(spawnPos.x + tileSize / 2f + 1f, spawnPos.y + tileSize / 2f - 5f);
    ProjectileTagComponent tag = projectile.getComponent(ProjectileTagComponent.class);

    // Scale the projectile so it’s more visible
    float size = 30f;
    if (tag.getType() == ProjectileType.HARPOON_PROJECTILE) {
      projectile.setPosition(spawnPos.x + tileSize / 2f + 1f, spawnPos.y + tileSize / 4f);
      size = 100f;
    }
    projectile.scaleHeight(size); // set the height in world units
    projectile.scaleWidth(size); // set the width in world units

    if (tag.getType() == ProjectileType.SHELL) {
      Random random = new Random();
      int col = (int) ((spawnPos.x - xOffset) / tileSize);
      int maxRange = 9 - col;
      int num = random.nextInt(maxRange - 1) + 2; // pick random num between 2 and 7
      projectile.addComponent(new PhysicsProjectileComponent(num * tileSize, direction));

      projectile
          .getEvents()
          .addListener(
              "despawnShell",
              e -> {
                Vector2 pos = projectile.getPosition();
                int damage = 5; // or configurable
                float radius = tileSize; // 1 tile radius
                damageRobotsAtPosition(pos, radius, damage);
              });
    } else {
      projectile.addComponent(new MoveDirectionComponent(direction)); // pass velocity
    }
    if (tag.getType() != ProjectileType.HARPOON_PROJECTILE
        && tag.getType() != ProjectileType.SHELL) {
      projectile.getEvents().addListener("despawnSlingshot", this::requestDespawn);
    }
    spawnEntity(projectile); // adds to area and entity service
  }

  /**
   * Deal damage to all robots in a circular area around the given world position.
   *
   * @param landingPos The world coordinates where the projectile landed
   * @param radius Radius of effect in world units (e.g., 1 tile = tileSize)
   * @param damage Amount of damage to apply
   */
  public void damageRobotsAtPosition(Vector2 landingPos, float radius, int damage) {
    if (robots.isEmpty()) return;

    List<Entity> robotsToRemove = new ArrayList<>();

    for (Entity robot : robots) {
      CombatStatsComponent stats = robot.getComponent(CombatStatsComponent.class);
      if (stats == null) continue;

      Vector2 robotPos = robot.getPosition();
      float dx = robotPos.x - landingPos.x;
      float dy = robotPos.y - landingPos.y;
      float distanceSq = dx * dx + dy * dy;

      if (distanceSq <= radius * radius) {
        // Apply damage by subtracting health
        stats.addHealth(-damage);

        logger.info(
            "Mortar shell hit robot at ({}, {}) for {} damage", robotPos.x, robotPos.y, damage);

        // Mark robot for removal if dead
        boolean mark = stats.isDead();
        if (mark) {
          robotsToRemove.add(robot);
        }
      }
    }

    // Despawn dead robots
    for (Entity r : robotsToRemove) {
      requestDespawn(r);
      robots.remove(r);
    }
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
    // --- Step 1: Resolve grid/tile/selected entity ---
    Entity tile = grid.getTile(position);

    if (tile == null) {
      logger.warn("No tile entity found at index {}", position);
      return;
    }

    Entity newEntity = getSelectedEntity();
    if (newEntity == null) {
      logger.warn("No selected entity to spawn");
      return;
    }

    Vector2 worldPos = resolveWorldPosition(position);
    newEntity.setPosition(worldPos);
    if ("mortar".equals(newEntity.getProperty("unitType")) && worldPos.x >= 1000) {
      return;
    }

    // --- Step 2: Item path ---
    if (trySpawnItem(newEntity, tile, worldPos)) {
      resetSelectionUI();
      return;
    }

    // --- Step 3: Defence / Generator path ---
    if (grid.isOccupiedIndex(position)) {
      logger.info("Tile {} already occupied", position);
      resetSelectionUI();
      return;
    }

    placeDefenceUnit(position, tile, newEntity, worldPos);
    resetSelectionUI();
  }

  /** Safely obtain the selected entity's supplier and create it. */
  private Entity getSelectedEntity() {
    if (selectedUnit == null) return null;
    DeckInputComponent deck = selectedUnit.getComponent(DeckInputComponent.class);
    return (deck != null) ? deck.getEntitySupplier().get() : null;
  }

  /** Reset drag state and selection after any placement. */
  private void resetSelectionUI() {
    setIsCharacterSelected(false);
    setSelectedUnit(null);
    cancelDrag();
  }

  /** Convert a tile index into its world position. */
  private Vector2 resolveWorldPosition(int position) {
    int col = position % levelCols;
    int row = position / levelCols;
    return new Vector2(xOffset + tileSize * col, yOffset + tileSize * row);
  }

  /** Adds 20 health points to all placed defences and generators on the grid. */
  private void healDefences() {
    if (grid == null) {
      logger.warn("Grid not initialised; cannot heal defences.");
      return;
    }

    final int rows = grid.getRows();
    final int cols = grid.getCols();
    final int total = rows * cols;

    // Find all occupied cells (a placed defence or generator)
    for (int i = 0; i < total; i++) {
      Entity occ = grid.getOccupantIndex(i);
      if (occ == null) continue;

      logger.info("Healing entity at grid index {}", i);
      occ.getEvents().trigger(HEAL);
    }
  }

  /**
   * Handle spawning an item (grenade, coffee, etc.) if applicable. Returns true if an item was
   * spawned and handled.
   */
  private boolean trySpawnItem(Entity entity, Entity tile, Vector2 worldPos) {
    ItemComponent item = entity.getComponent(ItemComponent.class);
    if (item == null) return false;

    String itemKey = item.getType().toString().toLowerCase(Locale.ROOT);
    Inventory inv = ServiceLocator.getProfileService().getProfile().getInventory();

    if (!inv.contains(itemKey)) {
      logger.info("Cannot spawn item {}, not in player's inventory", itemKey);
      tile.getComponent(TileStorageComponent.class).removeTileUnit();
      return true; // handled: do nothing
    }

    itemHandler.handleItemUse(item, worldPos);
    tile.getComponent(TileStorageComponent.class).removeTileUnit();
    return true;
  }

  /**
   * Place a non-item (defence or generator) unit on the grid, wire its events, and spawn into the
   * world.
   */
  private void placeDefenceUnit(int position, Entity tile, Entity unit, Vector2 worldPos) {
    tile.getComponent(TileStorageComponent.class).setTileUnit(unit);
    unit.scaleHeight(tileSize);

    if (unit.getComponent(GeneratorStatsComponent.class) != null) {
      if (unit.getComponent(GeneratorStatsComponent.class).getInterval() > 0) {
        spawnScrap(unit);
      } else {
        // healer entity, no scrap & kills itself after one animation cycle
        logger.info("Healer placed");
        healDefences();
        // remove the healer after its animation
        ServiceLocator.getRenderService()
            .getStage()
            .addAction(
                Actions.sequence(Actions.delay(2.75f), Actions.run(() -> removeUnit(position))));
      }
    }

    spawnEntity(unit);
    unit.getEvents().trigger("idleStart");

    Runnable clearTile =
        () -> {
          grid.removeOccupantIfMatchIndex(position, unit);
          tile.getComponent(TileStorageComponent.class).removeTileUnit();
        };

    unit.getEvents()
        .addListener(
            ENTITY_DEATH_EVENT,
            () -> {
              requestDespawn(unit);
              clearTile.run();
              robots.remove(unit);
            });
    unit.getEvents().addListener("despawned", clearTile::run);

    unit.getEvents()
        .addListener(
            "fire",
            (TargetDetectionTasks.AttackDirection dir) -> {
              if (unit.getComponent(ProjectileComponent.class) != null) {
                spawnProjectile(
                    worldPos, unit.getComponent(ProjectileComponent.class).getProjectile(), dir);
              }
              unit.getEvents().trigger("attackStart");
            });

    // play appropriate sound
    try {
      String soundPath = unit.getProperty("soundPath").toString();
      Sound sound = ServiceLocator.getResourceService().getAsset(soundPath, Sound.class);
      float volume = ServiceLocator.getSettingsService().getSoundVolume();
      sound.play(volume);
      logger.info("Playing sound: {}", soundPath);
    } catch (Exception e) {
      logger.info("No soundPath property found on this entity");
    }

    logger.info(
        "Unit spawned at position {} (r={}, c={})",
        position,
        position / levelCols,
        position % levelCols);
  }

  /**
   * Remove a unit form a tile
   *
   * @param position of the tile
   */
  @Override
  public void removeUnit(int position) {
    Entity occ = grid.getOccupantIndex(position);
    if (occ == null) {
      logger.info("No unit at position {}", position);
      return;
    }
    occ.getEvents().trigger("entityDespawn");
    requestDespawn(occ);
    grid.clearOccupantIndex(position);
    // Also clear the tile component (delegates to grid, stays in sync)
    Entity tile = grid.getTile(position);
    if (tile != null) {
      tile.getComponent(TileStorageComponent.class).removeTileUnit();
    }
    logger.info("Unit deleted at position {}", position);
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

  /** Adjusts all entities after a window resize by recalculating world scale and layout. */
  public void resize() {
    float oldTile = tileSize;
    float oldX = xOffset;
    float oldY = yOffset;

    // Recalculate scale values
    setScaling();

    // Reposition and rescale all entities relative to new scale
    relayoutTiles();
    relayoutPlacedUnits();
    relayoutRobots(oldTile, oldX, oldY);
  }

  /** Updates the position of all tiles based on the new scaling. */
  private void relayoutTiles() {
    for (int row = 0; row < levelRows; row++) {
      for (int col = 0; col < levelCols; col++) {
        int index = row * levelCols + col;
        Entity tile = grid.getTile(index);
        if (tile == null) continue;

        float tileX = xOffset + tileSize * col;
        float tileY = yOffset + tileSize * row;
        tile.setPosition(tileX, tileY);
      }
    }
  }

  /** Moves and rescales all units that are placed on tiles. */
  private void relayoutPlacedUnits() {
    for (int row = 0; row < levelRows; row++) {
      for (int col = 0; col < levelCols; col++) {
        int index = row * levelCols + col;
        Entity unit = grid.getOccupantIndex(index);
        if (unit == null) continue;

        float newX = xOffset + tileSize * col;
        float newY = yOffset + tileSize * row;
        unit.setPosition(newX, newY);
        unit.scaleHeight(tileSize);
      }
    }
  }

  /** Adjusts free-floating robots (not grid-bound) based on proportional scaling. */
  private void relayoutRobots(float oldTile, float oldX, float oldY) {
    float scaleRatio = tileSize / oldTile;

    for (Entity robot : robots) {
      Vector2 oldPos = robot.getPosition();
      float newX = (oldPos.x - oldX) * scaleRatio + xOffset;
      float newY = (oldPos.y - oldY) * scaleRatio + yOffset;

      robot.setPosition(newX, newY);
      robot.scaleHeight(tileSize);
    }
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

        // play game over noise
        Sound sound =
            ServiceLocator.getResourceService().getAsset("sounds/game-over-voice.mp3", Sound.class);
        float volume = ServiceLocator.getSettingsService().getSoundVolume();
        sound.play(volume);

        // Window activation trigger
        gameOverEntity.getEvents().trigger("gameOver");
      }
    }
  }

  /** Checks if the level is complete */
  public void checkLevelComplete() {
    if (isLevelComplete) {
      return;
      // level is already complete, don't check again
    }

    int currentWave = ServiceLocator.getWaveService().getCurrentWave();
    if (currentWave >= 4) {
      logger.info("Level is complete!");
      isLevelComplete = true;
      if (levelCompleteEntity != null) {
        levelCompleteEntity.getEvents().trigger("levelComplete");
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

  /**
   * Create symbolic entities to preview the upcoming wave. Positions a couple of placeholder robots
   * per row just off the right edge. These are visual only and removed after the intro camera pan.
   */
  public void createWavePreview() {
    wavePreview.createWavePreview();
  }

  /** Remove preview entities created for the intro camera pan. */
  public void clearWavePreview() {
    wavePreview.clearWavePreview();
  }

  /**
   * Getter for grid
   *
   * @return grid
   */
  public LevelGameGrid getGrid() {
    return grid;
  }

  /**
   * gets worldWidth currently used by LevelGameArea
   *
   * @return worldWidth
   */
  public float getWorldWidth() {
    return worldWidth;
  }

  /**
   * Getter for xOffset
   *
   * @return xOffset
   */
  public float getXOffset() {
    return xOffset;
  }

  /**
   * Getter for levelRows
   *
   * @return levelRows
   */
  public int getLevelRows() {
    return levelRows;
  }

  /**
   * Getter for yOffset
   *
   * @return yOffset
   */
  public float getYOffset() {
    return yOffset;
  }

  /**
   * Getter for levelCols
   *
   * @return levelCols
   */
  public int getLevelCols() {
    return levelCols;
  }

  /**
   * Getter for robots
   *
   * @return robots list
   */
  public List<Entity> getRobots() {
    return robots;
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
   * Setter to set grid in LevelGameArea
   *
   * @param newGrid the grid to be set
   */
  public void setGrid(LevelGameGrid newGrid) {
    this.grid = newGrid;
  }
}
