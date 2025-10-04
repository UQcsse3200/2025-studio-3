package com.csse3200.game.extensions;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import com.csse3200.game.services.SettingsService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.rendering.RenderService;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * A JUnit extension which provides UIComponent static initialization.
 */
public class UIExtension implements BeforeEachCallback {
  @Override
  public void beforeEach(ExtensionContext context) {
    SettingsService mockSettingsService = mock(SettingsService.class, RETURNS_DEEP_STUBS);
    when(mockSettingsService.getSettings().getCurrentUIScale()).thenReturn(Settings.UIScale.MEDIUM);    
    ServiceLocator.registerSettingsService(mockSettingsService);

    Stage stage = mock(Stage.class, RETURNS_DEEP_STUBS);
    RenderService renderService = mock(RenderService.class, RETURNS_DEEP_STUBS);
    ServiceLocator.registerRenderService(renderService);
    when(renderService.getStage()).thenReturn(stage);
    when(stage.getHeight()).thenReturn(720f);
    when(stage.getWidth()).thenReturn(1280f);
  }
}
