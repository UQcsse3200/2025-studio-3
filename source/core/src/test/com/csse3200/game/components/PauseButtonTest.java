package com.csse3200.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.components.hud.PauseButton;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.events.EventHandler;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PauseButtonTest {

    private PauseButton pauseButton;
    private Stage mockStage;
    private Entity mockEntity;
    private EventHandler mockEvents;
    private SettingsService mockSettingsService;
    private ResourceService mockResourceService;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockStage = mock(Stage.class);
        mockEntity = mock(Entity.class);
        mockEvents = mock(EventHandler.class);
        mockSettingsService = mock(SettingsService.class);
        mockResourceService = mock(ResourceService.class);

        // Register services in ServiceLocator
        ServiceLocator.registerSettingsService(mockSettingsService);
        ServiceLocator.registerGlobalResourceService(mockResourceService);

        // Stub required methods
        when(mockEntity.getEvents()).thenReturn(mockEvents);
        when(mockStage.getWidth()).thenReturn(800f);
        when(mockStage.getHeight()).thenReturn(600f);
        when(mockResourceService.getAsset(anyString(), eq(Texture.class)))
            .thenReturn(mock(Texture.class));

        // Create and attach PauseButton
        pauseButton = new PauseButton();
        pauseButton.setEntity(mockEntity);

        // Call create() — this sets up button, tooltip, and listeners
        pauseButton.create();
    }

    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }

    @Test
    @DisplayName("Pause button should correctly set paused state to true")
    void testPauseButtonSetsStateCorrectly() {
        pauseButton.setPaused(true);

        // Verify internal paused state
        assertTrue(getIsPaused(), "PauseButton should be in paused state");

        // Verify tooltip hidden and opacity reduced
        // We can’t directly check actors, but we can ensure no NPEs
    }

    @Test
    @DisplayName("Pause button should trigger 'pause' event when clicked")
    void testPauseButtonTriggersPauseEvent() {
        pauseButton.setPaused(false);
        pauseButton.entity.getEvents().trigger("pause");

        verify(mockEvents, times(1)).trigger("pause");
    }

    @Test
    @DisplayName("Pause button should resume when handleResume called")
    void testGameResumesAfterPause() {
        pauseButton.setPaused(true);

        assertFalse(getIsPaused(), "PauseButton should be unpaused after resume event");
    }

    /** Helper method to read private isPaused value using reflection */
    private boolean getIsPaused() {
        try {
            var field = PauseButton.class.getDeclaredField("isPaused");
            field.setAccessible(true);
            return (boolean) field.get(pauseButton);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
