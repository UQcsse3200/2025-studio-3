package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WorldMapService;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.ui.WorldMapNode;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles player movement and interaction on the world map */
public class WorldMapPlayerComponent extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapPlayerComponent.class);
  private static final float PLAYER_SPEED = 200f;
  private static final float INTERACTION_DISTANCE = 150f;
  private final Vector2 worldSize;
  private WorldMapNode nearbyNode = null;
  private Texture playerTexture;

  public WorldMapPlayerComponent(Vector2 worldSize) {
    this.worldSize = worldSize;
  }

  @Override
  public void create() {
    super.create();
    playerTexture = ServiceLocator.getResourceService().getAsset("images/character.png", Texture.class);
  }

  @Override
  public void update() {
    handleMovement();
    checkNodeProximity();
    handleNodeInteraction();
  }

  private void handleMovement() {
    float moveAmount = PLAYER_SPEED * Gdx.graphics.getDeltaTime();
    Vector2 position = entity.getPosition();

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      position.y += moveAmount;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      position.y -= moveAmount;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      position.x -= moveAmount;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      position.x += moveAmount;
    }

    // Clamp to world bounds
    position.x = Math.clamp(position.x, 0, worldSize.x - 96);
    position.y = Math.clamp(position.y, 0, worldSize.y - 96);

    entity.setPosition(position);
  }

  private void checkNodeProximity() {
    Vector2 playerPos = entity.getPosition();
    WorldMapService worldMapService = ServiceLocator.getWorldMapService();
    List<WorldMapNode> nodes = worldMapService.getAllNodes();

    WorldMapNode previousNearby = nearbyNode;
    nearbyNode = null;

    for (WorldMapNode node : nodes) {
      float nodeX = node.getPositionX() * worldSize.x;
      float nodeY = node.getPositionY() * worldSize.y;

      float distance = Vector2.dst(playerPos.x + 48, playerPos.y + 48, nodeX + 40, nodeY + 40);

      if (distance < INTERACTION_DISTANCE) {
        nearbyNode = node;
        break;
      }
    }

    // Update node render components for prompt display
    if (previousNearby != nearbyNode) {
      // This would require entity references to update render components
      // For now, we'll use events or a different approach
      updateNodePrompts();
    }
  }

  private void updateNodePrompts() {
    // Trigger event to update UI or use another mechanism
    // to communicate with render components
    if (nearbyNode != null) {
      entity.getEvents().trigger("nodeNearby", nearbyNode);
    } else {
      entity.getEvents().trigger("nodeLeft");
    }
  }

  private void handleNodeInteraction() {
    if (nearbyNode != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (nearbyNode.isUnlocked() && !nearbyNode.isCompleted()) {
        // Show warning dialog for unlocked nodes
        String message = "Do you want to enter " + nearbyNode.getLabel() + "?";
        ServiceLocator.getDialogService().warning(
          nearbyNode.getLabel(),
          message,
          // onConfirm callback - enter the node
          dialog -> {
            logger.info("Entering node: {}", nearbyNode.getLabel());
            // Mark as completed and unlock dependencies
            ServiceLocator.getWorldMapService().completeNode(nearbyNode.getRegistrationKey());
            // Trigger screen transition
            entity.getEvents().trigger("enterNode", nearbyNode);
          },
          // onCancel callback - do nothing
          dialog -> logger.info("Node entry cancelled for: {}", nearbyNode.getLabel())
        );
      } else {
        // Show error dialog for locked or completed nodes
        String message = nearbyNode.getLockReason() != null ? nearbyNode.getLockReason() : "This node is not available.";
        ServiceLocator.getDialogService().error(
          nearbyNode.getLabel(),
          message
        );
        logger.info("Node '{}' is not accessible: {}", nearbyNode.getLabel(), message);
      }
    }
  }

  public WorldMapNode getNearbyNode() {
    return nearbyNode;
  }

  @Override
  protected void draw(SpriteBatch batch) {
    if (playerTexture != null) {
      Vector2 position = entity.getPosition();
      // Draw player texture at world coordinates with appropriate size
      batch.draw(playerTexture, position.x, position.y, 96f, 96f);
    }
  }
}
