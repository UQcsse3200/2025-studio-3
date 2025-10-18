package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.GameStateService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeckInputComponentTest {

  @Mock AreaAPI area;
  private static final float TILE = 32f;

  private Entity makeDeckEntity(float x, float y) {
    DeckInputComponent comp = new DeckInputComponent(area, Entity::new);
    Entity e = new Entity().addComponent(comp);
    e.setPosition(x, y);
    return e;
  }

  @BeforeEach
  void beforeEach() {
    lenient()
        .when(area.stageToWorld(any()))
        .thenAnswer(
            inv -> {
              GridPoint2 p = inv.getArgument(0);
              return new GridPoint2(p.x, p.y);
            });
    when(area.getTileSize()).thenReturn(TILE);
  }

  @AfterEach
  void afterEach() {
    ServiceLocator.deregisterGameStateService();
  }

  @Test
  void leftClickShouldSelectAndReturnTrue() {
    Entity inv = makeDeckEntity(10, 20);

    boolean handled =
        inv.getComponent(DeckInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertTrue(handled);
    verify(area).setSelectedUnit(inv);
    verify(area, never()).setSelectedUnit(isNull());
  }

  @Test
  void rightClickClearsSelectionAndReturnsTrue() {
    Entity inv = makeDeckEntity(10, 20);

    boolean handled =
        inv.getComponent(DeckInputComponent.class).touchDown(11, 21, 0, Input.Buttons.RIGHT);

    assertTrue(handled);
    verify(area).setSelectedUnit(null);
    verify(area, never()).setSelectedUnit(same(inv));
  }

  @Test
  void otherButtonReturnsFalseAndDoesNothing() {
    Entity inv = makeDeckEntity(10, 20);

    boolean handled =
        inv.getComponent(DeckInputComponent.class).touchDown(12, 22, 0, Input.Buttons.MIDDLE);

    assertFalse(handled);
    verify(area, never()).setSelectedUnit(any());
  }

  @Test
  void clickOutsideReturnsFalseAndDoesNothing() {
    Entity inv = makeDeckEntity(100, 200);

    boolean handled =
        inv.getComponent(DeckInputComponent.class).touchDown(10, 20, 0, Input.Buttons.LEFT);

    assertFalse(handled);
    verify(area, never()).setSelectedUnit(any());
  }

  @Test
  void boundariesAreInclusive() {
    Entity inv = makeDeckEntity(10, 20);

    DeckInputComponent comp = inv.getComponent(DeckInputComponent.class);
    // exactly on left edge (x == 10)
    assertTrue(comp.touchDown(10, 25, 0, Input.Buttons.LEFT));
    // exactly on right edge (x == 10 + TILE)
    assertTrue(comp.touchDown((int) (10 + TILE), 25, 0, Input.Buttons.LEFT));
    // exactly on bottom edge (y == 20)
    assertTrue(comp.touchDown(15, 20, 0, Input.Buttons.LEFT));
    // exactly on top edge (y == 20 + TILE)
    assertTrue(comp.touchDown(15, (int) (20 + TILE), 0, Input.Buttons.LEFT));

    // We called select multiple times; capture at least one call with the entity
    ArgumentCaptor<Entity> cap = ArgumentCaptor.forClass(Entity.class);
    verify(area, atLeastOnce()).setSelectedUnit(cap.capture());
    assertTrue(cap.getAllValues().stream().anyMatch(e -> e == inv));
  }

  @Test
  void stageToWorldIsRespected() {
    Entity inv = makeDeckEntity(10, 20);

    doReturn(new GridPoint2(0, 0)).when(area).stageToWorld(any());

    boolean handled =
        inv.getComponent(DeckInputComponent.class).touchDown(12, 22, 0, Input.Buttons.LEFT);

    assertFalse(handled);
    verify(area, never()).setSelectedUnit(any());
  }

  @Test
  void leftClickIgnoredWhenPlacementLocked() {
    GameStateService service = mock(GameStateService.class);
    lenient().when(service.isPlacementLocked()).thenReturn(true);
    ServiceLocator.registerGameStateService(service);

    Entity inv = makeDeckEntity(10, 20);

    boolean handled =
        inv.getComponent(DeckInputComponent.class).touchDown(15, 25, 0, Input.Buttons.LEFT);

    assertFalse(handled);
    verify(area, never()).setSelectedUnit(any());
    verify(service).isPlacementLocked();
  }
}
