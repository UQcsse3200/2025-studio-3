package com.csse3200.game.tile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.LevelGameGrid;
import com.csse3200.game.areas.terrain.TerrainFactory;
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
class TileTest {
  @Mock Texture texture;
  LevelGameGrid grid;
  LevelGameArea levelGameArea;
  Entity selected;

  @BeforeEach
  void beforeEach() {
    // textures for tests
    Texture ghost1 = mock(Texture.class, "ghost_1");
    Texture ghost2 = mock(Texture.class, "ghost_2");
    Texture star = mock(Texture.class, "star");

    ServiceLocator.registerEntityService(new EntityService());

    // creates mock stage and render service
    com.badlogic.gdx.scenes.scene2d.Stage stage = mock(com.badlogic.gdx.scenes.scene2d.Stage.class);
    com.csse3200.game.rendering.RenderService renderService =
        mock(com.csse3200.game.rendering.RenderService.class);
    when(renderService.getStage()).thenReturn(stage);
    ServiceLocator.registerRenderService(renderService);

    // creates mock resource service
    com.csse3200.game.services.ResourceService resourceService =
        mock(com.csse3200.game.services.ResourceService.class);
    lenient()
        .when(resourceService.getAsset(eq("images/ghost_1.png"), eq(Texture.class)))
        .thenReturn(ghost1);
    lenient()
        .when(resourceService.getAsset(eq("images/ghost_king.png"), eq(Texture.class)))
        .thenReturn(ghost2);
    lenient()
        .when(resourceService.getAsset(eq("images/selected_star.png"), eq(Texture.class)))
        .thenReturn(star);
    ServiceLocator.registerResourceService(resourceService);
    TerrainFactory factory = mock(TerrainFactory.class);

    levelGameArea =
        new LevelGameArea(factory) {
          @Override
          public void create() {
            // default implementation ignored
          }
        };

    // creates entity for selected unit
    selected = new Entity().addComponent(new TextureRenderComponent("images/ghost_1.png"));
    levelGameArea.setSelectedUnit(selected);

    // creates a grid
    grid = new LevelGameGrid(1, 1);
    Entity tile = createValidTile();
    grid.addTile(0, tile);
    levelGameArea.setGrid(grid);
  }

  @Test
  void shouldAddUnit() {
    Entity tile = grid.getTile(0, 0);
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    tileStorageComponent.triggerSpawnUnit();
    assertTrue(tileStorageComponent.hasUnit());
    // because the selected unit is duplicated when placed on tile, so cant test that the both
    // entities are the same
    assertSame(
        selected.getComponent(TextureRenderComponent.class).getTexture(),
        tileStorageComponent.getTileUnit().getComponent(TextureRenderComponent.class).getTexture());
  }

  @Test
  void shouldntAddUnit() {
    Entity tile = grid.getTile(0, 0);
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    tileStorageComponent.triggerSpawnUnit();
    Entity newSelected =
        new Entity().addComponent(new TextureRenderComponent("images/ghost_king.png"));
    levelGameArea.setSelectedUnit(newSelected);
    tileStorageComponent.triggerSpawnUnit();
    // checks if the tile unit has not been replaced with new unit if there was already a unit
    // placed
    assertSame(
        selected.getComponent(TextureRenderComponent.class).getTexture(),
        tileStorageComponent.getTileUnit().getComponent(TextureRenderComponent.class).getTexture());
    assertNotSame(
        newSelected.getComponent(TextureRenderComponent.class).getTexture(),
        tileStorageComponent.getTileUnit().getComponent(TextureRenderComponent.class).getTexture());
  }

  @Test
  void shouldRemoveUnit() {
    Entity tile = grid.getTile(0, 0);
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    tileStorageComponent.triggerSpawnUnit();
    tileStorageComponent.removeTileUnit();
    assertFalse(tileStorageComponent.hasUnit());
  }

  @Test
  void shouldBeNullUnitByDefault() {
    Entity tile = grid.getTile(0, 0);
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    assertNull(tileStorageComponent.getTileUnit());
  }

  @Test
  void removeNullUnit() {
    Entity tile = grid.getTile(0, 0);
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
