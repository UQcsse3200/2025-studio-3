package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.areas.terrain.TerrainFactory.TerrainType;
import com.csse3200.game.components.currency.CurrencyGeneratorComponent;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.DefenceFactory;
import com.csse3200.game.entities.factories.NPCFactory;
import com.csse3200.game.entities.factories.ObstacleFactory;
import com.csse3200.game.entities.factories.PlayerFactory;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.utils.math.GridPoint2Utils;
import com.csse3200.game.utils.math.RandomUtils;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Forest area for the demo game with trees, a player, and some enemies. */
public class ForestGameArea extends GameArea {
  private static final Logger logger = LoggerFactory.getLogger(ForestGameArea.class);
  private static final int NUM_TREES = 7;
  private static final int NUM_ROBOTS = 7;
  private static final int NUM_GHOSTS = 0;
  private static final GridPoint2 PLAYER_SPAWN = new GridPoint2(10, 10);
  private static final float WALL_WIDTH = 0.1f;
  private static final String GRASS_TEXTURE = "images/iso_grass_3.png";
  private static final String[] FOREST_SOUNDS = {"sounds/Impact4.ogg"};
  private static final String BACKGROUND_MUSIC = "sounds/BGM_03_mp3.mp3";
  private static final String[] FOREST_MUSIC = {BACKGROUND_MUSIC};
  private static final String[] forestTextures = {
    "images/box_boy_leaf.png",
    "images/tree.png",
    "images/ghost_king.png",
    "images/ghost_1.png",
    "images/grass_1.png",
    "images/grass_2.png",
    "images/grass_3.png",
    "images/hex_grass_1.png",
    "images/hex_grass_2.png",
    "images/hex_grass_3.png",
    "images/iso_grass_1.png",
    "images/iso_grass_2.png",
    GRASS_TEXTURE,
    "images/sling_shooter.png"
  };
  private static final String[] forestTextureAtlases = {
    "images/terrain_iso_grass.atlas",
    "images/ghost.atlas",
    "images/ghostKing.atlas",
    "images/sling_shooter.atlas",
    GRASS_TEXTURE,
    "images/robot_placeholder.png",
    "images/normal_sunlight.png",
    "images/sling_shooter.png"
  };

  private final TerrainFactory terrainFactory;
  private CurrencyGeneratorComponent currencyGenerator;

  private Entity player;

  /**
   * Initialise this ForestGameArea to use the provided TerrainFactory.
   *
   * @param terrainFactory TerrainFactory used to create the terrain for the GameArea.
   * @requires terrainFactory != null
   */
  public ForestGameArea(TerrainFactory terrainFactory) {
    super();
    this.terrainFactory = terrainFactory;
  }

  /** Create the game area, including terrain, static entities (trees), dynamic entities (player) */
  @Override
  public void create() {
    loadAssets();

    displayUI();

    spawnTerrain();
    spawnTrees();
    player = spawnPlayer();

    spawnGhosts();
    spawnGhostKing();

    spawnRobots();
    spawnSun();
    spawnDefences();


    playMusic();
  }

  private void spawnDefences() {
    GridPoint2 minPos = new GridPoint2(0, 0);
    GridPoint2 maxPos = terrain.getMapBounds(0).sub(2, 2);

    GridPoint2 randomPos = RandomUtils.random(minPos, maxPos);
    Entity slingShooter = DefenceFactory.createSlingShooter();

    spawnEntityAt(slingShooter, randomPos, true, true);
    slingShooter.getEvents().addListener("entityDeath", (Entity e) -> requestDespawn(e));
    slingShooter.getEvents().trigger("idleStart");
  }

  private void displayUI() {
    Entity ui = new Entity();
    ui.addComponent(new GameAreaDisplay("Box Forest"));
    spawnEntity(ui);
  }

  private void spawnTerrain() {
    // Background terrain
    terrain = terrainFactory.createTerrain(TerrainType.FOREST_DEMO);
    spawnEntity(new Entity().addComponent(terrain));

    // Terrain walls
    float tileSize = terrain.getTileSize();
    GridPoint2 tileBounds = terrain.getMapBounds(0);
    Vector2 worldBounds = new Vector2(tileBounds.x * tileSize, tileBounds.y * tileSize);

    // Left
    spawnEntityAt(
        ObstacleFactory.createWall(WALL_WIDTH, worldBounds.y), GridPoint2Utils.ZERO, false, false);
    // Right
    spawnEntityAt(
        ObstacleFactory.createWall(WALL_WIDTH, worldBounds.y),
        new GridPoint2(tileBounds.x, 0),
        false,
        false);
    // Top
    spawnEntityAt(
        ObstacleFactory.createWall(worldBounds.x, WALL_WIDTH),
        new GridPoint2(0, tileBounds.y),
        false,
        false);
    // Bottom
    spawnEntityAt(
        ObstacleFactory.createWall(worldBounds.x, WALL_WIDTH), GridPoint2Utils.ZERO, false, false);
  }

  private void spawnTrees() {
    GridPoint2 minPos = new GridPoint2(0, 0);
    GridPoint2 maxPos = terrain.getMapBounds(0).sub(2, 2);

    for (int i = 0; i < NUM_TREES; i++) {
      GridPoint2 randomPos = RandomUtils.random(minPos, maxPos);
      Entity tree = ObstacleFactory.createTree();
      spawnEntityAt(tree, randomPos, true, false);
    }
  }

  public void spawnLaser(Entity entity) {
    autofire(
        entity,
        () -> {
          Entity laser = ObstacleFactory.createLaser();

          HitboxComponent hitbox = laser.getComponent(HitboxComponent.class);
          if (hitbox == null) {
            hitbox = new HitboxComponent();
            hitbox.setSensor(true);
            laser.addComponent(hitbox);
          } else {
            hitbox.setSensor(true);
          }

          Vector2 ePos = entity.getPosition();
          Vector2 dirn =
              entity.getComponent(PhysicsMovementComponent.class).getDirection().cpy().nor();
          float offset = 1.0f;
          Vector2 spawnPos = ePos.cpy().add(dirn.cpy().scl(offset));
          GridPoint2 entityPos = new GridPoint2(Math.round(spawnPos.x), Math.round(spawnPos.y));
          spawnEntityAt(laser, entityPos, true, true);
        });
  }

  /**
   * autofire -> Sets an automatic fire rate for projectiles
   *
   * @param entity Entity from which projectiles will spawn
   * @param SpawnAction The instructions to deploy the said parameter
   */
  private void autofire(Entity entity, Runnable SpawnAction) {
    final float initdelay = 2.0f;
    final float firerate = 0.5f;

    Timer.schedule(
        new Timer.Task() {
          @Override
          public void run() {
            SpawnAction.run();
          }
        },
        initdelay,
        firerate);
  }

  private Entity spawnPlayer() {
    Entity newPlayer = PlayerFactory.createPlayer();
    spawnEntityAt(newPlayer, PLAYER_SPAWN, true, true);
    newPlayer.getEvents().addListener("entityDeath", (Entity e) -> requestDespawn(e));
    return newPlayer;
  }

  private void spawnGhosts() {
    GridPoint2 minPos = new GridPoint2(0, 0);
    GridPoint2 maxPos = terrain.getMapBounds(0).sub(2, 2);

    for (int i = 0; i < NUM_GHOSTS; i++) {
      GridPoint2 randomPos = RandomUtils.random(minPos, maxPos);
      Entity ghost = NPCFactory.createGhost(player);

      ghost.getEvents().addListener("despawnGhost", (Entity e) -> requestDespawn(e));
      spawnEntityAt(ghost, randomPos, true, true);
    }
  }

  private void spawnRobots() {
    GridPoint2 minPos = new GridPoint2(0, 0);
    GridPoint2 maxPos = terrain.getMapBounds(0).sub(2, 2);

    for (int i = 0; i < NUM_ROBOTS; i++) {
      GridPoint2 randomPos = RandomUtils.random(minPos, maxPos);
      Entity robot = NPCFactory.createRobot(player);
      spawnEntityAt(robot, randomPos, true, true);
      robot.getEvents().addListener("despawnRobot", (Entity e) -> requestDespawn(e));
    }
  }

  private void spawnGhostKing() {
    GridPoint2 minPos = new GridPoint2(0, 0);
    GridPoint2 maxPos = terrain.getMapBounds(0).sub(2, 2);

    GridPoint2 randomPos = RandomUtils.random(minPos, maxPos);
    Entity ghostKing = NPCFactory.createGhostKing(player);
    spawnEntityAt(ghostKing, randomPos, true, true);
  }

  public void despawnGhost(Entity ghost) {
    despawnEntity(ghost);
  }

  public void despawnRobot(Entity robot) {
    despawnEntity(robot);
  }

  private void playMusic() {
    Music music = ServiceLocator.getResourceService().getAsset(BACKGROUND_MUSIC, Music.class);
    music.setLooping(true);
    music.setVolume(0.3f);
    music.play();
  }

  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(forestTextures);
    resourceService.loadTextureAtlases(forestTextureAtlases);
    resourceService.loadSounds(FOREST_SOUNDS);
    resourceService.loadMusic(FOREST_MUSIC);

    while (!resourceService.loadForMillis(10)) {
      // This could be upgraded to a loading screen
      logger.info("Loading... {}%", resourceService.getProgress());
    }
  }

  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(forestTextures);
    resourceService.unloadAssets(forestTextureAtlases);
    resourceService.unloadAssets(FOREST_SOUNDS);
    resourceService.unloadAssets(FOREST_MUSIC);
  }

  private void spawnSun() {
    Entity sunSpawner = new Entity();

    currencyGenerator = new CurrencyGeneratorComponent(5f, 25, "images/normal_sunlight.png");

    sunSpawner.addComponent(currencyGenerator);
    spawnEntity(sunSpawner);
  }

  @Override
  public void dispose() {
    super.dispose();
    ServiceLocator.getResourceService().getAsset(BACKGROUND_MUSIC, Music.class).stop();
    this.unloadAssets();
  }
}
