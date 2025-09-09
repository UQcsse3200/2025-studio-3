package com.csse3200.game.tile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.tile.TileHitboxComponent;
import com.csse3200.game.components.tile.TileInputComponent;
import com.csse3200.game.components.tile.TileStatusComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class TileTest {
  @Mock Texture texture;
  LevelGameArea levelGameArea;

  @BeforeEach
  void beforeEach() {
    RenderService renderService = new RenderService();
    Stage stage = new Stage(new ScreenViewport(), mock(SpriteBatch.class));
    renderService.setStage(stage);
    ServiceLocator.registerRenderService(renderService);
    ServiceLocator.registerEntityService(new EntityService());

    TerrainFactory factory = mock(TerrainFactory.class);

    levelGameArea =
        new LevelGameArea(factory) {
          @Override
          public void create() {
            // empty
          }
        };
  }

  @Test
  void shouldAddUnit() {
    Entity tile = createValidTile();
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    Entity unit = mock(Entity.class);
    tileStorageComponent.addTileUnit(unit);
    assertEquals(unit, tileStorageComponent.getTileUnit());
  }

  @Test
  void shouldntAddUnit() {
    Entity tile = createValidTile();
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    Entity firstUnit = mock(Entity.class);
    Entity secondUnit = mock(Entity.class);
    tileStorageComponent.addTileUnit(firstUnit);
    tileStorageComponent.addTileUnit(secondUnit);
    assertEquals(firstUnit, tileStorageComponent.getTileUnit());
    assertNotEquals(secondUnit, tileStorageComponent.getTileUnit());
  }

  @Test
  void shouldRemoveUnit() {
    Entity tile = createValidTile();
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    Entity unit = mock(Entity.class);
    tileStorageComponent.addTileUnit(unit);
    tileStorageComponent.removeTileUnit();
    assertNull(tileStorageComponent.getTileUnit());
  }

  @Test
  void shouldBeNullUnitByDefault() {
    Entity tile = createValidTile();
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    assertNull(tileStorageComponent.getTileUnit());
  }

  @Test
  void removeNullUnit() {
    Entity tile = createValidTile();
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    tileStorageComponent.removeTileUnit();
    assertNull(tileStorageComponent.getTileUnit());
  }

  @Test
  void shouldBeInHitboxOne() {
    Entity tile = createValidTile();
    TileHitboxComponent tileStorageComponent = tile.getComponent(TileHitboxComponent.class);
    GridPoint2 testPoint = new GridPoint2(1, 1);
    assertTrue(tileStorageComponent.inTileHitbox(testPoint));
  }

  @Test
  void shouldBeInHitboxTwo() {
    Entity tile = createValidTile();
    TileHitboxComponent tileStorageComponent = tile.getComponent(TileHitboxComponent.class);
    GridPoint2 testPoint = new GridPoint2(0, 0);
    assertTrue(tileStorageComponent.inTileHitbox(testPoint));
  }

  @Test
  void shouldntBeInHitboxOne() {
    Entity tile = createValidTile();
    TileHitboxComponent tileStorageComponent = tile.getComponent(TileHitboxComponent.class);
    GridPoint2 testPoint = new GridPoint2(8, 8);
    assertFalse(tileStorageComponent.inTileHitbox(testPoint));
  }

  @Test
  void shouldntBeInHitboxTwo() {
    Entity tile = createValidTile();
    TileHitboxComponent tileStorageComponent = tile.getComponent(TileHitboxComponent.class);
    GridPoint2 testPoint = new GridPoint2(0, 6);
    assertFalse(tileStorageComponent.inTileHitbox(testPoint));
  }

  @Test
  void createInvalidHitboxComponent() {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new TileHitboxComponent(3, 3, 5, 5);
            });
    assertEquals(
        "The max x and y values must be bigger than the min x and y values", ex.getMessage());
  }

  @Test
  void shouldBeValidTile() {
    Entity tile = createValidTile();
    assertNotNull(tile);
    assertNotNull(tile.getComponent(TileStorageComponent.class));
    assertNotNull(tile.getComponent(TileHitboxComponent.class));
    assertNotNull(tile.getComponent(TileStatusComponent.class));
    assertNotNull(tile.getComponent(TileInputComponent.class));
  }

  private Entity createValidTile() {
    // same as GridFactory but needs to be separate otherwise error with TextureRenderComponent
    return new Entity()
        .addComponent(new TextureRenderComponent(texture))
        .addComponent(new TileHitboxComponent(5, 5, 0, 0))
        .addComponent(new TileStorageComponent())
        .addComponent(new TileInputComponent())
        .addComponent(new TileStatusComponent(levelGameArea));
  }
}
