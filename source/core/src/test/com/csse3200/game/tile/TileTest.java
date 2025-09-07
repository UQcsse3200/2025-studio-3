package com.csse3200.game.tile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.tile.TileHitboxComponent;
import com.csse3200.game.components.tile.TileInputComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
public class TileTest {
    @Mock Texture texture;
    LevelGameArea levelGameArea;

    @BeforeEach
    void beforeEach() {
        TerrainFactory factory = mock(TerrainFactory.class);

        levelGameArea =
                new LevelGameArea(factory) {
                    @Override
                    public void create() {}
                };

        ServiceLocator.registerEntityService(new EntityService());
    }

    @Test
    public void shouldAddUnit() {
        Entity tile = createValidTile();
        TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
        tileStorageComponent.addTileUnit();
        assert(tileStorageComponent.hasUnit());
    }

//    @Test
//    public void shouldntAddUnit() {
//        Entity tile = createValidTile();
//        TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
//        Entity firstUnit = mock(Entity.class);
//        Entity secondUnit = mock(Entity.class);
//        tileStorageComponent.addTileUnit(firstUnit);
//        tileStorageComponent.addTileUnit(secondUnit);
//        assert(tileStorageComponent.getTileUnit() == firstUnit);
//        assert(tileStorageComponent.getTileUnit() != secondUnit);
//    }

    @Test
    public void shouldRemoveUnit() {
        Entity tile = createValidTile();
        TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
        tileStorageComponent.addTileUnit();
        tileStorageComponent.removeTileUnit();
        assert(!tileStorageComponent.hasUnit());
    }

//    @Test
//    public void shouldBeNullUnitByDefault() {
//        Entity tile = createValidTile();
//        TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
//        assert(tileStorageComponent.getTileUnit() == null);
//    }

//    @Test
//    public void removeNullUnit() {
//        Entity tile = createValidTile();
//        TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
//        tileStorageComponent.removeTileUnit();
//        assert(tileStorageComponent.getTileUnit() == null);
//    }

    @Test
    public void shouldBeInHitboxOne() {
        Entity tile = createValidTile();
        TileHitboxComponent tileStorageComponent = tile.getComponent(TileHitboxComponent.class);
        GridPoint2 testPoint = new GridPoint2(1, 1);
        assert(tileStorageComponent.inTileHitbox(testPoint));
    }

    @Test
    public void shouldBeInHitboxTwo() {
        Entity tile = createValidTile();
        TileHitboxComponent tileStorageComponent = tile.getComponent(TileHitboxComponent.class);
        GridPoint2 testPoint = new GridPoint2(0, 0);
        assert(tileStorageComponent.inTileHitbox(testPoint));
    }

    @Test
    public void shouldntBeInHitboxOne() {
        Entity tile = createValidTile();
        TileHitboxComponent tileStorageComponent = tile.getComponent(TileHitboxComponent.class);
        GridPoint2 testPoint = new GridPoint2(8, 8);
        assert(!tileStorageComponent.inTileHitbox(testPoint));
    }

    @Test
    public void shouldntBeInHitboxTwo() {
        Entity tile = createValidTile();
        TileHitboxComponent tileStorageComponent = tile.getComponent(TileHitboxComponent.class);
        GridPoint2 testPoint = new GridPoint2(0, 6);
        assert(!tileStorageComponent.inTileHitbox(testPoint));
    }

    @Test
    public void createInvalidHitboxComponent() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new TileHitboxComponent(3, 3, 5, 5);
        });
        assertEquals("The max x and y values must be bigger than the min x and y values", ex.getMessage());
    }

    @Test
    public void shouldBeValidTile() {
        Entity tile = createValidTile();
        assertNotNull(tile);
        assert(tile.getComponent(TileStorageComponent.class) != null);
        assert(tile.getComponent(TileHitboxComponent.class) != null);
        assert(tile.getComponent(TileInputComponent.class) != null);
    }

    private Entity createValidTile() {
        //same as GridFactory but needs to be separate otherwise error with TextureRenderComponent
        return new Entity()
                .addComponent(new TextureRenderComponent(texture))
                .addComponent(new TileHitboxComponent(5, 5, 0, 0))
                .addComponent(new TileStorageComponent(levelGameArea))
                .addComponent(new TileInputComponent());
    }
}
