package com.csse3200.game.components.tile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.AreaAPI;
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
    when(storage.hasUnit()).thenReturn(false);
    when(area.getSelectedUnit()).thenReturn(new Entity());
    when(area.isCharacterSelected()).thenReturn(true);

    Entity tile = makeTileAt(10, 20, storage);
    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertTrue(handled);
    verify(storage).triggerSpawnUnit();
    verify(storage, never()).removeTileUnit();
  }

  @Test
  void insideLeftWhenEmptyAndNoSelectedDoesNotSpawnButReturnsTrue() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    when(storage.hasUnit()).thenReturn(false);
    when(area.getSelectedUnit()).thenReturn(null);

    Entity tile = makeTileAt(10, 20, storage);
    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertTrue(handled);
    verify(storage, never()).triggerSpawnUnit();
    verify(storage, never()).removeTileUnit();
  }

  @Test
  void insideLeftWhenOccupiedDoesNotSpawnButReturnsTrue() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    when(storage.hasUnit()).thenReturn(true);
    when(area.getSelectedUnit()).thenReturn(new Entity());

    Entity tile = makeTileAt(10, 20, storage);
    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertTrue(handled);
    verify(storage, never()).triggerSpawnUnit();
    verify(storage, never()).removeTileUnit();
  }

  @Test
  void insideRightWhenOccupiedRemovesAndReturnsTrue() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    when(storage.hasUnit()).thenReturn(true);

    Entity tile = makeTileAt(10, 20, storage);
    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.RIGHT);

    assertTrue(handled);
    verify(storage).removeTileUnit();
    verify(storage, never()).triggerSpawnUnit();
  }

  @Test
  void insideRightWhenEmptyDoesNothingButReturnsTrue() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    when(storage.hasUnit()).thenReturn(false);

    Entity tile = makeTileAt(10, 20, storage);
    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.RIGHT);

    assertTrue(handled);
    verify(storage, never()).removeTileUnit();
    verify(storage, never()).triggerSpawnUnit();
  }

  @Test
  void insideOtherReturnsFalseAndDoesNothing() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = makeTileAt(10, 20, storage);

    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.MIDDLE);

    assertFalse(handled);
    verify(storage, never()).triggerSpawnUnit();
    verify(storage, never()).removeTileUnit();
  }

  @Test
  void outsideReturnsFalseAndDoesNothing() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    Entity tile = makeTileAt(100, 200, storage);

    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertFalse(handled);
    verify(storage, never()).triggerSpawnUnit();
    verify(storage, never()).removeTileUnit();
  }

  @Test
  void boundariesAreInclusive() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    when(storage.hasUnit()).thenReturn(false);
    when(area.getSelectedUnit()).thenReturn(new Entity());
    when(area.isCharacterSelected()).thenReturn(true);

    Entity tile = makeTileAt(10, 20, storage);
    TileInputComponent comp = tile.getComponent(TileInputComponent.class);

    // left edge x == 10
    assertTrue(comp.touchDown(10, 25, 0, Input.Buttons.LEFT));
    // right edge x == 10 + TILE
    assertTrue(comp.touchDown((int) (10 + TILE), 25, 0, Input.Buttons.LEFT));
    // bottom edge y == 20
    assertTrue(comp.touchDown(15, 20, 0, Input.Buttons.LEFT));
    // top edge y == 20 + TILE
    assertTrue(comp.touchDown(15, (int) (20 + TILE), 0, Input.Buttons.LEFT));

    verify(storage, atLeastOnce()).triggerSpawnUnit();
  }

  @Test
  void stageToWorldIsRespected() {
    TileStorageComponent storage = mock(TileStorageComponent.class);
    when(area.getSelectedUnit()).thenReturn(new Entity());

    Entity tile = makeTileAt(10, 20, storage);

    doReturn(new GridPoint2(0, 0)).when(area).stageToWorld(any());

    boolean handled =
        tile.getComponent(TileInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertFalse(handled);
    verify(storage, never()).triggerSpawnUnit();
    verify(storage, never()).removeTileUnit();
  }
}
