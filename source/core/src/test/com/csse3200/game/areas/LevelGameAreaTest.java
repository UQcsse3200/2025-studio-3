package com.csse3200.game.areas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.items.ItemComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.BossFactory;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.DiscordRichPresenceService;
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
  void spawnBossShouldCreateAndPlaceBossCorrectly() {
    try (MockedStatic<BossFactory> mockedBossFactory = mockStatic(BossFactory.class)) {
      CapturingLevelGameArea area = spy(new CapturingLevelGameArea());
      ServiceLocator.registerGameArea(area);
      area.setScaling();

      Entity fakeBoss = new Entity().addComponent(new HitboxComponent());
      mockedBossFactory
          .when(() -> BossFactory.createBossType(any(BossFactory.BossTypes.class)))
          .thenReturn(fakeBoss);

      area.spawnBoss(2, BossFactory.BossTypes.SCRAP_TITAN);

      mockedBossFactory.verify(() -> BossFactory.createBossType(BossFactory.BossTypes.SCRAP_TITAN));
      assertEquals(1, area.spawned.size());
      Entity spawnedBoss = area.spawned.get(0);
      assertEquals(fakeBoss, spawnedBoss);

      float expectedTileSize = (720f * (Renderer.GAME_SCREEN_WIDTH / 1280f)) / 8f;
      float expectedXOffset = 2f * expectedTileSize;
      float expectedYOffset = 1f * expectedTileSize;
      int levelCols = 10;
      int spawnRow = 2;
      float expectedX = expectedXOffset + expectedTileSize * levelCols;
      float expectedY = expectedYOffset + expectedTileSize * spawnRow - (expectedTileSize / 1.5f);
      float expectedScaleY = expectedTileSize * 3.0f;

      Vector2 bossPos = spawnedBoss.getPosition();
      assertEquals(expectedX, bossPos.x, 0.01f);
      assertEquals(expectedY, bossPos.y, 0.01f);
      assertEquals(expectedScaleY, spawnedBoss.getScale().y, 0.01f);
    }
  }
}
