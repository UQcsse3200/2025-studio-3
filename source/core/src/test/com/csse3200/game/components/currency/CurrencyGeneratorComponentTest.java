package com.csse3200.game.components.currency;

import static com.csse3200.game.services.GameStateService.FreezeReason.USER_PAUSE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class CurrencyGeneratorComponentTest {
  private static final String SCRAP_TEXTURE = "images/entities/currency/scrap_metal.png";

  @Mock private Stage stage;
  @Mock private Stage replacementStage;
  @Mock private Group root;
  @Mock private Group replacementRoot;
  @Mock private Timer timer;

  private GameStateService gameStateService;

  @BeforeEach
  void setUp() {
    lenient().when(stage.getRoot()).thenReturn(root);
    lenient().when(replacementStage.getRoot()).thenReturn(replacementRoot);
    lenient().doNothing().when(root).addAction(any(Action.class));
    lenient().doNothing().when(root).removeAction(any(Action.class));

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

    verify(root, never()).addAction(any(Action.class));
    assertTrue(component.isPaused());

    gameStateService.removeFreezeReason(USER_PAUSE);

    verify(root, times(1)).addAction(any(Action.class));
    assertFalse(component.isPaused());
  }

  @Test
  void disposingRemovesActionAndListener() {
    CurrencyGeneratorComponent component = buildComponent();
    Entity spawner = new Entity().addComponent(component);
    spawner.create();

    ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
    verify(root).addAction(actionCaptor.capture());

    reset(root);
    doNothing().when(root).removeAction(any(Action.class));

    component.dispose();

    verify(root).removeAction(actionCaptor.getValue());

    reset(root);
    gameStateService.addFreezeReason(USER_PAUSE);
    verifyNoInteractions(root);
  }

  @Test
  void pausesAgainstOriginalStageWhenStageSwapped() {
    CurrencyGeneratorComponent component = buildComponent();
    Entity spawner = new Entity().addComponent(component);
    spawner.create();

    ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
    verify(root).addAction(actionCaptor.capture());

    reset(root, replacementRoot);
    doNothing().when(root).removeAction(any(Action.class));

    ServiceLocator.getRenderService().setStage(replacementStage);

    gameStateService.addFreezeReason(USER_PAUSE);

    verify(root).removeAction(actionCaptor.getValue());
    verifyNoInteractions(replacementRoot);
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
