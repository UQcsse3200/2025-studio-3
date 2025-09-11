package com.csse3200.game.areas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LevelGameAreaTest {
  @Mock TerrainFactory terrainFactory;
  @Mock RenderService renderService;
  @Mock Stage stage;
  @Mock ResourceService resourceService;
  @Mock Music music;

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

    when(renderService.getStage()).thenReturn(stage);
    // second value allows testing of resize
    when(stage.getWidth()).thenReturn(1920f).thenReturn(1200f);
    when(stage.getHeight()).thenReturn(1080f).thenReturn(800f);

    when(resourceService.loadForMillis(anyInt())).thenReturn(false).thenReturn(true);
    when(resourceService.getProgress()).thenReturn(1);
    when(resourceService.getAsset(eq("sounds/BGM_03_mp3.mp3"), eq(Music.class))).thenReturn(music);
    when(resourceService.getAsset(anyString(), eq(com.badlogic.gdx.graphics.Texture.class)))
        .thenReturn(mock(com.badlogic.gdx.graphics.Texture.class));
  }

  @AfterEach
  void afterEach() {
    try {
      ServiceLocator.clear();
    } catch (Throwable ignored) {
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
  void selectingUnit_spawnsAndPositionsSelectionStar() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    // simple inventory entity
    Entity unit = new Entity().addComponent(new TextureRenderComponent("images/ghost_1png"));
    unit.setPosition(100f, 50f);

    area.setSelectedUnit(unit);

    assertFalse(area.spawned.isEmpty(), "expected a star to be spawned");
    Entity star = area.spawned.get(0);

    TextureRenderComponent tex = star.getComponent(TextureRenderComponent.class);
    assertNotNull(tex, "Star should have a texture");

    // Star in correct x position
    assertEquals(unit.getCenterPosition().x, star.getPosition().x, 0.001f);
  }

  @Test
  void setSelectedUnit_nullMovesStarOffscreen() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    // select a real unit first so the star is created
    var unit =
        new Entity()
            .addComponent(
                new TextureRenderComponent(
                    new com.badlogic.gdx.graphics.Texture(
                        new com.badlogic.gdx.graphics.Pixmap(
                            1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888))));
    unit.setPosition(10f, 10f);
    area.setSelectedUnit(unit);

    // last spawned is the star the method created
    Entity star = area.spawned.get(area.spawned.size() - 1);

    // now hide it
    area.setSelectedUnit(null);

    assertEquals(-100f, star.getPosition().x, 0.0001f);
    assertEquals(-100f, star.getPosition().y, 0.0001f);
  }

  @Test
  void spawnUnit_placesOnGrid_andCallsTileStorage() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    // Create a selected unit
    Entity selected = new Entity().addComponent(new TextureRenderComponent("images/ghost_1png"));
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
  void spawnUnit_noTile_doesNotCallTileStorage() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    var tex =
        new com.badlogic.gdx.graphics.Texture(
            new com.badlogic.gdx.graphics.Pixmap(
                1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888));
    area.setSelectedUnit(new Entity().addComponent(new TextureRenderComponent(tex)));

    LevelGameGrid grid = mock(LevelGameGrid.class);
    when(grid.getTileFromXY(anyFloat(), anyFloat())).thenReturn(null);
    area.setGrid(grid);

    // should not throw; still spawns the unit
    area.spawnUnit(3);
    assertFalse(area.spawned.isEmpty());

    verify(grid).getTileFromXY(anyFloat(), anyFloat());
  }

  @Test
  void create_loadsAssetsSpawnsThingsAndStartsMusic() {
    CapturingLevelGameArea area = spy(new CapturingLevelGameArea(terrainFactory));

    // Avoid robot factory static
    doNothing().when(area).spawnRobot(anyInt(), anyInt(), anyString());

    var terrain = mock(com.csse3200.game.areas.terrain.TerrainComponent.class);
    when(terrain.getTileSize()).thenReturn(64f);
    when(terrain.getMapBounds(eq(0))).thenReturn(new GridPoint2(12, 6));
    when(terrainFactory.createTerrain(any())).thenReturn(terrain);

    area.create();

    verify(resourceService).loadTextures(any(String[].class));
    verify(resourceService).loadTextureAtlases(any(String[].class));
    verify(resourceService).loadSounds(any(String[].class));
    verify(resourceService).loadMusic(any(String[].class));
    verify(music).setLooping(true);
    verify(music).setVolume(0.3f);
    verify(music).play();

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
  void resize_changesScaling() {
    // Given an area built with the initial stage size
    LevelGameArea area = spy(new LevelGameArea(terrainFactory));
    float tileBefore = area.getTileSize();

    area.resize();
    verify(area, times(1)).setScaling();

    float tileAfter = area.getTileSize();

    assertNotEquals(tileBefore, tileAfter, "resize() should recompute tileSize");
  }
}
