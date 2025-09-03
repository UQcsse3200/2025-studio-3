package com.csse3200.game.areas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

@ExtendWith(GameExtension.class)
class SpawnRobotTest {

    static class TestArea extends GameArea {
        @Override public void create() {}
        public Entity spawnOnCell(GridPoint2 cell, boolean cx, boolean cy) {
            return spawnRobotAtTile(cell, cx, cy);
        }
        public Entity spawnAt(float x, float y) { return spawnRobotAtFloat(x, y); }
    }

    private TestArea area;

    // Overlay grid constants, match GameArea implementation
    private static final float X_OFFSET = 2.9f;
    private static final float Y_OFFSET = 1.45f;
    private static final float GRID_HEIGHT = 7f;
    private static final int ROWS = 5;
    private static final int COLS = 10;
    private static final float CELL = GRID_HEIGHT / ROWS;
    private static final float EPS = 1e-4f; // float comparison epsilon

    @BeforeEach
    void setup() {
        ServiceLocator.clear();
        ServiceLocator.registerEntityService(new EntityService());
        area = new TestArea();
    }

    @Test
    void spawnAtTile_noCenter_placesAtBottomLeft() {
        try (MockedStatic<com.csse3200.game.entities.factories.RobotFactory> mocked =
                     mockStatic(com.csse3200.game.entities.factories.RobotFactory.class)) {
            Entity dummy = new Entity();
            mocked.when(() -> com.csse3200.game.entities.factories.RobotFactory.createStandardRobot())
                    .thenReturn(dummy);

            GridPoint2 cell = new GridPoint2(3, 1); // col=3,row=1
            Entity robot = area.spawnOnCell(cell, false, false);
            assertSame(dummy, robot);

            float expectedX = X_OFFSET + CELL * cell.x;
            float expectedY = Y_OFFSET + CELL * cell.y;
            Vector2 pos = robot.getPosition();

            assertEquals(expectedX, pos.x, EPS);
            assertEquals(expectedY, pos.y, EPS);
        }
    }

    @Test
    void spawnAtTile_centered_alignsEntityCenterToCellCenter() {
        try (MockedStatic<com.csse3200.game.entities.factories.RobotFactory> mocked =
                     mockStatic(com.csse3200.game.entities.factories.RobotFactory.class)) {
            Entity dummy = new Entity();
            mocked.when(() -> com.csse3200.game.entities.factories.RobotFactory.createStandardRobot())
                    .thenReturn(dummy);

            GridPoint2 cell = new GridPoint2(7, 3);
            Entity robot = area.spawnOnCell(cell, true, true);

            float cellCenterX = X_OFFSET + CELL * cell.x + CELL / 2f;
            float cellCenterY = Y_OFFSET + CELL * cell.y + CELL / 2f;

            Vector2 center = robot.getCenterPosition(); // pos + (scale/2)
            assertEquals(cellCenterX, center.x, EPS);
            assertEquals(cellCenterY, center.y, EPS);
        }
    }

    @Test
    void spawnAtFloat_setsExactWorldPosition() {
        try (MockedStatic<com.csse3200.game.entities.factories.RobotFactory> mocked =
                     mockStatic(com.csse3200.game.entities.factories.RobotFactory.class)) {
            Entity dummy = new Entity();
            mocked.when(() -> com.csse3200.game.entities.factories.RobotFactory.createStandardRobot())
                    .thenReturn(dummy);

            float x = 12.34f, y = 5.67f;
            Entity robot = area.spawnAt(x, y);
            assertSame(dummy, robot);
            assertEquals(x, robot.getPosition().x, EPS);
            assertEquals(y, robot.getPosition().y, EPS);
        }
    }

    @Test
    void centerX_only_alignsX_center_keepsY_bottomLeft() {
        try (var mocked = mockStatic(com.csse3200.game.entities.factories.RobotFactory.class)) {
            Entity dummy = new Entity();
            mocked.when(() -> com.csse3200.game.entities.factories.RobotFactory.createStandardRobot())
                    .thenReturn(dummy);

            GridPoint2 cell = new GridPoint2(4, 2);
            Entity robot = area.spawnOnCell(cell, true, false);

            float tileX = X_OFFSET + CELL * cell.x;
            float tileY = Y_OFFSET + CELL * cell.y;
            float cellCenterX = tileX + CELL / 2f;

            // X centred: entity center X equals cell center X
            assertEquals(cellCenterX, robot.getCenterPosition().x, EPS);
            // Y not centred: position Y equals bottom-left of the cell
            assertEquals(tileY, robot.getPosition().y, EPS);
        }
    }

    @Test
    void centerY_only_alignsY_center_keepsX_bottomLeft() {
        try (var mocked = mockStatic(com.csse3200.game.entities.factories.RobotFactory.class)) {
            Entity dummy = new Entity();
            mocked.when(() -> com.csse3200.game.entities.factories.RobotFactory.createStandardRobot())
                    .thenReturn(dummy);

            GridPoint2 cell = new GridPoint2(2, 3);
            Entity robot = area.spawnOnCell(cell, false, true);

            float tileX = X_OFFSET + CELL * cell.x;
            float tileY = Y_OFFSET + CELL * cell.y;
            float cellCenterY = tileY + CELL / 2f;

            assertEquals(tileX, robot.getPosition().x, EPS);        // X not centred
            assertEquals(cellCenterY, robot.getCenterPosition().y, EPS); // Y centred
        }
    }

    @Test
    void registersExactlyOnce_withSameInstance() {
        ServiceLocator.clear();
        EntityService es = mock(EntityService.class);
        ServiceLocator.registerEntityService(es);
        area = new TestArea();

        try (var mocked = mockStatic(com.csse3200.game.entities.factories.RobotFactory.class)) {
            Entity dummy = new Entity();
            mocked.when(() -> com.csse3200.game.entities.factories.RobotFactory.createStandardRobot())
                    .thenReturn(dummy);

            Entity robot = area.spawnOnCell(new GridPoint2(0, 0), false, false);
            assertSame(dummy, robot);
            verify(es, times(1)).register(same(dummy));
        }
    }

    @Test
    void corners_mapCorrectly() {
        try (var mocked = mockStatic(com.csse3200.game.entities.factories.RobotFactory.class)) {
            mocked.when(() -> com.csse3200.game.entities.factories.RobotFactory.createStandardRobot())
                    .thenAnswer(inv -> new Entity());

            // (0,0)
            Entity a = area.spawnOnCell(new GridPoint2(0, 0), false, false);
            assertEquals(X_OFFSET, a.getPosition().x, EPS);
            assertEquals(Y_OFFSET, a.getPosition().y, EPS);

            // (cols-1, rows-1)
            Entity b = area.spawnOnCell(new GridPoint2(COLS - 1, ROWS - 1), false, false);
            assertEquals(X_OFFSET + CELL * (COLS - 1), b.getPosition().x, EPS);
            assertEquals(Y_OFFSET + CELL * (ROWS - 1), b.getPosition().y, EPS);
        }
    }

    @Test
    void multipleRobots_independentPositions() {
        try (var mocked = mockStatic(com.csse3200.game.entities.factories.RobotFactory.class)) {
            mocked.when(() -> com.csse3200.game.entities.factories.RobotFactory.createStandardRobot())
                    .thenAnswer(inv -> new Entity());

            Entity r1 = area.spawnOnCell(new GridPoint2(1, 1), true, true);
            Entity r2 = area.spawnOnCell(new GridPoint2(8, 4), true, true);

            assertNotEquals(r1, r2);
            assertEquals(X_OFFSET + CELL * 1 + CELL/2f, r1.getCenterPosition().x, EPS);
            assertEquals(Y_OFFSET + CELL * 1 + CELL/2f, r1.getCenterPosition().y, EPS);
            assertEquals(X_OFFSET + CELL * 8 + CELL/2f, r2.getCenterPosition().x, EPS);
            assertEquals(Y_OFFSET + CELL * 4 + CELL/2f, r2.getCenterPosition().y, EPS);
        }
    }

    @Test
    void terrainNull_doesNotAffectOverlaySpawn() {
        area.terrain = null; // explicit
        try (var mocked = mockStatic(com.csse3200.game.entities.factories.RobotFactory.class)) {
            Entity dummy = new Entity();
            mocked.when(() -> com.csse3200.game.entities.factories.RobotFactory.createStandardRobot())
                    .thenReturn(dummy);
            assertDoesNotThrow(() -> area.spawnOnCell(new GridPoint2(2, 2), false, false));
        }
    }
}
