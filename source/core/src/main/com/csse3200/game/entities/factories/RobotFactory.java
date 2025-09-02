package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ServiceLocator;

public class RobotFactory {
    public static Entity createRobot(BaseEntityConfig config) {
        final String atlasPath = "images/robot_placeholder.atlas";
        var rs = ServiceLocator.getResourceService();

        AnimationRenderComponent animator = new AnimationRenderComponent(
                rs.getAsset(atlasPath, TextureAtlas.class));

        animator.addAnimation("chill", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("angry", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("default", 1f, Animation.PlayMode.NORMAL);

        Entity robot = new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator);

        animator.scaleEntity();
        animator.startAnimation("chill"); // start an animation

        // make a bit larger
        robot.setScale(robot.getScale().x * 4f, robot.getScale().y * 4f);

        return robot;
    }
}
