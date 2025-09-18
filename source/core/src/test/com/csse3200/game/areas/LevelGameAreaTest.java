package com.csse3200.game.areas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.areas.terrain.TerrainComponent;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
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
  void setScaling_usesStageSizeAndRendererWidth() {
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
    doNothing().when(area).spawnRobot(anyInt(), anyInt(), any());

    // Mock the TerrainComponent returned by the TerrainFactory
    var terrain = mock(TerrainComponent.class);

    // Only mark as lenient if you expect it might not be called
    lenient().when(terrain.getTileSize()).thenReturn(64f);
    lenient().when(terrain.getMapBounds(0)).thenReturn(new GridPoint2(12, 6));

    // Ensure the TerrainFactory returns the mock terrain
    when(terrainFactory.createTerrain(any())).thenReturn(terrain);

    // Simulate resource service loading
    when(resourceService.loadForMillis(anyInt())).thenReturn(false).thenReturn(true);
    when(resourceService.getProgress()).thenReturn(1);

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
  void dispose_stopsMusicAndUnloadsAssets() {
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
}
