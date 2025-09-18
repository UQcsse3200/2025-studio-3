package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
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
  public void create() {
    super.create();
    ServiceLocator.getWorldMapService().registerNodeRenderComponent(this);
  }

  /**
   * Updates the proximity state for this node.
   *
   * @param nearbyNode the node the player is currently near, or null if none
   */
  public void updateProximityState(WorldMapNode nearbyNode) {
    boolean isNearby =
        nearbyNode != null
            && this.node.getRegistrationKey().equals(nearbyNode.getRegistrationKey());
    setShowPrompt(isNearby);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    Texture nodeTexture =
        ServiceLocator.getResourceService().getAsset(node.getNodeTexture(), Texture.class);
    if (node.isCompleted()) {
      nodeTexture =
          ServiceLocator.getResourceService().getAsset("images/nodes/completed.png", Texture.class);
    } else if (!node.isUnlocked()) {
      nodeTexture =
          ServiceLocator.getResourceService().getAsset("images/nodes/locked.png", Texture.class);
    }

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
  }

  public void setShowPrompt(boolean showPrompt) {
    this.showPrompt = showPrompt;
  }
}
