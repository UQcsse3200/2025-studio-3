package com.csse3200.game.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.extensions.UIExtension;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UIExtension.class)
@ExtendWith(GameExtension.class)
class DragOverlayTest {
  private DragOverlay overlay;

  @BeforeEach
  void beforeEach() {
    AreaAPI area = mock(AreaAPI.class);
    doAnswer(invocation -> 64f).when(area).getTileSize();
    Gdx.input = mock(Input.class);
    overlay = new DragOverlay(area);
    overlay.create();
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

    overlay.update();

    Image img = getImage();
    // Compute expected using the Stage's screen-to-stage conversion
    Stage stage = ServiceLocator.getRenderService().getStage();
    Vector2 p = new Vector2(100, 50);
    stage.screenToStageCoordinates(p);
    assertEquals(p.x - img.getWidth() / 2f, img.getX(), 0.1f);
    assertEquals(p.y - img.getHeight() / 2f, img.getY(), 0.1f);
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
