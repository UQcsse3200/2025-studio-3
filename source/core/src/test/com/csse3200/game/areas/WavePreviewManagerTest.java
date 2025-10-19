package com.csse3200.game.areas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.entities.Entity;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;

class WavePreviewManagerTest {

  @Test
  void clearWavePreviewRemovesEntitiesAndResetsState() throws Exception {
    LevelGameArea area = mock(LevelGameArea.class);
    WavePreviewManager manager = new WavePreviewManager(area);

    Field previewField = WavePreviewManager.class.getDeclaredField("previewEntities");
    previewField.setAccessible(true);
    @SuppressWarnings("unchecked")
    List<Entity> previewList = (List<Entity>) previewField.get(manager);

    Entity first = new Entity();
    Entity second = new Entity();
    previewList.add(first);
    previewList.add(second);

    Field activeField = WavePreviewManager.class.getDeclaredField("active");
    activeField.setAccessible(true);
    activeField.setBoolean(manager, true);

    manager.clearWavePreview();

    verify(area).removePreviewEntity(first);
    verify(area).removePreviewEntity(second);
    assertTrue(previewList.isEmpty());
    assertFalse(activeField.getBoolean(manager));
  }

  @Test
  void clearWavePreviewNoOpWhenInactive() {
    LevelGameArea area = mock(LevelGameArea.class);
    WavePreviewManager manager = new WavePreviewManager(area);

    manager.clearWavePreview();

    verifyNoInteractions(area);
  }
}
