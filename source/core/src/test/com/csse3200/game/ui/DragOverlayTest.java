package com.csse3200.game.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

class DragOverlayTest {

  private DragOverlay overlay;
  private Stage stage;
  private MockedStatic<Gdx> gdxStatic;

  @BeforeEach
  void beforeEach() {
    AreaAPI area = mock(AreaAPI.class);
    when(area.getTileSize()).thenReturn(64f);

    stage = mock(Stage.class, RETURNS_DEEP_STUBS);
    RenderService renderService = mock(RenderService.class, RETURNS_DEEP_STUBS);
    ServiceLocator.registerRenderService(renderService);
    when(renderService.getStage()).thenReturn(stage);

    overlay = new DragOverlay(area);
    overlay.create();

    gdxStatic = mockStatic(Gdx.class, CALLS_REAL_METHODS);
    Gdx.input = mock(com.badlogic.gdx.Input.class);
  }

  @AfterEach
  void afterEach() {
    gdxStatic.close();
    ServiceLocator.clear();
  }

  @Test
  void begin_withNullTexture_doesNothing() {
    overlay.begin(null);
    assertFalse(getImage().isVisible());
  }

  @Test
  void begin_withTexture_activatesImage() {
    Texture texture = mock(Texture.class);
    overlay.begin(texture);
    assertTrue(getImage().isVisible());
  }

  @Test
  void cancel_hidesImage() {
    Texture texture = mock(Texture.class);
    overlay.begin(texture);
    overlay.cancel();
    assertFalse(getImage().isVisible());
  }

  @Test
  void setImageScale_updatesSizeWhenActive() {
    Texture texture = mock(Texture.class);
    overlay.begin(texture);
    float before = getImage().getWidth();

    overlay.setImageScale(2.0f);

    assertNotEquals(before, getImage().getWidth());
  }

  @Test
  void setImageScale_onlyUpdatesScaleWhenInactive() {
    overlay.setImageScale(1.5f);
    assertEquals(1.5f, overlay.sizeScale);
    assertFalse(getImage().isVisible());
  }

  @Test
  void update_movesImageWhenActive() {
    Texture texture = mock(Texture.class);
    overlay.begin(texture);

    when(Gdx.input.getX()).thenReturn(100);
    when(Gdx.input.getY()).thenReturn(50);
    when(stage.getHeight()).thenReturn(200f);

    overlay.update();

    Image img = getImage();
    assertEquals(100 - img.getWidth() / 2f, img.getX(), 0.1f);
    assertEquals(200 - 50 - img.getHeight() / 2f, img.getY(), 0.1f);
  }

  @Test
  void dispose_removesImageFromStage() {
    Image img = getImage();
    overlay.dispose();
    assertFalse(img.hasParent());
  }

  private Image getImage() {
    try {
      var f = DragOverlay.class.getDeclaredField("image");
      f.setAccessible(true);
      return (Image) f.get(overlay);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
