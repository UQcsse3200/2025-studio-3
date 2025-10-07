package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.NotNull;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefenceFactoryTest {
    private ConfigService mockConfigService;

    @BeforeEach
    void setUp() {
        // Physics world
        ServiceLocator.registerPhysicsService(new PhysicsService());

        // Render service with debug renderer
        RenderService mockRenderService = mock(RenderService.class);
        DebugRenderer mockDebugRenderer = mock(DebugRenderer.class);
        when(mockRenderService.getDebug()).thenReturn(mockDebugRenderer);
        ServiceLocator.registerRenderService(mockRenderService);

        ServiceLocator.registerTimeSource(new GameTime());

        // Resource service + atlas for animator
        ResourceService mockResourceService = mock(ResourceService.class);
        TextureAtlas mockAtlas = mock(TextureAtlas.class);
        ServiceLocator.registerResourceService(mockResourceService);
        when(mockResourceService.getAsset(anyString(), eq(TextureAtlas.class))).thenReturn(mockAtlas);

        TextureAtlas.AtlasRegion region = mock(TextureAtlas.AtlasRegion.class);
        when(region.getRegionWidth()).thenReturn(16);
        when(region.getRegionHeight()).thenReturn(16);
        when(region.getTexture()).thenReturn(mock(Texture.class));

        Array<TextureAtlas.AtlasRegion> list = new Array<>();
        list.add(region);

        for (String name :
                new String[] {"default", "idle", "attack"}) {
            when(mockAtlas.findRegions(name)).thenReturn(list);
            when(mockAtlas.findRegion(name)).thenReturn(region);
        }

        ProfileService mockProfileService = mock(ProfileService.class);
        Profile mockProfile = mock(Profile.class);
        when(mockProfileService.getProfile()).thenReturn(mockProfile);
        ServiceLocator.registerProfileService(mockProfileService);
        SkillSet mockSkillSet = mock(SkillSet.class);
        when(mockProfile.getSkillset()).thenReturn(mockSkillSet);

        // mock defender config
        mockConfigService = mock(ConfigService.class);

        // fake configs for each defender
        when(mockConfigService.getDefenderConfig("slingshooter")).thenReturn(cfg(50, 500, "images/entities/defences/sling_shooter.atlas", "images/entities/defences/sling_shooter_1.png", "images/effects/sling_projectile.png", 100, 1, "right"));
        when(mockConfigService.getDefenderConfig("armyguy")).thenReturn(cfg(80, 1000, "images/entities/defences/machine_gun.atlas", "images/entities/defences/army_guy_1.png", "images/effects/bullet.png", 100, 1, "right"));
        when(mockConfigService.getDefenderConfig("shadow")).thenReturn(cfg(20, 250, "images/entities/defences/shadow.atlas", "images/entities/defences/shadow_idle1.png", "images/effects/shock.png", 100, 1, "left"));
    }

    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }


    private static BaseDefenderConfig cfg(
            int health, int range, String atlas, String asset, String projectile, int cost, int damage, String direction) {
        BaseDefenderConfig c = new BaseDefenderConfig();
        try {
            java.lang.reflect.Field healthField = BaseDefenderConfig.class.getSuperclass().getDeclaredField("health");
            healthField.setAccessible(true);
            healthField.set(c, health);

            java.lang.reflect.Field rangeField = BaseDefenderConfig.class.getDeclaredField("range");
            rangeField.setAccessible(true);
            rangeField.set(c, range);

            java.lang.reflect.Field atlasField = BaseDefenderConfig.class.getSuperclass().getDeclaredField("atlasPath");
            atlasField.setAccessible(true);
            atlasField.set(c, atlas);

            java.lang.reflect.Field assetField = BaseDefenderConfig.class.getSuperclass().getDeclaredField("assetPath");
            assetField.setAccessible(true);
            assetField.set(c, asset);

            java.lang.reflect.Field projectileField = BaseDefenderConfig.class.getDeclaredField("projectilePath");
            projectileField.setAccessible(true);
            projectileField.set(c, projectile);

            java.lang.reflect.Field costField = BaseDefenderConfig.class.getDeclaredField("cost");
            costField.setAccessible(true);
            costField.set(c, cost);

            java.lang.reflect.Field damageField = BaseDefenderConfig.class.getDeclaredField("damage");
            damageField.setAccessible(true);
            damageField.set(c, damage);

            java.lang.reflect.Field directionField = BaseDefenderConfig.class.getDeclaredField("direction");
            directionField.setAccessible(true);
            directionField.set(c, direction);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create test defender config", e);
        }
        return c;
    }

    @Test
    void testCreateDefenceUnit() {
        // create each defence entity
        for (String defense : new String[]{"slingshooter", "armyguy", "shadow"}) {
            Entity defender = DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));
            assertNotNull(defender, () -> "DefenceFactory.createDefenceUnit(" + defense + ") returned null");

            // check expected components and layers

        }
    }
}
