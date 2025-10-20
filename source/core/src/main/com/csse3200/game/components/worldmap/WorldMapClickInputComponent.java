package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.csse3200.game.components.CameraComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.screens.WorldMapScreen;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.WorldMapNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldMapClickInputComponent extends InputComponent {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapClickInputComponent.class);
  private final WorldMapScreen screen;
  private Entity player;

  /**
   * Constructor for the world map click input component.
   * 
   * @param screen the world map screen
   * @param player the player entity
   * @param priority the priority of the input component
   */
  public WorldMapClickInputComponent(WorldMapScreen screen, Entity player, int priority) {
    super(priority);
    this.screen = screen;
    this.player = player;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    // Only handle left mouse button
    if (button != Input.Buttons.LEFT) {
      return false;
    }

    // Try to resolve the player entity dynamically (in case it was not set during init)
    if (player == null) {
      try {
        player = ServiceLocator.getWorldMapService().getPlayer();
      } catch (Exception e) {
        player = null;
      }
    }

    if (player == null) {
      // Player not yet ready; ignore click
      return false;
    }

    // Retrieve the camera from the world map screen
    CameraComponent cam = screen != null ? screen.getCameraComponent() : null;
    if (cam == null || cam.getCamera() == null) {
      return false;
    }

    // Convert screen-space coordinates to world-space coordinates
    Vector3 world = new Vector3(screenX, screenY, 0f);
    cam.getCamera().unproject(world);

    // Ensure world map service is available
    if (ServiceLocator.getWorldMapService() == null) {
      return false;
    }

    // Check whether the click hit a world map node
    WorldMapNode target = ServiceLocator.getWorldMapService().findNodeAt(world.x, world.y);

    if (target == null) {
      logger.info("[WorldMapClickInput] No node at ({}, {})", world.x, world.y);
      return false;
    }

    // Retrieve the player's movement component and move to the target node
    WorldMapPlayerComponent mover = player.getComponent(WorldMapPlayerComponent.class);
    if (mover == null) {
      return false;
    }

    return mover.moveToNode(target);
  }
}
