package com.csse3200.game.physics.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.csse3200.game.components.Component;
import com.csse3200.game.physics.components.PhysicsComponent;

public class LeftMovementComponent extends Component {
    private float speed = 2f;
    private PhysicsComponent physicsComponent;

    @Override
    public void create() {
        physicsComponent = entity.getComponent(PhysicsComponent.class);
    }

    @Override
    public void update() {
        Body body = physicsComponent.getBody();
        // constant velocity to left
        Vector2 desiredVelocity = new Vector2(-speed, 0);
        Vector2 velocity = body.getLinearVelocity();
        Vector2 impulse = desiredVelocity.cpy().sub(velocity).scl(body.getMass());
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

}
