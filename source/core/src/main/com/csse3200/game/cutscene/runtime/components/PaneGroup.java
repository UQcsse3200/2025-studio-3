package com.csse3200.game.cutscene.runtime.components;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaneGroup extends WidgetGroup {
  private static final Logger logger = LoggerFactory.getLogger(PaneGroup.class);
  private Map<Image, CharacterImageData> images = new HashMap<>();
  private static final float Z_OVERLAP_OFFSET = 0.3f;

  private void applyZOrder() {
    List<Map.Entry<Image, CharacterImageData>> entries = new ArrayList<>(images.entrySet());

    // sort the entries by z index
    entries.sort((a, b) -> Integer.compare(a.getValue().getzIndex(), b.getValue().getzIndex()));

    // Remap entry z indexes
    for (int i = 0; i < entries.size(); i++) {
      Image image = entries.get(i).getKey();

      image.setZIndex(i);
    }
  }

  public void relayout() {
    float paneWidth = getWidth();
    float paneHeight = getHeight();

    for (Map.Entry<Image, CharacterImageData> entry : images.entrySet()) {
      Image image = entry.getKey();
      CharacterImageData characterImageData = entry.getValue();

      Drawable drawable = image.getDrawable();
      if (drawable == null) continue;

      float imageWidth = drawable.getMinWidth();
      float imageHeight = drawable.getMinHeight();
      float width = paneWidth;
      float height = width * (imageHeight / imageWidth);

      if (height > paneHeight) {
        width = paneHeight * (imageWidth / imageHeight);
        height = paneHeight;
      }

      image.setSize(width, height);
      image.setPosition((paneWidth - width) * 0.5f, 0f);

      float offsetX = characterImageData.getxOffset();
      int z = images.size() - characterImageData.getzIndex();
      if (offsetX > -1 && offsetX < 1) {
        if (offsetX <= 0) {
          offsetX = (1 + z * Z_OVERLAP_OFFSET) * offsetX + z * Z_OVERLAP_OFFSET;
        } else {
          offsetX = (1 - z * Z_OVERLAP_OFFSET) * offsetX - z * Z_OVERLAP_OFFSET;
        }
      }
      float offsetY = characterImageData.getyOffset();
      image.moveBy(offsetX * image.getWidth(), offsetY * image.getHeight());
      image.setRotation(characterImageData.getRotation());
      image.setScale(characterImageData.getScale());
    }
  }

  public void addImage(Image image) {
    images.put(image, new CharacterImageData(0f, 0f, 0f, 1f, getChildren().size));
    addActor(image);
    applyZOrder();
  }

  public CharacterImageData getImage(Image image) {
    CharacterImageData imageData = images.get(image);
    if (imageData == null) {
      addImage(image);
      logger.info("Added character image {}", image.hashCode());
    }
    return images.get(image);
  }

  public List<CharacterImageData> getImages() {
    return new ArrayList<>(images.values());
  }

  public void removeImage(Image image) {
    removeActor(image);
    images.remove(image);
    applyZOrder();
  }

  public List<Image> getImagesKeys() {
    return new ArrayList<>(images.keySet());
  }
}
