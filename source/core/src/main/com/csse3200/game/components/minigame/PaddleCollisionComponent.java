package com.csse3200.game.components.minigame;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.components.Component;
import com.csse3200.game.physics.BodyUserData;
import com.csse3200.game.services.ServiceLocator;

/**
 * Handles collision detection between the ball and paddle in the paddle game. Uses the physics
 * system's collision events instead of manual collision checking.
 */
public class PaddleCollisionComponent extends Component {
  @Override
  public void create() {
    super.create();
    // Listen for collision events
    entity.getEvents().addListener("collisionStart", this::onCollisionStart);
  }

  /**
   * Handles collision start events between the ball and paddle.
   *
   * @param thisFixture the fixture of this entity
   * @param otherFixture the fixture of the colliding entity
   */
  private void onCollisionStart(Fixture thisFixture, Fixture otherFixture) {
    // Get the other entity
    BodyUserData otherUserData = (BodyUserData) otherFixture.getBody().getUserData();
    if (otherUserData == null || otherUserData.getEntity() == null) return;

    // Check if the other entity has a BallComponent
    BallComponent otherBall = otherUserData.getEntity().getComponent(BallComponent.class);
    if (otherBall == null) return;

    // Get our paddle component
    PaddleComponent paddle = entity.getComponent(PaddleComponent.class);
    if (paddle == null) return;

    // Only reverse the ball's Y velocity if it's moving downward (towards the paddle)
    if (otherBall.getVelocityY() < 0) {
      otherBall.hitPaddle();

      // Update the minigame service with the score
      ServiceLocator.getMinigameService().setScore(otherBall.getScore());
    }
  }
}
