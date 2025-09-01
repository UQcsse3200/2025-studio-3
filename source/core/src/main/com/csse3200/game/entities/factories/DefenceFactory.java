package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.components.npc.GhostAnimationController;
import com.csse3200.game.components.tasks.WanderTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.configs.NPCConfigs;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;


public class DefenceFactory {
    private static final NPCConfigs configs =
        FileLoader.readClass(NPCConfigs.class, "configs/NPCs.json");

    public static Entity createSigma() {
        Entity sigma = createBaseDefender();
        BaseEntityConfig config = configs.ghost; // update to whatever the name of the sigma is

        /* TODO Finn
        AnimationRenderComponent animator =
        new AnimationRenderComponent(
            ServiceLocator.getResourceService()
                .getAsset("images/ghost.atlas", TextureAtlas.class));
        
        animator.addAnimation("float", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("angry_float", 0.1f, Animation.PlayMode.LOOP);

        sigma
            .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
            .addComponent(animator)
            .addComponent(new GhostAnimationController());

        sigma.getComponent(AnimationRenderComponent.class).scaleEntity();
        */

        sigma.addComponent(new CombatStatsComponent(config.health, config.baseAttack));
            //.addComponent(animator)
            //.addComponent(new SigmaAnimationController());

        sigma.getComponent(AnimationRenderComponent.class).scaleEntity();
        return sigma;
    }
    
    public static Entity createBaseDefender() {
        Entity npc =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC));

        PhysicsUtils.setScaledCollider(npc, 0.9f, 0.4f);
        return npc;
    }

    private DefenceFactory() {
        throw new IllegalStateException("Instantiating static util class");
  }
}
