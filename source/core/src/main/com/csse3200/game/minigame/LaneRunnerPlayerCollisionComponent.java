package com.csse3200.game.minigame;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.components.Component;
import com.csse3200.game.physics.BodyUserData;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collision component for the Lane Runner player.
 */
public class LaneRunnerPlayerCollisionComponent extends Component {
  private static final Logger logger = LoggerFactory.getLogger(LaneRunnerPlayerCollisionComponent.class);
  
  /**
   * Creates a new LaneRunnerPlayerCollisionComponent.
   */
  public LaneRunnerPlayerCollisionComponent() {
    // No constructor parameters needed
  }

  @Override
  public void create() {
    entity.getEvents().addListener("collisionStart", this::onCollisionStart);
  }

  /**
   * Handles collision start events between the player and obstacles.
   * 
   * @param thisFixture the fixture of this entity (player)
   * @param otherFixture the fixture of the colliding entity
   */
  private void onCollisionStart(Fixture thisFixture, Fixture otherFixture) {
    logger.info("Collision detected! Player collided with something");
    
    // Get the other entity
    BodyUserData otherUserData = (BodyUserData) otherFixture.getBody().getUserData();
    if (otherUserData == null || otherUserData.getEntity() == null) {
      logger.warn("Collision with entity that has no user data");
      return;
    }
    
    // Check if the other entity has a LaneRunnerObstacleComponent
    LaneRunnerObstacleComponent obstacle = otherUserData.getEntity().getComponent(LaneRunnerObstacleComponent.class);
    if (obstacle == null) {
      logger.warn("Collision with entity that is not an obstacle");
      return;
    }
    
    logger.info("Player hit an obstacle - setting game over!");
    // Player hit an obstacle - game over
    ServiceLocator.getMinigameService().setGameOver(true);
  }
}
