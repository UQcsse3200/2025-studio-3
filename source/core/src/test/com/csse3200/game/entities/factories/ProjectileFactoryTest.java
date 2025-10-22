package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.Test;

class ProjectileFactoryTest {
  @Test
  void testCreateProjectile() {
    ServiceLocator.registerTimeSource(new GameTime());

    ServiceLocator.registerPhysicsService(new PhysicsService());

    // Render service with debug renderer
    RenderService mockRenderService = mock(RenderService.class);
    DebugRenderer mockDebugRenderer = mock(DebugRenderer.class);
    when(mockRenderService.getDebug()).thenReturn(mockDebugRenderer);
    ServiceLocator.registerRenderService(mockRenderService);

    // Resource service + atlas for animator
    ResourceService mockResourceService = mock(ResourceService.class);
    TextureAtlas mockAtlas = mock(TextureAtlas.class);
    ServiceLocator.registerResourceService(mockResourceService);
    when(mockResourceService.getAsset(anyString(), eq(TextureAtlas.class))).thenReturn(mockAtlas);

    Texture mockTexture = mock(Texture.class);
    when(mockTexture.getWidth()).thenReturn(16);
    when(mockTexture.getHeight()).thenReturn(16);
    when(mockResourceService.getAsset("images/effects/bullet.png", Texture.class))
        .thenReturn(mockTexture);

    Entity proj = ProjectileFactory.createProjectile("images/effects/bullet.png", 1);

    assertNotNull(proj, "Projectile entity should not be null");

    assertNotNull(
        proj.getComponent(PhysicsComponent.class), "Projectile should have a PhysicsComponent");
    assertNotNull(
        proj.getComponent(ColliderComponent.class), "Projectile should have a ColliderComponent");
    assertNotNull(
        proj.getComponent(HitboxComponent.class), "Projectile should have a HitboxComponent");
    assertNotNull(
        proj.getComponent(TouchAttackComponent.class),
        "Projectile should have a TouchAttackComponent");
    assertNotNull(
        proj.getComponent(CombatStatsComponent.class),
        "Projectile should have a CombatStatsComponent");
    TextureRenderComponent render = proj.getComponent(TextureRenderComponent.class);
    assertNotNull(render, "Projectile should have a TextureRenderComponent");

    assertEquals(
        PhysicsLayer.PROJECTILE,
        proj.getComponent(HitboxComponent.class).getLayer(),
        "Hitbox layer should be PROJECTILE");
    assertEquals(
        1,
        proj.getComponent(CombatStatsComponent.class).getHealth(),
        "Projectile health should be 1");
    assertEquals(
        1,
        proj.getComponent(CombatStatsComponent.class).getBaseAttack(),
        "Projectile damage should be 1");
  }
    @Test
    void testCreateBossProjectile() {
        ServiceLocator.registerTimeSource(new GameTime());
        ServiceLocator.registerPhysicsService(new PhysicsService());

        RenderService mockRenderService = mock(RenderService.class);
        DebugRenderer mockDebugRenderer = mock(DebugRenderer.class);
        when(mockRenderService.getDebug()).thenReturn(mockDebugRenderer);
        ServiceLocator.registerRenderService(mockRenderService);

        ResourceService mockResourceService = mock(ResourceService.class);
        ServiceLocator.registerResourceService(mockResourceService);

        Texture mockTexture = mock(Texture.class);
        when(mockTexture.getWidth()).thenReturn(32);
        when(mockTexture.getHeight()).thenReturn(32);
        when(mockResourceService.getAsset("images/effects/gun_bot_fireball.png", Texture.class))
                .thenReturn(mockTexture);

        Entity bossProj = ProjectileFactory.createBossProjectile(20);

        assertNotNull(bossProj, "Boss projectile should not be null");

        assertNotNull(bossProj.getComponent(PhysicsComponent.class),
                "Boss projectile should have PhysicsComponent");
        assertNotNull(bossProj.getComponent(ColliderComponent.class),
                "Boss projectile should have ColliderComponent");
        assertNotNull(bossProj.getComponent(HitboxComponent.class),
                "Boss projectile should have HitboxComponent");
        assertNotNull(bossProj.getComponent(TouchAttackComponent.class),
                "Boss projectile should have TouchAttackComponent");
        assertNotNull(bossProj.getComponent(CombatStatsComponent.class),
                "Boss projectile should have CombatStatsComponent");
        assertNotNull(bossProj.getComponent(TextureRenderComponent.class),
                "Boss projectile should have TextureRenderComponent");

        assertEquals(
                PhysicsLayer.BOSS_PROJECTILE,
                bossProj.getComponent(HitboxComponent.class).getLayer(),
                "Hitbox layer should be BOSS_PROJECTILE"
        );

        ColliderComponent collider = bossProj.getComponent(ColliderComponent.class);
        assertEquals(
                PhysicsLayer.BOSS_PROJECTILE,
                collider.getLayer(),
                "Collider layer should be BOSS_PROJECTILE"
        );
        CombatStatsComponent stats = bossProj.getComponent(CombatStatsComponent.class);
        assertEquals(1, stats.getHealth(), "Boss projectile health should be 1");
        assertEquals(20, stats.getBaseAttack(), "Boss projectile damage should be 20");

        TextureRenderComponent render = bossProj.getComponent(TextureRenderComponent.class);
        assertNotNull(render.getTexture(), "Render component should hold a texture");
    }

}
