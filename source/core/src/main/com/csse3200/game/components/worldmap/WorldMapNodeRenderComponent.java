package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.ui.WorldMapNode;

/** Renders a world map node using the engine's rendering system */
public class WorldMapNodeRenderComponent extends UIComponent {
  private final WorldMapNode node;
  private final Vector2 worldSize;
  private final float nodeSize;
  private boolean showPrompt = false;

  /**
   * Constructor for the world map node render component.
   *
   * @param node the node to render
   * @param worldSize the size of the world map
   * @param nodeSize the size of the node
   */
  public WorldMapNodeRenderComponent(WorldMapNode node, Vector2 worldSize, float nodeSize) {
    this.node = node;
    this.worldSize = worldSize;
    this.nodeSize = nodeSize;
  }

  @Override
  protected void draw(SpriteBatch batch) {
    Texture nodeTexture =
        ServiceLocator.getResourceService().getAsset(node.getNodeTexture(), Texture.class);

    // Calculate position from node's world coordinates
    float x = node.getPositionX() * worldSize.x;
    float y = node.getPositionY() * worldSize.y;

    // Draw with slight enlargement if hovering/nearby
    float drawSize = nodeSize;
    float drawX = x;
    float drawY = y;

    if (showPrompt) {
      drawSize += 8f;
      drawX -= 4f;
      drawY -= 4f;
    }

    batch.draw(nodeTexture, drawX, drawY, drawSize, drawSize);

    // Draw prompt text if nearby
    if (showPrompt && node.isUnlocked()) {
      String prompt = "Press E to Enter";
      Label promptLabel = new Label(prompt, skin);
      promptLabel.setPosition(x, y + nodeSize + 20f);
      promptLabel.setColor(Color.WHITE);
    }

    // Draw lock reason if locked
    if (!node.isUnlocked() && node.getLockReason() != null) {
      Label lockReasonLabel = new Label(node.getLockReason(), skin);
      lockReasonLabel.setPosition(x, y - 20f);
      lockReasonLabel.setColor(Color.WHITE);
    }
  }

  public void setShowPrompt(boolean showPrompt) {
    this.showPrompt = showPrompt;
  }
}
