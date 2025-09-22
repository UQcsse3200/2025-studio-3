package com.csse3200.game.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.components.dialog.DialogComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.DialogService.DialogType;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Test class for DialogComponent functionality. */
@ExtendWith(GameExtension.class)
class DialogComponentTest {

  @Mock private GL20 mockGL20;

  @Mock private Stage mockStage;

  private RenderService renderService;
  private ResourceService resourceService;

  private Entity testEntity;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Create real services
    renderService = new RenderService();
    resourceService = new ResourceService();

    // Mock the stage
    when(mockStage.getWidth()).thenReturn(800f);
    when(mockStage.getHeight()).thenReturn(600f);

    // Set the stage on the render service
    renderService.setStage(mockStage);

    // Register services with ServiceLocator
    ServiceLocator.registerRenderService(renderService);
    ServiceLocator.registerResourceService(resourceService);
    ServiceLocator.registerGlobalResourceService(resourceService);

    testEntity = new Entity();
  }

  @AfterEach
  void tearDown() {
    // Clean up ServiceLocator after each test
    ServiceLocator.clear();
  }

  /** Helper method to create a DialogComponent with proper setup */
  private DialogComponent createDialogComponent(DialogType type, String title, String message) {
    DialogComponent component = new DialogComponent(type, title, message);
    testEntity.addComponent(component);

    try {
      testEntity.create();
    } catch (Exception e) {
      // Do nothing
    }
    return component;
  }

  @Test
  void testDialogTypeEnum() {
    assertEquals(4, DialogType.values().length);
    assertNotNull(DialogType.INFO);
    assertNotNull(DialogType.WARNING);
    assertNotNull(DialogType.ERROR);
    assertNotNull(DialogType.SKILL);
  }

  @Test
  void testDialogComponentCreation() {
    DialogComponent infoDialog = new DialogComponent(DialogType.INFO, "Info", "Info message");
    DialogComponent warningDialog =
        new DialogComponent(DialogType.WARNING, "Warning", "Warning message");
    DialogComponent errorDialog = new DialogComponent(DialogType.ERROR, "Error", "Error message");

    assertNotNull(infoDialog);
    assertNotNull(warningDialog);
    assertNotNull(errorDialog);

    assertEquals(DialogType.INFO, infoDialog.getDialogType());
    assertEquals(DialogType.WARNING, warningDialog.getDialogType());
    assertEquals(DialogType.ERROR, errorDialog.getDialogType());
  }

  @Test
  void testDialogProperties() {
    String testTitle = "Test Title";
    String testMessage = "Test Message";

    DialogComponent dialog = new DialogComponent(DialogType.WARNING, testTitle, testMessage);

    assertEquals(testTitle, dialog.getTitle());
    assertEquals(testMessage, dialog.getMessage());
    assertEquals(DialogType.WARNING, dialog.getDialogType());
    assertFalse(dialog.isVisible());
  }

  @Test
  void testCallbackSetters() {
    DialogComponent dialog = new DialogComponent(DialogType.WARNING, "Test", "Test message");
    dialog.setOnConfirm(d -> {});
    dialog.setOnCancel(d -> {});
    dialog.setOnClose(d -> {});
    assertDoesNotThrow(
        () -> {
          dialog.setOnConfirm(d -> System.out.println("Confirm called"));
          dialog.setOnCancel(d -> System.out.println("Cancel called"));
          dialog.setOnClose(d -> System.out.println("Close called"));
        });
  }

  @Test
  void testDialogTypeSpecificBehavior() {
    DialogComponent infoDialog = new DialogComponent(DialogType.INFO, "Info", "Info message");
    DialogComponent warningDialog =
        new DialogComponent(DialogType.WARNING, "Warning", "Warning message");
    DialogComponent errorDialog = new DialogComponent(DialogType.ERROR, "Error", "Error message");
    assertEquals(DialogType.INFO, infoDialog.getDialogType());
    assertEquals(DialogType.WARNING, warningDialog.getDialogType());
    assertEquals(DialogType.ERROR, errorDialog.getDialogType());
  }

  @Test
  void testZIndex() {
    DialogComponent dialog = new DialogComponent(DialogType.INFO, "Test", "Test message");
    assertTrue(dialog.getZIndex() > 50f);
  }

  @Test
  void testDispose() {
    DialogComponent dialog = new DialogComponent(DialogType.INFO, "Test", "Test message");
    assertDoesNotThrow(dialog::dispose);
  }

  @Test
  void testShowAndHide() {
    DialogComponent dialog = createDialogComponent(DialogType.INFO, "Test", "Test message");
    assertFalse(dialog.isVisible());
    assertDoesNotThrow(dialog::show);
    assertDoesNotThrow(dialog::hide);
  }

  @Test
  void testDialogPositioning() {
    DialogComponent dialog = createDialogComponent(DialogType.INFO, "Test", "Test message");
    assertDoesNotThrow(() -> dialog.setPosition(100f, 200f));
  }

  @Test
  void testDialogSizing() {
    DialogComponent dialog = createDialogComponent(DialogType.INFO, "Test", "Test message");
    assertDoesNotThrow(() -> dialog.setSize(400f, 300f));
  }

  @Test
  void testResize() {
    DialogComponent dialog = createDialogComponent(DialogType.INFO, "Test", "Test message");
    assertDoesNotThrow(dialog::resize);
  }
}
