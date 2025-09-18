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
  private Label promptLabel;
  private Label lockReasonLabel;

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

    // Create labels and add them to the stage
    promptLabel = new Label("Press E to Enter", skin);
    promptLabel.setColor(Color.WHITE);
    promptLabel.setVisible(false);
    stage.addActor(promptLabel);

    lockReasonLabel = new Label("", skin);
    lockReasonLabel.setColor(Color.RED);
    lockReasonLabel.setVisible(false);
    stage.addActor(lockReasonLabel);
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
    updateLabelVisibility();
  }

  private void updateLabelVisibility() {
    if (promptLabel != null && lockReasonLabel != null) {
      // Show prompt if nearby and unlocked
      promptLabel.setVisible(showPrompt && node.isUnlocked());

      // Show lock reason if locked
      lockReasonLabel.setVisible(!node.isUnlocked() && node.getLockReason() != null);
      if (!node.isUnlocked() && node.getLockReason() != null) {
        lockReasonLabel.setText(node.getLockReason());
      }
    }
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

    // Update label positions (they're already managed by the stage for visibility)
    if (promptLabel != null) {
      promptLabel.setPosition(x, y + nodeSize + 20f);
    }
    if (lockReasonLabel != null) {
      lockReasonLabel.setPosition(x, y - 20f);
    }
  }

  public void setShowPrompt(boolean showPrompt) {
    this.showPrompt = showPrompt;
  }
}
