package com.csse3200.game.areas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.items.ItemComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.*;
import com.csse3200.game.ui.DragOverlay;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class LevelGameAreaTest {
  @Mock RenderService renderService;
  @Mock Stage stage;
  @Mock ResourceService resourceService;
  @Mock Music music;
  @Mock ProfileService profileService;
  @Mock ConfigService configService;
  @Mock DiscordRichPresenceService discordRichPresenceService;

  private MockedStatic<Persistence> persistenceMock;
  private Profile profile;

  private MockedStatic<com.badlogic.gdx.utils.Timer> timerMock;

  /** A class to capture spawned entities without needing a full ECS */
  static class CapturingLevelGameArea extends LevelGameArea {
    final List<Entity> spawned = new ArrayList<>();

    CapturingLevelGameArea() {
      super("levelOne");
    }

    @Override
    protected void spawnEntity(Entity entity) {
      spawned.add(entity);
    }
  }

  @BeforeEach
  void beforeEach() {
    ServiceLocator.registerRenderService(renderService);
    ServiceLocator.registerResourceService(resourceService);
    ServiceLocator.registerProfileService(profileService);
    ServiceLocator.registerConfigService(configService);
    ServiceLocator.registerDiscordRichPresenceService(discordRichPresenceService);

    lenient().when(renderService.getStage()).thenReturn(stage);
    // second value allows testing of resize
    lenient().when(stage.getWidth()).thenReturn(1920f);
    lenient().when(stage.getHeight()).thenReturn(1080f);

    lenient()
        .when(resourceService.getAsset(anyString(), eq(Texture.class)))
        .thenReturn(mock(Texture.class));

    lenient().when(resourceService.loadForMillis(anyInt())).thenReturn(true);
    lenient().when(resourceService.getProgress()).thenReturn(1);

    profile = new Profile();
    profile.getInventory().addItem("grenade"); // so inventory not null
    profile.getArsenal().unlockDefence("slingshooter"); // Add some defences for testing
    profile.getArsenal().unlockDefence("furnace");
    lenient().when(profileService.getProfile()).thenReturn(profile);

    // Mock config service calls
    lenient().when(configService.getDefenderConfig(anyString())).thenReturn(null);
    lenient().when(configService.getGeneratorConfig(anyString())).thenReturn(null);
    lenient().when(configService.getItemConfig(anyString())).thenReturn(null);

    lenient()
        .doNothing()
        .when(discordRichPresenceService)
        .updateGamePresence(anyString(), anyInt());

    persistenceMock = mockStatic(Persistence.class, withSettings().strictness(Strictness.LENIENT));
    // Note: Persistence.profile() no longer exists in the reworked system

    // Make Timer.schedule(task, delay) run the task immediately
    timerMock =
        mockStatic(
            com.badlogic.gdx.utils.Timer.class, withSettings().strictness(Strictness.LENIENT));
    timerMock
        .when(
            () ->
                com.badlogic.gdx.utils.Timer.schedule(
                    Mockito.<com.badlogic.gdx.utils.Timer.Task>any(), Mockito.anyFloat()))
        .thenAnswer(
            inv -> {
              com.badlogic.gdx.utils.Timer.Task task = inv.getArgument(0);
              // run the Stop task immediately so your test can assert synchronously
              task.run();
              return null;
            });
  }

  @AfterEach
  void afterEach() {
    try {
      ServiceLocator.clear();
      if (persistenceMock != null) {
        persistenceMock.close();
      }
      if (timerMock != null) timerMock.close();
    } catch (Throwable ignored) {
      // Ignore throwable and continue to next test
    }
  }

  @Test
  void setScalingUsesStageSizeAndRendererWidth() {
    LevelGameArea area = new LevelGameArea("levelOne");

    float tile = area.getTileSize();
    assertTrue(tile > 0f, "tileSize should be computed > 0");

    GridPoint2 s = new GridPoint2(100, 200);
    GridPoint2 world = area.stageToWorld(s);
    GridPoint2 back = area.worldToStage(world);

    // delta due to conversion rounding
    assertEquals(s.x, back.x, 2);
    assertEquals(s.y, back.y, 2);

    // y values should be flipped
    assertNotEquals(s.y, world.y);
  }

  @Test
  void spawnUnitWithNullSupplier() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());

    // Selected “card” whose supplier returns null
    Entity selected = new Entity().addComponent(new DeckInputComponent(area, () -> null));
    area.setSelectedUnit(selected);

    // Minimal grid so code can compute a tile
    LevelGameGrid grid = mock(LevelGameGrid.class);
    lenient().when(grid.getTileFromXY(anyFloat(), anyFloat())).thenReturn(new Entity());
    area.setGrid(grid);

    area.spawnUnit(0);
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void spawnUnitItemNotInInventoryRemovesFromTileAndReturns() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());

    // Selected entity is an Item of type GRENADE (matches your seeded key)
    ItemComponent item = mock(ItemComponent.class);
    when(item.getType()).thenReturn(ItemComponent.Type.GRENADE);
    Entity itemEntity = new Entity().addComponent(item);
    area.setSelectedUnit(new Entity().addComponent(new DeckInputComponent(area, () -> itemEntity)));

    // Arrange a tile with storage
    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = new Entity().addComponent(storage);
    LevelGameGrid grid = mock(LevelGameGrid.class);
    when(grid.getTile(anyInt())).thenReturn(tile);
    area.setGrid(grid);

    // Use profile from @BeforeEach and make it NOT contain the item
    profile.getInventory().removeItem("grenade");

    area.spawnUnit(1);

    verify(storage).removeTileUnit();
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void spawnUnitItemInInventoryConsumesOnePlaysEffectAndClearsTile() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());

    ItemComponent item = mock(ItemComponent.class);
    when(item.getType()).thenReturn(ItemComponent.Type.GRENADE);
    Entity grenade = new Entity().addComponent(item);
    area.setSelectedUnit(new Entity().addComponent(new DeckInputComponent(area, () -> grenade)));

    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = new Entity().addComponent(storage);
    LevelGameGrid grid = mock(LevelGameGrid.class);
    when(grid.getTile(anyInt())).thenReturn(tile);
    area.setGrid(grid);

    ItemEffectsService effects = mock(ItemEffectsService.class);
    ServiceLocator.registerItemEffectsService(effects);

    area.spawnUnit(2);

    verify(effects).playEffect(anyString(), any(Vector2.class), anyInt(), any(Vector2.class));
    assertFalse(ServiceLocator.getProfileService().getProfile().getInventory().contains("grenade"));
    verify(storage).removeTileUnit();
  }

  @Test
  void getters() {
    LevelGameGrid grid = mock(LevelGameGrid.class);
    LevelGameArea area = new LevelGameArea("levelOne");
    area.setGrid(grid);

    assertSame(grid, area.getGrid());
    assertNull(area.getSelectedUnit());
  }

  @Test
  void dragNullsAreNoop() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());

    // dragOverlay is null until create(); also pass null texture
    assertDoesNotThrow(() -> area.beginDrag(null));
    assertDoesNotThrow(area::cancelDrag);
  }

  @Test
  void characterSelectedRoundTrip() {
    LevelGameArea area = new LevelGameArea("levelOne");
    assertFalse(area.isCharacterSelected());
    area.setIsCharacterSelected(true);
    assertTrue(area.isCharacterSelected());
  }

  @Test
  void spawnRobotOnDefenceWithNullGridIsNoop() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());
    area.spawnRobotOnDefence(RobotFactory.RobotType.STANDARD);
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void spawnRobotOnDefenceWithoutAnyDefenceIsNoop() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());

    LevelGameGrid grid = mock(LevelGameGrid.class);
    area.setGrid(grid);
    area.spawnRobotOnDefence(RobotFactory.RobotType.STANDARD);
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void beginDrag_overlayNull_textureNull_doesNothing() {
    CapturingLevelGameArea area = new CapturingLevelGameArea();

    // overlay = null, texture = null → if short-circuits, nothing happens
    assertDoesNotThrow(() -> area.beginDrag(null));
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void beginDrag_overlayNull_textureNotNull_doesNothing() {
    CapturingLevelGameArea area = new CapturingLevelGameArea();

    Texture mockTexture = mock(Texture.class);
    // overlay = null, texture != null → left side false, nothing happens
    assertDoesNotThrow(() -> area.beginDrag(mockTexture));
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void beginDrag_overlayNotNull_textureNull_doesNothing() throws Exception {
    CapturingLevelGameArea area = new CapturingLevelGameArea();

    // Use reflection to inject a mock DragOverlay into the private field
    DragOverlay overlay = mock(DragOverlay.class);
    var f = LevelGameArea.class.getDeclaredField("dragOverlay");
    f.setAccessible(true);
    f.set(area, overlay);

    // Call with texture = null
    assertDoesNotThrow(() -> area.beginDrag(null));

    // Verify overlay.begin() was NOT called
    verify(overlay, never()).begin(any());
  }

  @Test
  void cancelDrag_overlayNull_doesNothing() {
    CapturingLevelGameArea area = new CapturingLevelGameArea();

    // overlay = null → if condition false, nothing happens
    assertDoesNotThrow(area::cancelDrag);
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void beginDrag_overlayAndTextureNotNull_callsBegin() throws Exception {
    CapturingLevelGameArea area = new CapturingLevelGameArea();

    // Inject a mock overlay
    DragOverlay overlay = mock(DragOverlay.class);
    var f = LevelGameArea.class.getDeclaredField("dragOverlay");
    f.setAccessible(true);
    f.set(area, overlay);

    // Use a mock texture
    Texture texture = mock(Texture.class);

    // Act
    area.beginDrag(texture);

    // Assert
    verify(overlay).begin(texture);
  }

  @Test
  void cancelDrag_overlayNotNull_callsCancel() throws Exception {
    CapturingLevelGameArea area = new CapturingLevelGameArea();

    // Inject a mock overlay
    DragOverlay overlay = mock(DragOverlay.class);
    var f = LevelGameArea.class.getDeclaredField("dragOverlay");
    f.setAccessible(true);
    f.set(area, overlay);

    // Act
    area.cancelDrag();

    // Assert
    verify(overlay).cancel();
  }

  @Test
  void loadLevelConfiguration_usesDefaultMapPath_whenConfigMapPathMissing() throws Exception {
    // Return a level config with rows/cols but NO map file path
    BaseLevelConfig levelCfg = mock(BaseLevelConfig.class);
    when(levelCfg.getRows()).thenReturn(7);
    when(levelCfg.getCols()).thenReturn(11);
    when(levelCfg.getMapFile()).thenReturn(null);
    when(configService.getLevelConfig(anyString())).thenReturn(levelCfg);

    LevelGameArea area = new LevelGameArea("levelOne");

    // Reflect to verify private fields populated by loadLevelConfiguration()
    Field rowsF = LevelGameArea.class.getDeclaredField("levelRows");
    Field colsF = LevelGameArea.class.getDeclaredField("levelCols");
    Field mapF = LevelGameArea.class.getDeclaredField("mapFilePath");
    rowsF.setAccessible(true);
    colsF.setAccessible(true);
    mapF.setAccessible(true);

    assertEquals(7, rowsF.getInt(area));
    assertEquals(11, colsF.getInt(area));
    assertEquals("images/backgrounds/level_map_grass.png", (String) mapF.get(area));
  }

  @Test
  void create_populatesUnitAndItemLists_andSpawnsWindows() throws Exception {
    // Provide a valid level config with a concrete map path so create() can run safely
    BaseLevelConfig levelCfg = mock(BaseLevelConfig.class);
    when(levelCfg.getRows()).thenReturn(5);
    when(levelCfg.getCols()).thenReturn(10);
    when(levelCfg.getMapFile()).thenReturn("images/backgrounds/level_map_grass.png");
    when(configService.getLevelConfig(anyString())).thenReturn(levelCfg);

    // Provide defender/generator/item configs with asset paths for lists
    BaseDefenderConfig slingCfg = mock(BaseDefenderConfig.class);
    when(slingCfg.getAssetPath()).thenReturn("images/entities/defences/slingshooter.png");
    BaseDefenderConfig furnaceCfg = mock(BaseDefenderConfig.class);
    when(furnaceCfg.getAssetPath()).thenReturn("images/entities/defences/furnace.png");
    when(configService.getDefenderConfig("slingshooter")).thenReturn(slingCfg);
    when(configService.getDefenderConfig("furnace")).thenReturn(furnaceCfg);

    BaseItemConfig grenadeCfg = mock(BaseItemConfig.class);
    when(grenadeCfg.getAssetPath()).thenReturn("images/entities/items/grenade.png");
    when(configService.getItemConfig("grenade")).thenReturn(grenadeCfg);

    PhysicsService physicsService = mock(PhysicsService.class, RETURNS_DEEP_STUBS);
    PhysicsEngine physicsEngine = mock(PhysicsEngine.class, RETURNS_DEEP_STUBS);

    // Make sure getPhysics() returns the engine mock
    when(physicsService.getPhysics()).thenReturn(physicsEngine);

    // Ensure any call that could return a Box2D Body or similar is safe
    when(physicsEngine.createBody(any())).thenReturn(mock(Body.class));
    when(physicsEngine.getWorld()).thenReturn(mock(World.class));

    // Register in the ServiceLocator
    ServiceLocator.registerPhysicsService(physicsService);
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());

    BaseDefenderConfig wallCfg = mock(BaseDefenderConfig.class);
    when(wallCfg.getAtlasPath()).thenReturn("images/entities/defences/wall.atlas");
    when(configService.getDefenderConfig("wall")).thenReturn(wallCfg);

    TextureAtlas atlas = mock(TextureAtlas.class, RETURNS_DEEP_STUBS);
    when(resourceService.getAsset(anyString(), eq(TextureAtlas.class))).thenReturn(atlas);
    when(atlas.findRegions(anyString())).thenReturn(new com.badlogic.gdx.utils.Array<>());

    area.create(); // calls displayUI(), spawnMap(), spawnGrid(), overlay creation

    // Inspect private maps populated by displayUI()
    Field unitListF = LevelGameArea.class.getDeclaredField("unitList");
    Field itemListF = LevelGameArea.class.getDeclaredField("itemList");
    Field gameOverF = LevelGameArea.class.getDeclaredField("gameOverEntity");
    Field lvlCompleteF = LevelGameArea.class.getDeclaredField("levelCompleteEntity");
    unitListF.setAccessible(true);
    itemListF.setAccessible(true);
    gameOverF.setAccessible(true);
    lvlCompleteF.setAccessible(true);

    @SuppressWarnings("unchecked")
    Map<String, Supplier<Entity>> unitList = (Map<String, Supplier<Entity>>) unitListF.get(area);
    @SuppressWarnings("unchecked")
    Map<String, Supplier<Entity>> itemList = (Map<String, Supplier<Entity>>) itemListF.get(area);

    assertTrue(unitList.containsKey("images/entities/defences/slingshooter.png"));
    assertTrue(unitList.containsKey("images/entities/defences/furnace.png"));
    assertTrue(itemList.containsKey("images/entities/items/grenade.png"));

    assertNotNull(gameOverF.get(area)); // createGameOverEntity()
    assertNotNull(lvlCompleteF.get(area)); // LevelCompletedWindow()
  }

  @Test
  void create_loadsMapTextureWhenNotPreloaded() {
    final String path = "images/backgrounds/not_preloaded.png";
    BaseLevelConfig levelCfg = mock(BaseLevelConfig.class);
    when(levelCfg.getRows()).thenReturn(5);
    when(levelCfg.getCols()).thenReturn(10);
    when(levelCfg.getMapFile()).thenReturn(path);
    when(configService.getLevelConfig(anyString())).thenReturn(levelCfg);

    PhysicsService physicsService = mock(PhysicsService.class, RETURNS_DEEP_STUBS);
    PhysicsEngine physicsEngine = mock(PhysicsEngine.class, RETURNS_DEEP_STUBS);
    when(physicsService.getPhysics()).thenReturn(physicsEngine);
    ServiceLocator.registerPhysicsService(physicsService);

    Texture tex = mock(Texture.class);
    when(resourceService.getAsset(eq(path), eq(Texture.class))).thenReturn(null, tex);

    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());
    // Skip wall spawning to avoid PolygonShape native call
    doNothing().when(area).spawnWall();

    area.create();

    verify(resourceService).loadTextures(argThat(arr -> arr.length == 1 && path.equals(arr[0])));
    verify(resourceService).loadAll();
    verify(resourceService, atLeast(2)).getAsset(eq(path), eq(Texture.class));
  }

  @Test
  void checkGameOver_triggersOnce_andPlaysSound() throws Exception {
    // Level config only — enough for create() to build map/grid
    BaseLevelConfig levelCfg = mock(BaseLevelConfig.class);
    when(levelCfg.getRows()).thenReturn(5);
    when(levelCfg.getCols()).thenReturn(10);
    when(levelCfg.getMapFile()).thenReturn("images/backgrounds/level_map_grass.png");
    when(configService.getLevelConfig(anyString())).thenReturn(levelCfg);

    // Spy and skip spawnWall() to avoid Box2D
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());
    doNothing().when(area).spawnWall();

    area.create();

    // Register only what checkGameOver() uses
    SettingsService settings = mock(SettingsService.class);
    when(settings.getSoundVolume()).thenReturn(0.5f);
    ServiceLocator.registerSettingsService(settings);

    Sound goSound = mock(Sound.class);
    when(resourceService.getAsset(eq("sounds/game-over-voice.mp3"), eq(Sound.class)))
        .thenReturn(goSound);

    // Create a robot that has crossed the left edge
    float t = area.getTileSize();
    Entity robot = new Entity();
    robot.setPosition(area.getXOffset() - t * 1.1f, area.getYOffset());
    area.getRobots().add(robot);

    area.checkGameOver();

    Field f = LevelGameArea.class.getDeclaredField("isGameOver");
    f.setAccessible(true);
    assertTrue(f.getBoolean(area));
    verify(goSound, times(1)).play(eq(0.5f));

    // Idempotent re-check
    area.checkGameOver();
    verify(goSound, times(1)).play(anyFloat());
  }

  @Test
  void checkLevelComplete_tripsAtWaveFour_andIsIdempotent() throws Exception {
    // Simple level config for create()
    BaseLevelConfig levelCfg = mock(BaseLevelConfig.class);
    when(levelCfg.getRows()).thenReturn(5);
    when(levelCfg.getCols()).thenReturn(10);
    when(levelCfg.getMapFile()).thenReturn("images/backgrounds/level_map_grass.png");
    when(configService.getLevelConfig(anyString())).thenReturn(levelCfg);

    // Spy and bypass wall spawning
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());
    doNothing().when(area).spawnWall();

    area.create();

    // Wave service determines current wave index
    WaveService waves = mock(WaveService.class);
    when(waves.getCurrentWave()).thenReturn(4);
    ServiceLocator.registerWaveService(waves);

    Field flag = LevelGameArea.class.getDeclaredField("isLevelComplete");
    flag.setAccessible(true);
    assertFalse(flag.getBoolean(area));

    area.checkLevelComplete();
    assertTrue(flag.getBoolean(area));

    // Verify idempotence
    area.checkLevelComplete();
    assertTrue(flag.getBoolean(area));
  }

  @Test
  void create_spawnsOverlayAndGridWithoutError() {
    BaseLevelConfig levelCfg = mock(BaseLevelConfig.class);
    when(levelCfg.getRows()).thenReturn(5);
    when(levelCfg.getCols()).thenReturn(10);
    when(levelCfg.getMapFile()).thenReturn("images/backgrounds/level_map_grass.png");
    when(configService.getLevelConfig(anyString())).thenReturn(levelCfg);

    // Spy and skip Box2D wall creation
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea());
    doNothing().when(area).spawnWall();

    assertDoesNotThrow(area::create);
    assertNotNull(area.getGrid());
  }
}
