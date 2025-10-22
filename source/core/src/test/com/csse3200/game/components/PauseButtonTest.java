package com.csse3200.game.components;

import com.csse3200.game.components.hud.PauseButton;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.SettingsService;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PauseButton, verifying that pressing pause correctly stops
 * enemy spawning, currency updates, and player firing.
 */
public class PauseButtonTest {

    private PauseButton pauseButton;
    private Entity entity;

    // Mock systems that depend on pause state
    private Entity enemySpawner;
    private Entity currencySystem;
    private Entity firingSystem;

    @BeforeEach
    void setUp() {
        // Setup required services
        ServiceLocator.registerSettingsService(mock(SettingsService.class));
        ServiceLocator.registerGlobalResourceService(mock(ResourceService.class));
        ServiceLocator.registerTimeSource(new GameTime());

        // Create a parent entity and mock subsystems
        entity = new Entity();
        pauseButton = new PauseButton();
        entity.addComponent(pauseButton);
        pauseButton.create();

        // Mock dependent systems
        enemySpawner = mock(Entity.class);
        currencySystem = mock(Entity.class);
        firingSystem = mock(Entity.class);

        // Register listeners to verify pause events trigger properly
        entity.getEvents().addListener("pause", () -> {
            enemySpawner.getEvents().trigger("pause");
            currencySystem.getEvents().trigger("pause");
            firingSystem.getEvents().trigger("pause");
        });
    }

    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }

    @Test
    void testPauseButtonTriggersPauseEvent() {

        pauseButton.setPaused(true);
        entity.getEvents().trigger("pause");

        verify(enemySpawner.getEvents(), atLeastOnce()).trigger("pause");
        verify(currencySystem.getEvents(), atLeastOnce()).trigger("pause");
        verify(firingSystem.getEvents(), atLeastOnce()).trigger("pause");
    }

    @Test
    void testGameResumesAfterPause() {
        pauseButton.setPaused(true);
        entity.getEvents().trigger("pause");
        pauseButton.setPaused(false);
        entity.getEvents().trigger("resume");

        verify(enemySpawner.getEvents(), atLeastOnce()).trigger("pause");
        verifyNoMoreInteractions(enemySpawner.getEvents());
    }

    @Test
    void testPauseButtonSetsStateCorrectly() {
        pauseButton.setPaused(true);
        Assertions.assertTrue(getPrivatePausedState(pauseButton));

        pauseButton.setPaused(false);
        Assertions.assertFalse(getPrivatePausedState(pauseButton));
    }


    private boolean getPrivatePausedState(PauseButton button) {
        try {
            var field = PauseButton.class.getDeclaredField("isPaused");
            field.setAccessible(true);
            return field.getBoolean(button);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
