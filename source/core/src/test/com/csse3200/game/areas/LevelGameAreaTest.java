package com.csse3200.game.areas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.areas.terrain.TerrainComponent;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.items.ItemComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ItemEffectsService;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.DragOverlay;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class LevelGameAreaTest {
  @Mock TerrainFactory terrainFactory;
  @Mock RenderService renderService;
  @Mock Stage stage;
  @Mock ResourceService resourceService;
  @Mock Music music;
  @Mock ProfileService profileService;

  private MockedStatic<Persistence> persistenceMock;
  private Profile profile;

  /** A class to capture spawned entities without needing a full ECS */
  static class CapturingLevelGameArea extends LevelGameArea {
    final List<Entity> spawned = new ArrayList<>();

    CapturingLevelGameArea(TerrainFactory factory) {
      super(factory);
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

    lenient().when(renderService.getStage()).thenReturn(stage);
    // second value allows testing of resize
    lenient().when(stage.getWidth()).thenReturn(1920f);
    lenient().when(stage.getHeight()).thenReturn(1080f);

    lenient()
        .when(resourceService.getAsset(eq("sounds/BGM_03_mp3.mp3"), eq(Music.class)))
        .thenReturn(music);
    lenient()
        .when(resourceService.getAsset(anyString(), eq(Texture.class)))
        .thenReturn(mock(Texture.class));

    lenient().when(resourceService.loadForMillis(anyInt())).thenReturn(true);
    lenient().when(resourceService.getProgress()).thenReturn(1);

    profile = new Profile();
    profile.getInventory().addItem("grenade"); // so inventory not null
    lenient().when(profileService.getProfile()).thenReturn(profile);

    persistenceMock = mockStatic(Persistence.class, withSettings().strictness(Strictness.LENIENT));
    // Note: Persistence.profile() no longer exists in the reworked system
  }

  @AfterEach
  void afterEach() {
    try {
      ServiceLocator.clear();
      if (persistenceMock != null) {
        persistenceMock.close();
      }
    } catch (Throwable ignored) {
      // Ignore throwable and continue to next test
    }
  }

  @Test
  void setScalingUsesStageSizeAndRendererWidth() {
    LevelGameArea area = new LevelGameArea(terrainFactory);

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
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

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
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    // Selected entity is an Item of type GRENADE (matches your seeded key)
    ItemComponent item = mock(ItemComponent.class);
    when(item.getType()).thenReturn(ItemComponent.Type.GRENADE);
    Entity itemEntity = new Entity().addComponent(item);
    area.setSelectedUnit(new Entity().addComponent(new DeckInputComponent(area, () -> itemEntity)));

    // Arrange a tile with storage
    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = new Entity().addComponent(storage);
    LevelGameGrid grid = mock(LevelGameGrid.class);
    when(grid.getTileFromXY(anyFloat(), anyFloat())).thenReturn(tile);
    area.setGrid(grid);

    // Use profile from @BeforeEach and make it NOT contain the item
    profile.getInventory().removeItem("grenade");

    area.spawnUnit(1);

    verify(storage).removeTileUnit();
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void spawnUnitItemInInventoryConsumesOnePlaysEffectAndClearsTile() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    ItemComponent item = mock(ItemComponent.class);
    when(item.getType()).thenReturn(ItemComponent.Type.GRENADE);
    Entity grenade = new Entity().addComponent(item);
    area.setSelectedUnit(new Entity().addComponent(new DeckInputComponent(area, () -> grenade)));

    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = new Entity().addComponent(storage);
    LevelGameGrid grid = mock(LevelGameGrid.class);
    when(grid.getTileFromXY(anyFloat(), anyFloat())).thenReturn(tile);
    area.setGrid(grid);

    ItemEffectsService effects = mock(ItemEffectsService.class);
    ServiceLocator.registerItemEffectsService(effects);

    area.spawnUnit(2);

    verify(effects).playEffect(anyString(), any(Vector2.class), anyInt(), any(Vector2.class));
    assertFalse(ServiceLocator.getProfileService().getProfile().getInventory().contains("grenade"));
    verify(storage).removeTileUnit();
  }

  @Test
  void spawnUnitPlacesOnGridAndCallsTileStorage() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    // Create a selected unit
    Entity selected =
        new Entity()
            .addComponent(
                new DeckInputComponent(
                    area,
                    () ->
                        new Entity()
                            .addComponent(new TextureRenderComponent("images/ghost_1.png"))));
    area.setSelectedUnit(selected);

    LevelGameGrid grid = mock(LevelGameGrid.class);
    Entity tile = new Entity().addComponent(mock(TileStorageComponent.class));
    when(grid.getTileFromXY(anyFloat(), anyFloat())).thenReturn(tile);
    area.setGrid(grid);

    int position = 7;
    area.spawnUnit(position);

    assertFalse(area.spawned.isEmpty());
    Entity spawned = area.spawned.get(area.spawned.size() - 1);
    assertNotNull(spawned.getComponent(TextureRenderComponent.class));

    TileStorageComponent storage = tile.getComponent(TileStorageComponent.class);
    verify(storage).setTileUnit(spawned);
  }

  @Test
  void spawnUnitNoTileDoesNotCallTileStorage() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    Entity selected =
        new Entity()
            .addComponent(
                new DeckInputComponent(
                    area,
                    () ->
                        new Entity()
                            .addComponent(new TextureRenderComponent("images/ghost_1.png"))));
    area.setSelectedUnit(selected);

    LevelGameGrid grid = mock(LevelGameGrid.class);
    when(grid.getTileFromXY(anyFloat(), anyFloat())).thenReturn(null);
    area.setGrid(grid);

    // should not throw; still spawns the unit
    area.spawnUnit(3);
    assertFalse(area.spawned.isEmpty());

    verify(grid).getTileFromXY(anyFloat(), anyFloat());
  }

  @Test
  void createLoadsAssetsSpawnsThingsAndStartsMusic() {
    // Use a spy so we can verify calls to spawn methods
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    // Avoid robot factory static
    lenient().doNothing().when(area).spawnRobot(anyInt(), anyInt(), any());

    // Mock the TerrainComponent returned by the TerrainFactory
    var terrain = mock(TerrainComponent.class);

    // Only mark as lenient if you expect it might not be called
    lenient().when(terrain.getTileSize()).thenReturn(64f);
    lenient().when(terrain.getMapBounds(0)).thenReturn(new GridPoint2(12, 6));

    // Ensure the TerrainFactory returns the mock terrain
    when(terrainFactory.createTerrain(any())).thenReturn(terrain);

    // Simulate resource service loading
    when(resourceService.loadForMillis(anyInt())).thenReturn(false).thenReturn(true);
    when(resourceService.getProgress()).thenReturn(0).thenReturn(1);

    // Run the create() method
    area.create();

    // Verify assets loaded
    verify(resourceService).loadTextures(any(String[].class));
    verify(resourceService).loadTextureAtlases(any(String[].class));
    verify(resourceService).loadSounds(any(String[].class));
    verify(resourceService).loadMusic(any(String[].class));

    // Verify music started
    verify(music).setLooping(true);
    verify(music).setVolume(0.3f);
    verify(music).play();

    // Ensure entities were spawned
    assertFalse(area.spawned.isEmpty());
  }

  @Test
  void disposeStopsMusicAndUnloadsAssets() {
    CapturingLevelGameArea area = new CapturingLevelGameArea(terrainFactory);
    area.dispose();
    verify(music).stop();
    verify(resourceService, atLeastOnce()).unloadAssets(any(String[].class));
  }

  @Test
  void getters() {
    LevelGameGrid grid = mock(LevelGameGrid.class);
    LevelGameArea area = new LevelGameArea(terrainFactory);
    area.setGrid(grid);

    assertSame(grid, area.getGrid());
    assertNull(area.getSelectedUnit());
  }

  @Test
  void resizeChangesScaling() {
    // Given an area built with the initial stage size
    LevelGameArea area = spy(new LevelGameArea(terrainFactory));
    float tileBefore = area.getTileSize();

    // change the 'window' size
    when(stage.getWidth()).thenReturn(1200f);
    when(stage.getHeight()).thenReturn(800f);

    area.resize();

    verify(area, times(1)).setScaling();
    assertNotEquals(tileBefore, area.getTileSize(), "resize() should recompute tileSize");
  }

  @Test
  void dragNullsAreNoop() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    // dragOverlay is null until create(); also pass null texture
    assertDoesNotThrow(() -> area.beginDrag(null));
    assertDoesNotThrow(area::cancelDrag);
  }

  @Test
  void characterSelectedRoundTrip() {
    LevelGameArea area = new LevelGameArea(terrainFactory);
    assertFalse(area.isCharacterSelected());
    area.setIsCharacterSelected(true);
    assertTrue(area.isCharacterSelected());
  }

  @Test
  void spawnRobotOnDefenceWithNullGridIsNoop() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));
    area.spawnRobotOnDefence(RobotFactory.RobotType.STANDARD);
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void spawnRobotOnDefenceWithoutAnyDefenceIsNoop() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    LevelGameGrid grid = mock(LevelGameGrid.class);
    TileStorageComponent storage = mock(TileStorageComponent.class);
    when(storage.getTileUnit()).thenReturn(null);
    Entity tile = new Entity().addComponent(storage);

    when(grid.getTileFromXY(anyFloat(), anyFloat())).thenReturn(tile);
    area.setGrid(grid);

    area.spawnRobotOnDefence(RobotFactory.RobotType.STANDARD);
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void beginDrag_overlayNull_textureNull_doesNothing() {
    CapturingLevelGameArea area = new CapturingLevelGameArea(terrainFactory);

    // overlay = null, texture = null → if short-circuits, nothing happens
    assertDoesNotThrow(() -> area.beginDrag(null));
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void beginDrag_overlayNull_textureNotNull_doesNothing() {
    CapturingLevelGameArea area = new CapturingLevelGameArea(terrainFactory);

    Texture mockTexture = mock(Texture.class);
    // overlay = null, texture != null → left side false, nothing happens
    assertDoesNotThrow(() -> area.beginDrag(mockTexture));
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void beginDrag_overlayNotNull_textureNull_doesNothing() throws Exception {
    CapturingLevelGameArea area = new CapturingLevelGameArea(terrainFactory);

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
    CapturingLevelGameArea area = new CapturingLevelGameArea(terrainFactory);

    // overlay = null → if condition false, nothing happens
    assertDoesNotThrow(area::cancelDrag);
    assertTrue(area.spawned.isEmpty());
  }

  @Test
  void beginDrag_overlayAndTextureNotNull_callsBegin() throws Exception {
    CapturingLevelGameArea area = new CapturingLevelGameArea(terrainFactory);

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
    CapturingLevelGameArea area = new CapturingLevelGameArea(terrainFactory);

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
}
