package com.csse3200.game.minigame.WallPong;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.PhysicsComponent;

public class WallPongInput {
    private final Entity paddle;

    public WallPongInput(Entity paddle) {
        this.paddle = paddle;
    }

    public boolean keyDown(int keycode){
        PhysicsComponent physics = paddle.getComponent(PhysicsComponent.class);

        if(physics != null){
            Vector2 velocity = new Vector2();
            if(keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
                velocity.x = -WallPongConfig.PADDLE_SPEED;
            }else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D){
                velocity.x = WallPongConfig.PADDLE_SPEED;
            }
            physics.getBody().setLinearVelocity(velocity);

        }
        return true;
    }

    public boolean keyUp(int keycode){
        PhysicsComponent physics = paddle.getComponent(PhysicsComponent.class);

        if(physics != null && ((keycode == Input.Keys.LEFT || keycode == Input.Keys.A)||(keycode == Input.Keys.RIGHT || keycode == Input.Keys.D))){
            physics.getBody().setLinearVelocity(new Vector2(0, 0));
        }
        return true;
    }
}
