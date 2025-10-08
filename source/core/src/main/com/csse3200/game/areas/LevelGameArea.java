package com.csse3200.game.areas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.components.CombatStatsComponent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.components.GeneratorStatsComponent;
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
  private boolean isGameOver = false;
  private final ArrayList<Entity> robots = new ArrayList<>();
  private final Map<String, Supplier<Entity>> unitList = new HashMap<>();
  private final Map<String, Supplier<Entity>> itemList = new HashMap<>();
  protected Entity gameOverEntity;
  // Drag and drop variables
  private DragOverlay dragOverlay;
  private boolean characterSelected = false;

  // Level configuration
  private String currentLevelKey;
  private int levelRows = 5; // Default fallback
  private int levelCols = 10; // Default fallback
  private float worldWidth; // background map world width
  private String mapFilePath; // from level config

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

    // TODO: Add dynamic updates when wave is changed.
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
        mapFilePath = "images/backgrounds/level-1-map-v2.png";
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

  /** Spawns the level UI */
  protected void displayUI() {
    Entity ui = new Entity();
    Profile profile = ServiceLocator.getProfileService().getProfile();
    ConfigService configService = ServiceLocator.getConfigService();

    for (String defenceKey : profile.getArsenal().getDefenders()) {
      BaseDefenderConfig config = configService.getDefenderConfig(defenceKey);
      unitList.put(config.getAssetPath(), () -> DefenceFactory.createDefenceUnit(config));
    }
    for (String generatorKey : profile.getArsenal().getGenerators()) {
      BaseGeneratorConfig config = configService.getGeneratorConfig(generatorKey);
      unitList.put(config.getAssetPath(), () -> GeneratorFactory.createGeneratorUnit(config));
    }

    Inventory inventory = profile.getInventory();

    if (inventory.contains("grenade")) {
      BaseItemConfig grenadeConfig = configService.getItemConfig("grenade");
      if (grenadeConfig != null) {
        itemList.put(grenadeConfig.getAssetPath(), ItemFactory::createGrenade);
      }
    }
    if (inventory.contains("coffee")) {
      BaseItemConfig coffeeConfig = configService.getItemConfig("coffee");
      if (coffeeConfig != null) {
        itemList.put(coffeeConfig.getAssetPath(), ItemFactory::createCoffee);
      }
    }
    if (inventory.contains("buff")) {
      BaseItemConfig buffConfig = configService.getItemConfig("buff");
      if (buffConfig != null) {
        itemList.put(buffConfig.getAssetPath(), ItemFactory::createBuff);
      }
    }
    if (inventory.contains("emp")) {
      BaseItemConfig empConfig = configService.getItemConfig("emp");
      if (empConfig != null) {
        itemList.put(empConfig.getAssetPath(), ItemFactory::createEmp);
      }
    }
    if (inventory.contains("nuke")) {
      BaseItemConfig nukeConfig = configService.getItemConfig("nuke");
      if (nukeConfig != null) {
        itemList.put(nukeConfig.getAssetPath(), ItemFactory::createNuke);
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

  /** Creates the game map and renders it */
  private void spawnMap() {
    // Compute world height (viewport height)
    float viewportHeight = stageHeight * stageToWorldRatio;

    // Load texture from level config
    Texture tex = ServiceLocator.getResourceService().getAsset(mapFilePath, Texture.class);
    if (tex == null) {
      // Ensure it’s loaded via MainGameScreen (already loaded backgrounds) or load on demand
      ServiceLocator.getResourceService().loadTextures(new String[] {mapFilePath});
      ServiceLocator.getResourceService().loadAll();
      tex = ServiceLocator.getResourceService().getAsset(mapFilePath, Texture.class);
    }

    // Create background entity
    Entity map = new Entity();
    BackgroundMapComponent bg = new BackgroundMapComponent(tex, viewportHeight);
    map.addComponent(bg);
    map.setPosition(0f, 0f); // left-aligned world

    // Let LevelGameArea expose world width for clamping/panning
    this.worldWidth = bg.getWorldWidth();

    spawnEntity(map);
    spawnWall();
  }

  private void spawnWall() {
    Entity wall = DefenceFactory.createWall();
    float tileX = xOffset + tileSize * -1;
    float tileY = yOffset - tileSize / 5;
    float wallSize = tileSize * 6;
    wall.scaleHeight(wallSize);
    wall.setPosition(tileX, tileY);
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
    if (robotType == RobotType.BUNGEE) {
      spawnRobotOnDefence(robotType);
      return;
    }

    Entity unit = RobotFactory.createRobotType(robotType);

    // Get and set position coords
    col = Math.clamp(col, 0, levelCols - 1);
    row = Math.clamp(row, 0, levelRows - 1);

    // place on that grid cell (bottom-left of the tile)
    float tileX = xOffset + tileSize * col;
    float tileY = yOffset + tileSize * row;

    unit.setPosition(tileX, tileY);
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

    final int rows = grid.getRows();
    final int cols = grid.getCols();
    final int total = rows * cols;

    int bestRow = -1;
    int bestCol = -1;

    // Find the RIGHT-MOST occupied cell (a placed defence)
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

    // No defences found -> do nothing (test expects no robot to be created)
    if (bestCol < 0) {
      logger.info("No defence tiles found to spawn {} robot on.", robotType);
      return;
    }

    // Create the robot and place it slightly to the right of the right-most defence
    Entity unit = RobotFactory.createRobotType(robotType);
    if (unit == null) {
      logger.error("spawnRobotOnDefence: RobotFactory returned null for {}", robotType);
      return;
    }

    // Clamp just inside the right edge to avoid spawning off-map
    float spawnCol = Math.min(bestCol + 0.5f, cols - 0.01f);
    float worldX = xOffset + tileSize * spawnCol;
    float worldY = yOffset + tileSize * bestRow;

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
    unit.getEvents().addListener("despawned", () -> robots.remove(unit));

    logger.info("Spawned {} robot at row={}, col+0.5={}", robotType, bestRow, spawnCol);
  }

  public void spawnProjectile(
      Vector2 spawnPos, Entity projectile, TargetDetectionTasks.AttackDirection direction) {
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
      int max_range = 9 - col;
      int num = random.nextInt(max_range - 1) + 2; // pick random num between 2 and 7
      projectile.addComponent(new PhysicsProjectileComponent(num * tileSize, direction));

      projectile
          .getEvents()
          .addListener(
              "despawnShell",
              (e) -> {
                Vector2 pos = projectile.getPosition();
                int damage = 5; // or configurable
                float radius = tileSize; // 1 tile radius
                damageRobotsAtPosition(pos, radius, damage);
              });
    } else {
      projectile.addComponent(new MoveDirectionComponent(direction)); // pass velocity
    }
    if (tag != null
        && tag.getType() != ProjectileType.HARPOON_PROJECTILE
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
        if (stats.isDead()) {
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
    // Resolve world position from tile index
    final int col = position % levelCols;
    final int row = position / levelCols;
    final float tileX = xOffset + tileSize * col;
    final float tileY = yOffset + tileSize * row;
    final Vector2 entityPos = new Vector2(tileX, tileY);

    Supplier<Entity> entitySupplier =
        selectedUnit.getComponent(DeckInputComponent.class).getEntitySupplier();
    Entity newEntity = entitySupplier.get();
    if (newEntity == null) {
      logger.error("Entity fetched was NULL");
      return;
    }
    newEntity.setPosition(entityPos);

    // Get the tile at the spawn coordinates
    Entity selectedTile = grid.getTile(position);
    if ("mortar".equals(newEntity.getProperty("unitType")) && tileX >= 1000) {
      return;
    }
    if (selectedTile == null) {
      logger.warn("No tile entity found at index {}", position);
      return;
    }

    // ---------- ITEM PATH  (to be moved to a different method in future) ----------

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
    if (item != null) {
      spawnItem(item, entityPos);

      // Clear Item from tile storage
      selectedTile.getComponent(TileStorageComponent.class).removeTileUnit();
      // Clear drag/selection after using an item
      setIsCharacterSelected(false);
      setSelectedUnit(null);
      cancelDrag();
      return;
    }

    // ---------- DEFENCE/UNIT PATH (single source of truth = GRID) ----------

    if (grid.isOccupiedIndex(position)) {
      logger.info("Tile {} already occupied", position);
      return;
    }

    // Add entity to tile unless it is an Item
    selectedTile.getComponent(TileStorageComponent.class).setTileUnit(newEntity);

    // set scale to render as desired
    newEntity.scaleHeight(tileSize);

    // if entity is a furnace, trigger currency generation at that point
    if (newEntity.getComponent(GeneratorStatsComponent.class) != null) {
      if (newEntity.getComponent(GeneratorStatsComponent.class).getInterval() > 0) {
        spawnScrap(newEntity);
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

    spawnEntity(newEntity);
    // trigger the animation - this will change with more entities
    newEntity.getEvents().trigger("idleStart");

    // Ensure grid slot frees when unit leaves
    Runnable clearTile =
        () -> {
          grid.removeOccupantIfMatchIndex(position, newEntity);
          selectedTile.getComponent(TileStorageComponent.class).removeTileUnit();
        };

    newEntity
        .getEvents()
        .addListener(
            ENTITY_DEATH_EVENT,
            () -> {
              requestDespawn(newEntity);
              clearTile.run();
              robots.remove(newEntity);
            });

    newEntity.getEvents().addListener("despawned", clearTile::run);

    logger.info("Unit spawned at position {} (r={}, c={})", position, row, col);

    newEntity
        .getEvents()
        .addListener(
            "fire",
            (TargetDetectionTasks.AttackDirection direction) -> {
              if (newEntity.getComponent(ProjectileComponent.class) != null) {
                spawnProjectile(
                    entityPos,
                    newEntity.getComponent(ProjectileComponent.class).getProjectile(),
                    direction);
              }
              newEntity.getEvents().trigger("attackStart");
            });
    setIsCharacterSelected(false);
    setSelectedUnit(null);
    cancelDrag();
  }

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
   * Spawns item at the selected tile and triggers the relevant item effect to occur.
   *
   * @param item the specific item type (component)
   * @param entityPos the tile position on which the item was placed
   */
  public void spawnItem(ItemComponent item, Vector2 entityPos) {
    String itemType = item.getType().toString();
    logger.info("Spawning item {}", itemType);
    String key = item.getType().toString().toLowerCase(Locale.ROOT);

    // Remove one instance of the Item from the inventory
    ServiceLocator.getProfileService().getProfile().getInventory().removeItem(key);
    logger.info("One {} item used", key);

    // Spawn effect
    // Currently just effect displays, not entity itself then effect after a delay
    ServiceLocator.getItemEffectsService()
        .playEffect(
            key,
            entityPos,
            (int) tileSize,
            new Vector2(
                (float) (xOffset * 0.25 + levelCols * tileSize), (float) (tileSize * -0.75)));

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
      return;
    }

    // For other items trigger impact on defences
    String itemTrigger = ServiceLocator.getConfigService().getItemConfig(key).getTrigger();
    final int total = grid.getRows() * grid.getCols();

    // Send trigger on each currently placed defence
    for (int i = 0; i < total; i++) {
      // Get entity on the grid tile
      Entity entity = grid.getOccupantIndex(i);
      // Check whether there is an entity and whether it is a defence
      if (entity != null
          && (entity.getComponent(DefenderStatsComponent.class) != null
              || entity.getComponent(GeneratorStatsComponent.class) != null)) {
        // Send start trigger
        entity.getEvents().trigger(itemTrigger);
        logger.info("Start {} on {}", itemTrigger, entity);
        // Create a timer to be able to send a trigger to stop after 30 seconds
        final Timer.Task repeatTask =
            new Timer.Task() {
              @Override
              public void run() {
                entity.getEvents().trigger(itemTrigger + "Stop");
                logger.info("Stop {}", itemTrigger);
              }
            };
        Timer.schedule(repeatTask, 30f);
      }
    }
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
    float oldTile = tileSize;
    float oldX = xOffset;
    float oldY = yOffset;

    setScaling(); // recompute sizes from renderer/stage
    relayoutAfterScaling(oldTile, oldX, oldY);
  }

  private void relayoutAfterScaling(float oldTile, float oldX, float oldY) {
    // 1) Move all tiles to their new positions
    for (int i = 0; i < levelRows * levelCols; i++) {
      int col = i % levelCols;
      int row = i / levelCols;
      float tileX = xOffset + tileSize * col;
      float tileY = yOffset + tileSize * row;

      // Assuming LevelGameGrid exposes getTile(i). If not, add it.
      Entity tile = grid.getTile(i);
      if (tile != null) {
        tile.setPosition(tileX, tileY);
      }
    }

    // Move all placed units to their tile’s new position and re-scale
    for (int i = 0; i < levelRows * levelCols; i++) {
      Entity u = grid.getOccupantIndex(i);
      if (u == null) continue;

      int col = i % levelCols;
      int row = i / levelCols;
      float nx = xOffset + tileSize * col;
      float ny = yOffset + tileSize * row;

      u.setPosition(nx, ny);
      u.scaleHeight(tileSize);
    }

    // Continuously positioned robots: scale & offset from old → new
    float s = tileSize / oldTile;
    for (Entity r : robots) {
      Vector2 p = r.getPosition();
      float nx = (p.x - oldX) * s + xOffset;
      float ny = (p.y - oldY) * s + yOffset;
      r.setPosition(nx, ny);
      r.scaleHeight(tileSize);
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

  public float getWorldWidth() {
    return worldWidth;
  }
}
