package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

public class PaddleInputComponent extends Component {
  private PaddleComponent paddle;

  public PaddleInputComponent(Entity paddleEntity) {
    this.paddle = paddleEntity.getComponent(PaddleComponent.class);
  }

  @Override
  public void update() {
    float delta = Gdx.graphics.getDeltaTime();
    int left = Input.Keys.LEFT;
    int altLeft = Input.Keys.A;
    // Note: Paddle controls are minigame-specific; no settings mapping provided, keep defaults
    if (Gdx.input.isKeyPressed(left) || Gdx.input.isKeyPressed(altLeft)) {
      paddle.moveLeft(delta);
    }
    int right = Input.Keys.RIGHT;
    int altRight = Input.Keys.D;
    if (Gdx.input.isKeyPressed(right) || Gdx.input.isKeyPressed(altRight)) {
      paddle.moveRight(delta);
    }
  }
}
