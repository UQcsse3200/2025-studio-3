package com.csse3200.game.physics.attacking_system;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.BodyUserData;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;

/**
 * the damage mapping system handles damage interactions between entities.
 * It listens to collisionStart event and applies the damage logic.
 */
public class damageMappingSystem  {

    public damageMappingSystem(Entity entity) {
        entity.getEvents().addListener("collisionStart",this::onCollisionStart);
    }
    public void onCollisionStart(Fixture fixtureA, Fixture fixtureB) {

        Entity entityA =  ((BodyUserData)fixtureA.getBody().getUserData()).entity;
        Entity entityB = ((BodyUserData)fixtureB.getBody().getUserData()).entity;
        if(entityA==null || entityB==null) return;

        Boolean isProjectile = (Boolean) entityA.getProperty("isProjectile");
        if (isProjectile==null||!isProjectile) return;
            CombatStatsComponent attackerStats = (CombatStatsComponent)entityA.getComponent(CombatStatsComponent.class);
             CombatStatsComponent victimStats = (CombatStatsComponent)entityB.getComponent(CombatStatsComponent.class);

             if(attackerStats!=null && victimStats!=null) {
                 victimStats.hit(attackerStats);
                if (isProjectile==true){
                    entityA.getEvents().trigger("destroy");
                }
             }




    }
}
