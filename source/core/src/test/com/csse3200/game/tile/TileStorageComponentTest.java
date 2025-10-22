package com.csse3200.game.tile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.LevelGameGrid;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.components.tile.TileHitboxComponent;
import com.csse3200.game.components.tile.TileInputComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.extensions.UIExtension;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.CurrencyService;
import com.csse3200.game.services.DiscordRichPresenceService;
import com.csse3200.game.services.ItemEffectsService;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(GameExtension.class)
@ExtendWith(UIExtension.class)
@ExtendWith(MockitoExtension.class)
class TileStorageComponentTest {
  @Mock Texture texture;
  LevelGameGrid grid;
  LevelGameArea levelGameArea;
  @Mock DiscordRichPresenceService discordRichPresenceService;

  @BeforeEach
  void beforeEach() {
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerDiscordRichPresenceService(discordRichPresenceService);

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

    ConfigService configService = mock(ConfigService.class);
    ServiceLocator.registerConfigService(configService);

    // creates mock profile service with skillset chain for DefenderStatsComponent static init
    ProfileService profileService = new ProfileService();
    profileService.createProfile("TestProfile", 1);
    ServiceLocator.registerProfileService(profileService);

    // creates mock discord rich presence service
    DiscordRichPresenceService discordService = mock(DiscordRichPresenceService.class);
    ServiceLocator.registerDiscordRichPresenceService(discordService);

    // creates currency/item/wave services used by spawnUnit
    CurrencyService currencyService = new CurrencyService(1000, 1000);
    ServiceLocator.registerCurrencyService(currencyService);
    ItemEffectsService itemEffectsService = mock(ItemEffectsService.class);
    ServiceLocator.registerItemEffectsService(itemEffectsService);
    WaveService waveService = mock(WaveService.class);
    ServiceLocator.registerWaveService(waveService);

    levelGameArea =
        new LevelGameArea("levelOne") {
          @Override
          public void create() {
            // default implementation ignored
          }
        };

    lenient()
        .doNothing()
        .when(discordRichPresenceService)
        .updateGamePresence(anyString(), anyInt());

    // creates a grid
    grid = new LevelGameGrid(1, 1);
    Entity tile = createValidTile();
    grid.addTile(0, tile);
    levelGameArea.setGrid(grid);

    Entity selected =
        new Entity()
            .addComponent(new TextureRenderComponent(mock(Texture.class)))
            .addComponent(
                new DeckInputComponent(
                    levelGameArea,
                    () -> {
                      Entity entity = new Entity();
                      entity.addComponent(new DefenderStatsComponent(100, 50, 500, 1f, 0.1f, 50));
                      return entity;
                    }));
    levelGameArea.setSelectedUnit(selected);
  }

  @Test
  void shouldntAddUnit() {
    Entity tile = grid.getTile(0, 0);
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    levelGameArea.spawnUnit(tileStorageComponent.getPosition());

    int beforeSecondTriggerId = tileStorageComponent.getTileUnit().getId();
    levelGameArea.setSelectedUnit(
        new Entity()
            .addComponent(new TextureRenderComponent(mock(Texture.class)))
            .addComponent(
                new DeckInputComponent(
                    levelGameArea,
                    () -> {
                      Entity entity = new Entity();
                      entity.addComponent(new DefenderStatsComponent(100, 50, 500, 1f, 0.1f, 50));
                      return entity;
                    })));
    levelGameArea.spawnUnit(tileStorageComponent.getPosition());
    int afterSecondTriggerId = tileStorageComponent.getTileUnit().getId();

    // checks if the tile unit has not been replaced with new unit if there was already a unit
    // placed
    assertEquals(beforeSecondTriggerId, afterSecondTriggerId);
  }

  @Test
  void shouldAddUnit() {
    Entity tile = grid.getTile(0, 0);
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    levelGameArea.spawnUnit(tileStorageComponent.getPosition());
    assertTrue(tileStorageComponent.hasUnit());
  }

  @Test
  void shouldRemoveUnit() {
    Entity tile = grid.getTile(0, 0);
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    levelGameArea.spawnUnit(tileStorageComponent.getPosition());
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
  void shouldGetArea() {
    Entity tile = grid.getTile(0, 0);
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    AreaAPI area = tileStorageComponent.getArea();
    assertEquals(area, levelGameArea);
  }

  @Test
  void shouldGetPosition() {
    Entity tile = grid.getTile(0, 0);
    TileStorageComponent tileStorageComponent = tile.getComponent(TileStorageComponent.class);
    int pos = tileStorageComponent.getPosition();
    assertEquals(0, pos);
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
