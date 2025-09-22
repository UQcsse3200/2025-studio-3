package com.csse3200.game.components.tile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.areas.LevelGameGrid;
import com.csse3200.game.entities.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TileInputComponentTest {

  @Mock AreaAPI area;

  private static final float TILE = 32f;

  private Entity makeTileAt(float x, float y, TileStorageComponent storage) {
    TileInputComponent comp = new TileInputComponent(area);
    Entity e = new Entity().addComponent(storage).addComponent(comp);
    e.setPosition(x, y);
    return e;
  }

  @BeforeEach
  void beforeEach() {
    lenient().when(area.getTileSize()).thenReturn(TILE);
    lenient()
        .when(area.stageToWorld(any()))
        .thenAnswer(
            inv -> {
              GridPoint2 p = inv.getArgument(0);
              return new GridPoint2(p.x, p.y);
            });
  }

  @Test
  void insideLeftWhenEmptyAndSelectedTriggersSpawnAndReturnsTrue() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    when(area.getSelectedUnit()).thenReturn(new Entity());
    LevelGameGrid mockGrid = mock(LevelGameGrid.class);
    when(area.getGrid()).thenReturn(mockGrid);
    when(mockGrid.isOccupiedIndex(anyInt())).thenReturn(true);
    Entity tile = makeTileAt(10, 20, storage);
    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertTrue(handled);
    verify(area).spawnUnit(anyInt());
    verify(storage, never()).removeTileUnit();
  }

  @Test
  void insideLeftWhenNoSelectedDoesNotSpawnAndReturnsFalse() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    when(area.getSelectedUnit()).thenReturn(null);

    Entity tile = makeTileAt(10, 20, storage);
    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertFalse(handled);
    verify(area, never()).spawnUnit(anyInt());
    verify(area, never()).removeUnit(anyInt());
  }

  @Test
  void insideRightRemovesAndReturnsTrue() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = makeTileAt(10, 20, storage);
    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.RIGHT);

    assertTrue(handled);
    verify(area).removeUnit(anyInt());
    verify(area, never()).spawnUnit(anyInt());
  }

  @Test
  void insideOtherReturnsFalseAndDoesNothing() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = makeTileAt(10, 20, storage);

    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.MIDDLE);

    assertFalse(handled);
    verify(area, never()).spawnUnit(anyInt());
    verify(storage, never()).removeTileUnit();
  }

  @Test
  void outsideReturnsFalseAndDoesNothing() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = makeTileAt(100, 200, storage);

    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertFalse(handled);
    verify(area, never()).spawnUnit(anyInt());
    verify(storage, never()).removeTileUnit();
  }

  @Test
  void stageToWorldIsRespected() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = makeTileAt(10, 20, storage);

    doReturn(new GridPoint2(0, 0)).when(area).stageToWorld(any(GridPoint2.class));

    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertFalse(handled);
    verify(area, never()).spawnUnit(anyInt());
    verify(storage, never()).removeTileUnit();
  }
}
