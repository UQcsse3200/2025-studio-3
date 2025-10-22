package com.csse3200.game.components.currency;

import static com.csse3200.game.services.GameStateService.FreezeReason.USER_PAUSE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.components.GeneratorStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameStateService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class CurrencyGeneratorComponentTest {
  private static final String SCRAP_TEXTURE = "images/entities/currency/scrap_metal.png";

  @Mock private Stage stage;
  @Mock private Stage replacementStage;
  @Mock private Timer timer;

  private GameStateService gameStateService;

  @BeforeEach
  void setUp() {
    RenderService renderService = new RenderService();
    renderService.setStage(stage);
    ServiceLocator.registerRenderService(renderService);

    gameStateService = spy(new GameStateService(new GameTime(), timer));
    ServiceLocator.registerGameStateService(gameStateService);
  }

  @Test
  void resumesWhenGameUnfrozen() {
    gameStateService.addFreezeReason(USER_PAUSE);

    CurrencyGeneratorComponent component = buildComponent();
    Entity spawner = new Entity().addComponent(component);
    spawner.create();

    // Verify freeze listener was registered
    verify(gameStateService).registerFreezeListener(any());
    assertTrue(component.isPaused());

    gameStateService.removeFreezeReason(USER_PAUSE);

    // Verify component is no longer paused after unfreeze
    assertFalse(component.isPaused());
  }

  @Test
  void disposingRemovesActionAndListener() {
    CurrencyGeneratorComponent component = buildComponent();
    Entity spawner = new Entity().addComponent(component);
    spawner.create();

    // Verify freeze listener was registered
    verify(gameStateService).registerFreezeListener(any());

    component.dispose();

    // Verify freeze listener was unregistered
    verify(gameStateService).unregisterFreezeListener(any());

    // Verify component is paused after dispose
    assertTrue(component.isPaused());
  }

  @Test
  void pausesAgainstOriginalStageWhenStageSwapped() {
    CurrencyGeneratorComponent component = buildComponent();
    Entity spawner = new Entity().addComponent(component);
    spawner.create();

    // Verify freeze listener was registered
    verify(gameStateService).registerFreezeListener(any());

    // Swap the stage
    ServiceLocator.getRenderService().setStage(replacementStage);

    // Add freeze reason - component should pause regardless of stage swap
    gameStateService.addFreezeReason(USER_PAUSE);

    // Verify component is paused
    assertTrue(component.isPaused());
  }

  @Test
  void createSkipsWhenStageNotReady() {
    ServiceLocator.getRenderService().setStage(null);

    CurrencyGeneratorComponent component = buildComponent();
    Entity spawner = new Entity().addComponent(component);
    spawner.create();

    verify(gameStateService, never()).registerFreezeListener(any());
  }

  private static CurrencyGeneratorComponent buildComponent() {
    Entity furnace = new Entity();
    furnace.addComponent(new GeneratorStatsComponent(100, 5, 10, 50));
    return new CurrencyGeneratorComponent(furnace, new GridPoint2(0, 0), SCRAP_TEXTURE);
  }
}
