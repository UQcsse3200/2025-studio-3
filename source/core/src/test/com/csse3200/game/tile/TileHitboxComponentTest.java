package com.csse3200.game.tile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.LevelGameGrid;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.tile.TileHitboxComponent;
import com.csse3200.game.components.tile.TileInputComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class TileHitboxComponentTest {
  @Mock Texture texture;
  LevelGameGrid grid;
  LevelGameArea levelGameArea;

  @BeforeEach
  void beforeEach() {
    ServiceLocator.registerEntityService(new EntityService());

    // creates mock stage and render service
    com.badlogic.gdx.scenes.scene2d.Stage stage = mock(com.badlogic.gdx.scenes.scene2d.Stage.class);
    com.csse3200.game.rendering.RenderService renderService =
        mock(com.csse3200.game.rendering.RenderService.class);
    lenient().when(renderService.getStage()).thenReturn(stage);
    ServiceLocator.registerRenderService(renderService);

    // creates mock resource service
    com.csse3200.game.services.ResourceService resourceService =
        mock(com.csse3200.game.services.ResourceService.class);
    lenient()
        .when(resourceService.getAsset(anyString(), eq(Texture.class)))
        .thenReturn(mock(Texture.class));
    ServiceLocator.registerResourceService(resourceService);

    // creates mock input service
    com.csse3200.game.input.InputService inputService =
        mock(com.csse3200.game.input.InputService.class);
    lenient().doNothing().when(inputService).register(any());
    lenient().doNothing().when(inputService).unregister(any());
    ServiceLocator.registerInputService(inputService);

    TerrainFactory factory = mock(TerrainFactory.class);

    levelGameArea =
        new LevelGameArea(factory, "levelOne") {
          @Override
          public void create() {
            // default implementation ignored
          }
        };

    // creates a grid
    grid = new LevelGameGrid(1, 1);
    Entity tile = createValidTile();
    grid.addTile(0, tile);
    levelGameArea.setGrid(grid);

    Entity selected =
        new Entity()
            .addComponent(new TextureRenderComponent(mock(Texture.class)))
            .addComponent(
                new DeckInputComponent(levelGameArea, Entity::new)); // or whatever ctor you use
    levelGameArea.setSelectedUnit(selected); // setter or reflection
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
  void shouldGetMinX() {
    Entity tile = createValidTile();
    float minX = tile.getComponent(TileHitboxComponent.class).getMinPosX();
    assertEquals(0, minX);
  }

  @Test
  void shouldGetMinY() {
    Entity tile = createValidTile();
    float minY = tile.getComponent(TileHitboxComponent.class).getMinPosY();
    assertEquals(0, minY);
  }

  @Test
  void shouldGetMaxX() {
    Entity tile = createValidTile();
    float maxX = tile.getComponent(TileHitboxComponent.class).getMaxPosX();
    assertEquals(5, maxX);
  }

  @Test
  void shouldGetMaxY() {
    Entity tile = createValidTile();
    float maxY = tile.getComponent(TileHitboxComponent.class).getMaxPosY();
    assertEquals(5, maxY);
  }

  @Test
  void shouldSetMinX() {
    Entity tile = createValidTile();
    tile.getComponent(TileHitboxComponent.class).setMinPosX(1);
    float minX = tile.getComponent(TileHitboxComponent.class).getMinPosX();
    assertEquals(1, minX);
  }

  @Test
  void shouldSetMinY() {
    Entity tile = createValidTile();
    tile.getComponent(TileHitboxComponent.class).setMinPosY(2);
    float minY = tile.getComponent(TileHitboxComponent.class).getMinPosY();
    assertEquals(2, minY);
  }

  @Test
  void shouldSetMaxX() {
    Entity tile = createValidTile();
    tile.getComponent(TileHitboxComponent.class).setMaxPosX(6);
    float maxX = tile.getComponent(TileHitboxComponent.class).getMaxPosX();
    assertEquals(6, maxX);
  }

  @Test
  void shouldSetMaxY() {
    Entity tile = createValidTile();
    tile.getComponent(TileHitboxComponent.class).setMaxPosY(7);
    float maxY = tile.getComponent(TileHitboxComponent.class).getMaxPosY();
    assertEquals(7, maxY);
  }

  @Test
  void shouldBeValidTile() {
    Entity tile = createValidTile();
    assertNotNull(tile);
    assertNotNull(tile.getComponent(TileStorageComponent.class));
    assertNotNull(tile.getComponent(TileHitboxComponent.class));
    assertNotNull(tile.getComponent(TileInputComponent.class));
  }

  private Entity createValidTile() {
    // same as GridFactory but needs to be separate otherwise error with TextureRenderComponent
    Entity tile =
        new Entity()
            .addComponent(new TextureRenderComponent(texture))
            .addComponent(new TileHitboxComponent(5, 5, 0, 0))
            .addComponent(new TileStorageComponent(levelGameArea))
            .addComponent(new TileInputComponent(levelGameArea));
    tile.getComponent(TileStorageComponent.class).setPosition(0);
    return tile;
  }
}
