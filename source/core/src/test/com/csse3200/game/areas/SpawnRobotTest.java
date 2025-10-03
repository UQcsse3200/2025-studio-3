package com.csse3200.game.areas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.components.tile.TileHitboxComponent;
import com.csse3200.game.components.tile.TileInputComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.entities.factories.RobotFactory.RobotType;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.DiscordRichPresenceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

@ExtendWith(GameExtension.class)
class SpawnRobotTest {

  // Match LevelGameArea constants
  private static final int ROWS = 5;
  private static final int COLS = 10;

  // Fixed geometry for predictable math in tests
  private static final float X_OFFSET = 2.9f;
  private static final float Y_OFFSET = 1.45f;
  private static final float GRID_HEIGHT = 7f;
  private static final float CELL = GRID_HEIGHT / ROWS; // tileSize
  private static final float EPS = 1e-4f;

  private static void ensureRenderService() {
    RenderService rs = mock(RenderService.class);
    Stage stage = mock(Stage.class);
    when(stage.getWidth()).thenReturn(1920f);
    when(stage.getHeight()).thenReturn(1080f);
    when(rs.getStage()).thenReturn(stage);
    ServiceLocator.registerRenderService(rs);
  }

  @BeforeEach
  void setup() {
    ServiceLocator.clear();
    ServiceLocator.registerEntityService(new EntityService());
    ensureRenderService();
    DiscordRichPresenceService discordRichPresenceService = mock(DiscordRichPresenceService.class);
    ServiceLocator.registerDiscordRichPresenceService(discordRichPresenceService);
    lenient().doNothing().when(discordRichPresenceService).updateGamePresence(anyString(), anyInt());
  }

  private static void setPrivateField(Object target, String name, Object value) throws Exception {
    var f = target.getClass().getDeclaredField(name);
    f.setAccessible(true);
    f.set(target, value);
  }

  private static LevelGameArea newLevelAreaWithGeometry() throws Exception {
    ensureRenderService();
    LevelGameArea lvl = new LevelGameArea("levelOne");
    setPrivateField(lvl, "tileSize", CELL);
    setPrivateField(lvl, "xOffset", X_OFFSET);
    setPrivateField(lvl, "yOffset", Y_OFFSET);
    LevelGameGrid grid = new LevelGameGrid(ROWS, COLS);
    // Give the grid real tiles that have TileStorageComponent (delegates to grid)
    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        Entity tile =
            new Entity()
                .addComponent(new TileStorageComponent(lvl))
                .addComponent(new TileHitboxComponent(5, 5, 0, 0))
                .addComponent(new TileInputComponent(lvl));
        tile.getComponent(TileStorageComponent.class).setPosition(r * COLS + c);
        grid.addTile(r * COLS + c, tile);
      }
    }
    lvl.setGrid(grid);
    return lvl;
  }

  private static int idx(int row, int col) {
    return row * COLS + col;
  }

  private static float worldX(float colLike) {
    return X_OFFSET + CELL * colLike;
  }

  private static float worldY(int row) {
    return Y_OFFSET + CELL * row;
  }

  @Test
  void spawnRobot_placesAtBottomLeftOfTile_andScalesHeight() throws Exception {
    LevelGameArea lvl = newLevelAreaWithGeometry();

    try (MockedStatic<RobotFactory> mocked = mockStatic(RobotFactory.class)) {
      Entity dummy = new Entity();
      mocked.when(() -> RobotFactory.createRobotType(RobotType.STANDARD)).thenReturn(dummy);

      int col = 3, row = 1;
      lvl.spawnRobot(col, row, RobotType.STANDARD);

      assertEquals(worldX(col), dummy.getPosition().x, EPS);
      assertEquals(worldY(row), dummy.getPosition().y, EPS);

      assertEquals(CELL, dummy.getScale().y, EPS);
    }
  }

  @Test
  void spawnRobot_clampsOutOfBounds_lowAndHigh() throws Exception {
    LevelGameArea lvl = newLevelAreaWithGeometry();

    try (MockedStatic<RobotFactory> mocked = mockStatic(RobotFactory.class)) {
      Entity e1 = new Entity();
      Entity e2 = new Entity();
      mocked
          .when(() -> RobotFactory.createRobotType(any()))
          .thenReturn(e1) // first call
          .thenReturn(e2); // second call

      lvl.spawnRobot(-5, -7, RobotType.STANDARD);
      assertEquals(worldX(0), e1.getPosition().x, EPS);
      assertEquals(worldY(0), e1.getPosition().y, EPS);

      lvl.spawnRobot(999, 999, RobotType.FAST);
      assertEquals(worldX(COLS - 1), e2.getPosition().x, EPS);
      assertEquals(worldY(ROWS - 1), e2.getPosition().y, EPS);
    }
  }

  @Test
  void spawnRobot_registersEntityOnce() throws Exception {
    // fresh service so we can verify calls
    ServiceLocator.clear();
    EntityService es = mock(EntityService.class);
    ServiceLocator.registerEntityService(es);

    LevelGameArea lvl = newLevelAreaWithGeometry();

    try (MockedStatic<RobotFactory> mocked = mockStatic(RobotFactory.class)) {
      Entity dummy = new Entity();
      mocked.when(() -> RobotFactory.createRobotType(RobotType.STANDARD)).thenReturn(dummy);

      lvl.spawnRobot(2, 2, RobotType.STANDARD);
      verify(es, times(1)).register(same(dummy));
    }
  }

  @Test
  void spawnRobotOnDefence_noDefence_doesNothing() throws Exception {
    LevelGameArea lvl = newLevelAreaWithGeometry();

    try (MockedStatic<RobotFactory> rf = mockStatic(RobotFactory.class)) {
      lvl.spawnRobotOnDefence(RobotType.STANDARD);
      rf.verify(() -> RobotFactory.createRobotType(any()), times(0));
    }
  }

  @Test
  void spawnRobotOnDefence_spawnsRightOfRightmostDefence_sameRow() throws Exception {
    LevelGameArea lvl = newLevelAreaWithGeometry();

    lvl.getGrid().placeOccupantIndex(idx(2, 6), new Entity());

    Entity spawned = spy(new Entity());
    try (MockedStatic<RobotFactory> rf = mockStatic(RobotFactory.class)) {
      rf.when(() -> RobotFactory.createRobotType(RobotType.STANDARD)).thenReturn(spawned);

      lvl.spawnRobotOnDefence(RobotType.STANDARD);

      assertEquals(worldX(6 + 0.5f), spawned.getPosition().x, EPS);
      assertEquals(worldY(2), spawned.getPosition().y, EPS);
    }
  }

  @Test
  void spawnRobotOnDefence_picksRightmostAcrossRows_andClampsAtRightEdge() throws Exception {
    LevelGameArea lvl = newLevelAreaWithGeometry();

    // Defences at (1,3) and (4,9) -> rightmost is (4,9)
    lvl.getGrid().placeOccupantIndex(idx(1, 3), new Entity());
    lvl.getGrid().placeOccupantIndex(idx(4, 9), new Entity());

    Entity spawned = spy(new Entity());
    try (MockedStatic<RobotFactory> rf = mockStatic(RobotFactory.class)) {
      rf.when(() -> RobotFactory.createRobotType(RobotType.FAST)).thenReturn(spawned);

      lvl.spawnRobotOnDefence(RobotType.FAST);

      float spawnCol = Math.min(9 + 0.5f, COLS - 0.01f);
      assertEquals(worldX(spawnCol), spawned.getPosition().x, EPS);
      assertEquals(worldY(4), spawned.getPosition().y, EPS);
    }
  }
}
