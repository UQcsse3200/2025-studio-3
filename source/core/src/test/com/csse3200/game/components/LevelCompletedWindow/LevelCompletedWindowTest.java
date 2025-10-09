package com.csse3200.game.components.LevelCompletedWindow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.components.levelcompleted.LevelCompletedWindow;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.UIExtension;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(UIExtension.class)
class LevelCompletedWindowTest {

  HeadlessApplication app;
  private static final String EVENT_NAME = "levelComplete";

  @BeforeEach
  void setup() {
    // Headless app
    app =
        new HeadlessApplication(
            new ApplicationAdapter() {}, new HeadlessApplicationConfiguration());

    // Mock OpenGL calls
    Gdx.gl20 = Mockito.mock(GL20.class);
    Gdx.gl = Gdx.gl20;
    Graphics graphics = Mockito.mock(Graphics.class);
    Mockito.when(graphics.getWidth()).thenReturn(1280);
    Mockito.when(graphics.getHeight()).thenReturn(720);
    Gdx.graphics = graphics;

    // Give Stage a mocked Batch to avoid shader compilation
    Batch mockBatch = Mockito.mock(Batch.class);
    Stage stage = new Stage(new ScreenViewport(), mockBatch);

    // Provide a RenderService whose getStage() returns our stage
    RenderService renderService =
        new RenderService() {
          @Override
          public Stage getStage() {
            return stage;
          }
        };
    ServiceLocator.registerRenderService(renderService);
  }

  @AfterEach
  void teardown() {
    ServiceLocator.clear();
  }

  @Test
  void windowBecomesVisibleAfterEvent() {
    Entity ui = new Entity();
    LevelCompletedWindow comp = new LevelCompletedWindow();
    ui.addComponent(comp);
    ui.create();

    Window w = comp.getWindow();
    assertNotNull(w, "Window should be created");
    assertFalse(w.isVisible(), "Window should start hidden");

    ui.getEvents().trigger(EVENT_NAME);

    assertTrue(w.isVisible(), "Window should be visible after event");
  }

  @Test
  void windowStaysHiddenWithoutEvent() {
    Entity ui = new Entity();
    LevelCompletedWindow comp = new LevelCompletedWindow();
    ui.addComponent(comp);
    ui.create();

    Window w = comp.getWindow();
    assertNotNull(w);
    assertFalse(w.isVisible(), "Window should remain hidden if no event fired");
  }
}
