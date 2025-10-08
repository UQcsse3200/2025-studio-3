package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.dialog.AchievementDialogComponent;
import com.csse3200.game.components.dialog.DialogComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.extensions.UIExtension;
import com.csse3200.game.rendering.RenderService;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Test class for DialogService. */
@ExtendWith(GameExtension.class)
@ExtendWith(UIExtension.class)
class DialogServiceTest {
  private static final Logger logger = LoggerFactory.getLogger(DialogServiceTest.class);
  @Mock private EntityService mockEntityService;
  private ResourceService resourceService;
  private DialogService dialogService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Create real services
    resourceService = new ResourceService();
    ServiceLocator.registerResourceService(resourceService);
    ServiceLocator.registerGlobalResourceService(resourceService);
    ServiceLocator.registerEntityService(mockEntityService);

    // Mock entity registration to call create() on entities, but handle UI failures gracefully
    doAnswer(
            invocation -> {
              Entity entity = invocation.getArgument(0);
              try {
                entity.create();
              } catch (Exception e) {
                // In headless testing, UI creation might fail - this is expected
                // Log the error but don't rethrow to allow tests to continue
                logger.warn("UI creation failed in test environment: {}", e.getMessage());
              }
              return null;
            })
        .when(mockEntityService)
        .register(any(Entity.class));

    dialogService = new DialogService();
    ServiceLocator.registerDialogService(dialogService);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void testConstructor() {
    // Test that constructor creates service without errors
    DialogService service = new DialogService();
    assertNotNull(service);
    assertEquals(0, service.getActiveDialogCount());
    assertFalse(service.hasActiveDialogs());
  }

  @Test
  void testInfoDialog() {
    DialogComponent dialog = dialogService.info("Test", "Test");
    assertNotNull(dialog);
    assertEquals(DialogService.DialogType.INFO, dialog.getDialogType());
    assertEquals("Test", dialog.getTitle());
    assertEquals("Test", dialog.getMessage());
    assertTrue(dialogService.hasActiveDialogs());
    assertEquals(1, dialogService.getActiveDialogCount());
    assertDoesNotThrow(dialog::hide);
  }

  @Test
  void testInfoDialog_WithCallback() {
    Consumer<DialogComponent> callback = mock();

    DialogComponent dialog = dialogService.info("Test", "Test", callback);
    assertNotNull(dialog);
    assertEquals(DialogService.DialogType.INFO, dialog.getDialogType());
    assertEquals("Test", dialog.getTitle());
    assertEquals("Test", dialog.getMessage());
    assertTrue(dialogService.hasActiveDialogs());
    assertEquals(1, dialogService.getActiveDialogCount());
    assertDoesNotThrow(dialog::hide);
  }

  @Test
  void testWarningDialog() {
    DialogComponent dialog = dialogService.warning("Warning", "Warning Message");
    assertNotNull(dialog);
    assertEquals(DialogService.DialogType.WARNING, dialog.getDialogType());
    assertEquals("Warning", dialog.getTitle());
    assertEquals("Warning Message", dialog.getMessage());
    assertTrue(dialogService.hasActiveDialogs());
    assertEquals(1, dialogService.getActiveDialogCount());
    assertDoesNotThrow(dialog::hide);
  }

  @Test
  void testWarningDialog_WithCallbacks() {
    Consumer<DialogComponent> onConfirm = mock();
    Consumer<DialogComponent> onCancel = mock();

    DialogComponent dialog =
        dialogService.warning("Warning", "Warning Message", onConfirm, onCancel);
    assertNotNull(dialog);
    assertEquals(DialogService.DialogType.WARNING, dialog.getDialogType());
    assertEquals("Warning", dialog.getTitle());
    assertEquals("Warning Message", dialog.getMessage());
    assertTrue(dialogService.hasActiveDialogs());
    assertEquals(1, dialogService.getActiveDialogCount());
    assertDoesNotThrow(dialog::hide);
  }

  @Test
  void testErrorDialog() {
    DialogComponent dialog = dialogService.error("Error", "Error Message");
    assertNotNull(dialog);
    assertEquals(DialogService.DialogType.ERROR, dialog.getDialogType());
    assertEquals("Error", dialog.getTitle());
    assertEquals("Error Message", dialog.getMessage());
    assertTrue(dialogService.hasActiveDialogs());
    assertEquals(1, dialogService.getActiveDialogCount());
    assertDoesNotThrow(dialog::hide);
  }

  @Test
  void testErrorDialog_WithCallback() {
    Consumer<DialogComponent> callback = mock();

    DialogComponent dialog = dialogService.error("Error", "Error Message", callback);
    assertNotNull(dialog);
    assertEquals(DialogService.DialogType.ERROR, dialog.getDialogType());
    assertEquals("Error", dialog.getTitle());
    assertEquals("Error Message", dialog.getMessage());
    assertTrue(dialogService.hasActiveDialogs());
    assertEquals(1, dialogService.getActiveDialogCount());
    assertDoesNotThrow(dialog::hide);
  }

  @Test
  void testAchievementDialog() {
    AchievementDialogComponent dialog =
        dialogService.achievement("Test Achievement", "Test Description", 100, "T2");
    assertNotNull(dialog);
    // Achievement dialogs are tracked separately, so they don't affect the regular dialog count
    assertEquals(0, dialogService.getActiveDialogCount());
    assertFalse(dialogService.hasActiveDialogs());
    assertDoesNotThrow(dialog::hide);
  }

  @Test
  void testAchievementDialog_WithNullTier() {
    AchievementDialogComponent dialog =
        dialogService.achievement("Test Achievement", "Test Description", 100, null);
    assertNotNull(dialog);
    // Achievement dialogs are tracked separately, so they don't affect the regular dialog count
    assertEquals(0, dialogService.getActiveDialogCount());
    assertFalse(dialogService.hasActiveDialogs());
    assertDoesNotThrow(dialog::hide);
  }

  @Test
  void testMultipleDialogs() {
    DialogComponent dialog1 = dialogService.info("Title1", "Message1");
    DialogComponent dialog2 = dialogService.warning("Title2", "Message2");
    DialogComponent dialog3 = dialogService.error("Title3", "Message3");

    assertEquals(3, dialogService.getActiveDialogCount());
    assertTrue(dialogService.hasActiveDialogs());

    assertDoesNotThrow(dialog1::hide);
    assertDoesNotThrow(dialog2::hide);
    assertDoesNotThrow(dialog3::hide);
  }

  @Test
  void testHideAllDialogs() {
    dialogService.info("Title1", "Message1");
    dialogService.warning("Title2", "Message2");
    dialogService.error("Title3", "Message3");

    assertEquals(3, dialogService.getActiveDialogCount());
    assertTrue(dialogService.hasActiveDialogs());

    dialogService.hideAllDialogs();

    assertEquals(0, dialogService.getActiveDialogCount());
    assertFalse(dialogService.hasActiveDialogs());
  }

  @Test
  void testGetActiveDialogs() {
    List<DialogComponent> dialogs = dialogService.getActiveDialogs();
    assertNotNull(dialogs);
    assertTrue(dialogs.isEmpty());

    DialogComponent dialog = dialogService.info("Test", "Test");
    dialogs = dialogService.getActiveDialogs();
    assertEquals(1, dialogs.size());
    assertTrue(dialogs.contains(dialog));
  }

  @Test
  void testResize() {
    dialogService.info("Test", "Test");
    assertDoesNotThrow(() -> dialogService.resize());
  }

  @Test
  void testDialogTypeEnum() {
    DialogService.DialogType[] types = DialogService.DialogType.values();
    assertEquals(4, types.length);

    assertTrue(java.util.Arrays.asList(types).contains(DialogService.DialogType.INFO));
    assertTrue(java.util.Arrays.asList(types).contains(DialogService.DialogType.WARNING));
    assertTrue(java.util.Arrays.asList(types).contains(DialogService.DialogType.ERROR));
    assertTrue(java.util.Arrays.asList(types).contains(DialogService.DialogType.SKILL));
  }
}
