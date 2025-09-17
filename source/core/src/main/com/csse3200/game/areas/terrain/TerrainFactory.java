package com.csse3200.game.areas.terrain;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.terrain.TerrainComponent.TerrainOrientation;
import com.csse3200.game.components.CameraComponent;
import com.csse3200.game.entities.configs.BaseLevelGameConfig;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.utils.math.RandomUtils;

/** Factory for creating game terrains. */
public class TerrainFactory {
  private static final GridPoint2 MAP_SIZE = new GridPoint2(30, 30);
  private static final int TUFT_TILE_COUNT = 30;
  private static final int ROCK_TILE_COUNT = 30;

  private final OrthographicCamera camera;
  private final TerrainOrientation orientation;

  private final BaseLevelGameConfig configs;

  /**
   * Create a terrain factory with Orthogonal orientation
   *
   * @param cameraComponent Camera to render terrains to. Must be ortographic.
   */
  public TerrainFactory(CameraComponent cameraComponent) {
    this(cameraComponent, TerrainOrientation.ORTHOGONAL);
  }

  /**
   * Create a terrain factory
   *
   * @param cameraComponent Camera to render terrains to. Must be orthographic.
   * @param orientation orientation to render terrain at
   */
  public TerrainFactory(CameraComponent cameraComponent, TerrainOrientation orientation) {
    this.camera = (OrthographicCamera) cameraComponent.getCamera();
    this.orientation = orientation;
    this.configs = FileLoader.readClass(BaseLevelGameConfig.class, "configs/levels.json");
  }

  /**
   * Create a terrain of the given type, using the orientation of the factory. This can be extended
   * to add additional game terrains.
   *
   * @param terrainType Terrain to create
   * @return Terrain component which renders the terrain
   */
  public TerrainComponent createTerrain(TerrainType terrainType) {
    ResourceService resourceService = ServiceLocator.getResourceService();
    switch (terrainType) {
      case FOREST_DEMO:
        TextureRegion orthoGrass =
            new TextureRegion(resourceService.getAsset("images/grass_1.png", Texture.class));
        TextureRegion orthoTuft =
            new TextureRegion(resourceService.getAsset("images/grass_2.png", Texture.class));
        TextureRegion orthoRocks =
            new TextureRegion(resourceService.getAsset("images/grass_3.png", Texture.class));
        return createForestDemoTerrain(0.5f, orthoGrass, orthoTuft, orthoRocks);
      case FOREST_DEMO_ISO:
        TextureRegion isoGrass =
            new TextureRegion(resourceService.getAsset("images/iso_grass_1.png", Texture.class));
        TextureRegion isoTuft =
            new TextureRegion(resourceService.getAsset("images/iso_grass_2.png", Texture.class));
        TextureRegion isoRocks =
            new TextureRegion(resourceService.getAsset("images/iso_grass_3.png", Texture.class));
        return createForestDemoTerrain(1f, isoGrass, isoTuft, isoRocks);
      case FOREST_DEMO_HEX:
        TextureRegion hexGrass =
            new TextureRegion(resourceService.getAsset("images/hex_grass_1.png", Texture.class));
        TextureRegion hexTuft =
            new TextureRegion(resourceService.getAsset("images/hex_grass_2.png", Texture.class));
        TextureRegion hexRocks =
            new TextureRegion(resourceService.getAsset("images/hex_grass_3.png", Texture.class));
        return createForestDemoTerrain(1f, hexGrass, hexTuft, hexRocks);
      case LEVEL_ONE_MAP:
        TextureRegion levelMap =
            new TextureRegion(resourceService.getAsset("images/level-1-map-v2.png", Texture.class));
        return createLevelMap(levelMap);
      default:
        return null;
    }
  }

  /**
   * Takes the level number to create a terrain component using the createLevelTerrain method.
   * @param levelNum integer representing the level number of the terrain component being created.
   * @return Terrain component reflecting the level number presented, which renders the terrain.
   */
  public TerrainComponent createTerrain(int levelNum) {
    if (levelNum == 1) {
      BaseLevelGameConfig config = new BaseLevelGameConfig();
      return createLevelTerrain(config);
    } else if (levelNum == 2) {
      BaseLevelGameConfig config = new BaseLevelGameConfig();
      return createLevelTerrain(config);
    } else {
      BaseLevelGameConfig config = new BaseLevelGameConfig();
      return createLevelTerrain(config);
    }
  }

  /**
   * Creates a terrain component according to the config passed by createTerrain.
   * @param config containing the required number of rows, columns and filepath to the map image file.
   * @return Terrain component reflecting the config presented, which renders the terrain.
   */
  public TerrainComponent createLevelTerrain(BaseLevelGameConfig config) {
    ResourceService resourceService = ServiceLocator.getResourceService();
    if (config.getLevelNum() == 1) {
      TextureRegion levelMap =
          new TextureRegion(resourceService.getAsset(config.getMapFilePath(), Texture.class));
      return createLevelMap(levelMap);
    } else if (config.getLevelNum() == 2) {
      TextureRegion levelMap =
          new TextureRegion(resourceService.getAsset(config.getMapFilePath(), Texture.class));
      return createLevelMap(levelMap);
    } else {
      TextureRegion levelMap =
          new TextureRegion(resourceService.getAsset(configs.getMapFilePath(), Texture.class));
      return createLevelMap(levelMap);
    }
  }

  /**
   * Creates the map for a game level
   *
   * @param levelMap the map to create
   * @return the Terrain Component
   */
  private TerrainComponent createLevelMap(TextureRegion levelMap) {
    // Scale based on height so image fills viewport vertically
    float worldHeight = camera.viewportHeight;
    float scale = worldHeight / levelMap.getRegionHeight();
    float worldWidth = levelMap.getRegionWidth() * scale;

    // Create fake tiled map with one cell
    TiledMap tiledMap = new TiledMap();
    TerrainTile mapTile = new TerrainTile(levelMap);

    TiledMapTileLayer layer = new TiledMapTileLayer(1, 1, (int) worldWidth, (int) worldHeight);
    Cell cell = new Cell();
    cell.setTile(mapTile);
    layer.setCell(0, 0, cell);

    tiledMap.getLayers().add(layer);

    TiledMapRenderer renderer = createRenderer(tiledMap, scale);

    return new TerrainComponent(camera, tiledMap, renderer, orientation, worldWidth);
  }

  private TerrainComponent createForestDemoTerrain(
      float tileWorldSize, TextureRegion grass, TextureRegion grassTuft, TextureRegion rocks) {
    GridPoint2 tilePixelSize = new GridPoint2(grass.getRegionWidth(), grass.getRegionHeight());
    TiledMap tiledMap = createForestDemoTiles(tilePixelSize, grass, grassTuft, rocks);
    TiledMapRenderer renderer = createRenderer(tiledMap, tileWorldSize / tilePixelSize.x);
    return new TerrainComponent(camera, tiledMap, renderer, orientation, tileWorldSize);
  }

  private TiledMapRenderer createRenderer(TiledMap tiledMap, float tileScale) {
    switch (orientation) {
      case ORTHOGONAL:
        return new OrthogonalTiledMapRenderer(tiledMap, tileScale);
      case ISOMETRIC:
        return new IsometricTiledMapRenderer(tiledMap, tileScale);
      case HEXAGONAL:
        return new HexagonalTiledMapRenderer(tiledMap, tileScale);
      default:
        return null;
    }
  }

  private TiledMap createForestDemoTiles(
      GridPoint2 tileSize, TextureRegion grass, TextureRegion grassTuft, TextureRegion rocks) {
    TiledMap tiledMap = new TiledMap();
    TerrainTile grassTile = new TerrainTile(grass);
    TerrainTile grassTuftTile = new TerrainTile(grassTuft);
    TerrainTile rockTile = new TerrainTile(rocks);
    TiledMapTileLayer layer = new TiledMapTileLayer(MAP_SIZE.x, MAP_SIZE.y, tileSize.x, tileSize.y);

    // Create base grass
    fillTiles(layer, MAP_SIZE, grassTile);

    // Add some grass and rocks
    fillTilesAtRandom(layer, MAP_SIZE, grassTuftTile, TUFT_TILE_COUNT);
    fillTilesAtRandom(layer, MAP_SIZE, rockTile, ROCK_TILE_COUNT);

    tiledMap.getLayers().add(layer);
    return tiledMap;
  }

  private static void fillTilesAtRandom(
      TiledMapTileLayer layer, GridPoint2 mapSize, TerrainTile tile, int amount) {
    GridPoint2 min = new GridPoint2(0, 0);
    GridPoint2 max = new GridPoint2(mapSize.x - 1, mapSize.y - 1);

    for (int i = 0; i < amount; i++) {
      GridPoint2 tilePos = RandomUtils.random(min, max);
      Cell cell = layer.getCell(tilePos.x, tilePos.y);
      cell.setTile(tile);
    }
  }

  private static void fillTiles(TiledMapTileLayer layer, GridPoint2 mapSize, TerrainTile tile) {
    for (int x = 0; x < mapSize.x; x++) {
      for (int y = 0; y < mapSize.y; y++) {
        Cell cell = new Cell();
        cell.setTile(tile);
        layer.setCell(x, y, cell);
      }
    }
  }

  /**
   * This enum should contain the different terrains in your game, e.g. forest, cave, home, all with
   * the same oerientation. But for demonstration purposes, the base code has the same level in 3
   * different orientations.
   */
  public enum TerrainType {
    FOREST_DEMO,
    FOREST_DEMO_ISO,
    FOREST_DEMO_HEX,
    LEVEL_ONE_MAP
  }
}
