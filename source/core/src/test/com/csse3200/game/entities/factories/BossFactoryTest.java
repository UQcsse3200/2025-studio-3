package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseBossConfig;
import com.csse3200.game.entities.configs.BossConfigs;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Unit tests for the {@link BossFactory}. This test class manually manages a headless
 * GDX application lifecycle and mocks static file loading.
 */
class BossFactoryTest {

    private static Application application;
    private static BossConfigs mockBossConfigs;

    @BeforeAll
    static void beforeAll() {
        application = new HeadlessApplication(new ApplicationAdapter() {});
        Gdx.gl = mock(GL20.class);

        mockBossConfigs = new BossConfigs();
        mockBossConfigs.scrapTitan = cfg(100, 10, 1f, "atlas/scrap_titan.atlas", 2.0f, 0f);
        mockBossConfigs.samuraiBot = cfg(200, 20, 2f, "atlas/samurai.atlas", 3.0f, 0f);
        mockBossConfigs.gunBot = cfg(150, 15, 1.5f, "atlas/gun_Bot.atlas", 2.5f, 5f);

        MockedStatic<FileLoader> mockedFileLoader = mockStatic(FileLoader.class);
        mockedFileLoader.when(() -> FileLoader.readClass(BossConfigs.class, "configs/boss.json"))
                .thenReturn(mockBossConfigs);
    }

    @AfterAll
    static void afterAll() {
        application.exit();
    }

    @BeforeEach
    void setUp() {
        ServiceLocator.registerPhysicsService(new PhysicsService());
        ServiceLocator.registerTimeSource(new GameTime());

        ResourceService mockResourceService = mock(ResourceService.class);
        TextureAtlas mockAtlas = mock(TextureAtlas.class);
        ServiceLocator.registerResourceService(mockResourceService);
        when(mockResourceService.getAsset(anyString(), eq(TextureAtlas.class))).thenReturn(mockAtlas);


        RenderService mockRenderService = mock(RenderService.class);
        when(mockRenderService.getDebug()).thenReturn(mock(DebugRenderer.class));
        ServiceLocator.registerRenderService(mockRenderService);


        TextureAtlas.AtlasRegion region = mock(TextureAtlas.AtlasRegion.class);
        Array<TextureAtlas.AtlasRegion> regions = new Array<>(new TextureAtlas.AtlasRegion[]{region});
        when(mockAtlas.findRegions(anyString())).thenReturn(regions);
    }

    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }

    private static BaseBossConfig cfg(int health, int attack, float speed, String atlas, float scale, float range) {
        BaseBossConfig config = new BaseBossConfig();
        config.health = health;
        config.attack = attack;
        config.speed = speed;
        config.atlasFilePath = atlas;
        config.scale = scale;
        return config;
    }

    @Test
    void shouldCreateScrapTitanWithCorrectComponentsAndStats() {
        Entity scrapTitan = BossFactory.createBossType(BossFactory.BossTypes.SCRAP_TITAN);

        assertNotNull(scrapTitan);
        assertNotNull(scrapTitan.getComponent(PhysicsComponent.class));
        assertNotNull(scrapTitan.getComponent(CombatStatsComponent.class));
        assertNotNull(scrapTitan.getComponent(TouchAttackComponent.class));

        CombatStatsComponent stats = scrapTitan.getComponent(CombatStatsComponent.class);
        assertEquals(100, stats.getHealth());
        assertEquals(10, stats.getBaseAttack());

        scrapTitan.create();
        assertPhysicsLayers(scrapTitan, PhysicsLayer.BOSS);
    }

    @Test
    void shouldCreateSamuraiBotWithCorrectComponentsAndStats() {
        Entity samuraiBot = BossFactory.createBossType(BossFactory.BossTypes.SAMURAI_BOT);

        assertNotNull(samuraiBot);
        assertNotNull(samuraiBot.getComponent(PhysicsComponent.class));
        assertNotNull(samuraiBot.getComponent(CombatStatsComponent.class));
        assertNotNull(samuraiBot.getComponent(TouchAttackComponent.class));

        CombatStatsComponent stats = samuraiBot.getComponent(CombatStatsComponent.class);
        assertEquals(200, stats.getHealth());
        assertEquals(20, stats.getBaseAttack());

        samuraiBot.create();
        assertPhysicsLayers(samuraiBot, PhysicsLayer.BOSS);
    }

    @Test
    void shouldCreateGunBotWithCorrectComponentsAndStats() {
        Entity gunBot = BossFactory.createBossType(BossFactory.BossTypes.GUN_BOT);

        assertNotNull(gunBot);
        assertNotNull(gunBot.getComponent(PhysicsComponent.class));
        assertNotNull(gunBot.getComponent(CombatStatsComponent.class));
        assertNull(gunBot.getComponent(TouchAttackComponent.class));

        CombatStatsComponent stats = gunBot.getComponent(CombatStatsComponent.class);
        assertEquals(150, stats.getHealth());
        assertEquals(15, stats.getBaseAttack());

        gunBot.create();
        assertPhysicsLayers(gunBot, PhysicsLayer.BOSS);
    }

    private void assertPhysicsLayers(Entity entity, short expectedLayer) {
        ColliderComponent collider = entity.getComponent(ColliderComponent.class);
        HitboxComponent hitbox = entity.getComponent(HitboxComponent.class);
        Filter filter = collider.getFixture().getFilterData();

        assertEquals(expectedLayer, filter.categoryBits, "Collider category should be BOSS");
        assertEquals(expectedLayer, hitbox.getLayer(), "Hitbox layer should be BOSS");

        short expectedMask = (short) (PhysicsLayer.DEFAULT | PhysicsLayer.NPC | PhysicsLayer.OBSTACLE | PhysicsLayer.ENEMY);
        assertEquals(expectedMask, filter.maskBits, "Collider mask bits are incorrect");
    }
}