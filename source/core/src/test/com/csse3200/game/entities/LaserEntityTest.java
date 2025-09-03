package com.csse3200.game.entities;
import static com.csse3200.game.entities.factories.ObstacleFactory.createLaser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.factories.ObstacleFactory;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.ProjectileBoundsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class LaserEntityTest {
    @Nested
    @ExtendWith(GameExtension.class)
    class LaserFactoryTest {
        @BeforeEach
        void setupServices() {
            ServiceLocator.clear();

            // Mock ResourceService so textures don't crash
            ResourceService resourceService = mock(ResourceService.class);
            when(resourceService.getAsset(anyString(), any())).thenReturn(mock(Texture.class));
            ServiceLocator.registerResourceService(resourceService);

            // Register real PhysicsService so PhysicsComponent works
            ServiceLocator.registerPhysicsService(new PhysicsService());
        }
        @Test
        void shouldCreateLaserEntityWithAllComponents() {
            Entity laser = createLaser();

            assertNotNull(laser.getComponent(TextureRenderComponent.class));
            assertNotNull(laser.getComponent(PhysicsComponent.class));
            assertNotNull(laser.getComponent(CombatStatsComponent.class));
            assertNotNull(laser.getComponent(TouchAttackComponent.class));
            assertNotNull(laser.getComponent(HitboxComponent.class));
            assertNotNull(laser.getComponent(ProjectileBoundsComponent.class));
        }
        @Test
        void shouldBeKinematicBody() {
            Entity laser = createLaser();
            PhysicsComponent pc = laser.getComponent(PhysicsComponent.class);

            assertEquals(BodyDef.BodyType.KinematicBody, pc.getBody().getType());
        }
        @Test
        void shouldHaveCorrectVelocity() {
            Entity laser = createLaser();
            PhysicsComponent pc = laser.getComponent(PhysicsComponent.class);

            Vector2 velocity = pc.getBody().getLinearVelocity();
            assertEquals(5f, velocity.x, 0.001f);
            assertEquals(0f, velocity.y, 0.001f);
        }
        @Test
        void shouldHaveCorrectCombatStats() {
            Entity laser = createLaser();
            CombatStatsComponent stats = laser.getComponent(CombatStatsComponent.class);

            assertEquals(1, stats.getHealth());
            assertEquals(0, stats.getBaseAttack());
        }
        @Test
        void shouldHaveCorrectScale() {
            Entity laser = ObstacleFactory.createLaser();
            Vector2 scale = laser.getScale();

            assertEquals(0.2f, scale.x, 0.001f);
            assertEquals(1.0f, scale.y, 0.001f);
        }

        @Test
        void shouldHaveProjectileBoundsComponent() {
            Entity laser = ObstacleFactory.createLaser();

            ProjectileBoundsComponent bounds = laser.getComponent(ProjectileBoundsComponent.class);

            assertNotNull(bounds, "Laser must have a ProjectileBoundsComponent");
            assertEquals(laser, bounds.getEntity(), "ProjectileBoundsComponent should reference the laser entity");
        }

    }
}