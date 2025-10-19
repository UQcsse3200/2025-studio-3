package com.csse3200.game.minigame;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

public class CollisionComponent extends Component {
  private Image target;
  private static final float VISIBLE_PADDLE_HEIGHT = 10f;
  private static final float COLLISION_WIDTH_RATIO = 0.25f;

  public CollisionComponent(Image target) {
    this.target = target;
  }

  public void checkCollision(float delta) {
    Entity owner = entity;
    if (owner == null) return;

    BallComponent ball = owner.getComponent(BallComponent.class);
    if (ball == null) return;

    Rectangle ballRect =
        new Rectangle(
            ball.getImage().getX(),
            ball.getImage().getY(),
            ball.getImage().getWidth(),
            ball.getImage().getHeight());

    float imageWidth = target.getWidth();
    float imageHeight = target.getHeight();
    float effectiveWidth = imageWidth * COLLISION_WIDTH_RATIO;
    float effectiveHeight = VISIBLE_PADDLE_HEIGHT;
    float xOffset = (imageWidth - effectiveWidth) / 2f;
    float yOffset = (imageHeight / 2f) - (effectiveHeight / 2f);

    Rectangle targetRect =
        new Rectangle(
            target.getX() + xOffset, target.getY() + yOffset, effectiveWidth, effectiveHeight);

    if (ballRect.overlaps(targetRect)) {
      if (ball.getVelocityY() < 0) {
        ball.reverseY();
        float repositionY = target.getY() + yOffset + effectiveHeight + 2f;
        ball.getImage().setY(repositionY);
      }
    }
  }
}
