package com.csse3200.game.entities.factories;

import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.services.ConfigService;
import org.junit.jupiter.api.BeforeEach;

import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefenceFactoryTest {
    private BaseDefenderConfig mockConfigService;

    @BeforeEach
    void setUp() {

        // mock defender config
        mockConfigService = mock(BaseDefenderConfig.class);

        // fake configs for each defender
        BaseDefenderConfig slingshooterConfig = cfg(50, 500, "images/entities/defences/sling_shooter.atlas", "images/entities/defences/sling_shooter_1.png", "images/effects/sling_projectile.png", 100, 1, "right");
        BaseDefenderConfig armyguyConfig = cfg(80, 1000, "images/entities/defences/machine_gun.atlas", "images/entities/defences/army_guy_1.png", "images/effects/bullet.png", 100, 1, "right");
        BaseDefenderConfig shadowConfig = cfg(20, 250, "images/entities/defences/shadow.atlas", "images/entities/defences/shadow_idle1.png", "images/effects/shock.png", 100, 1, "left");
    }

    private static BaseDefenderConfig cfg(
            int health, int range, String atlas, String asset, String projectile, int cost, int damage, String direction) {
        BaseDefenderConfig c = new BaseDefenderConfig();
        try {
            java.lang.reflect.Field healthField = BaseDefenderConfig.class.getDeclaredField("health");
            healthField.setAccessible(true);
            healthField.set(c, health);

            java.lang.reflect.Field rangeField = BaseDefenderConfig.class.getDeclaredField("range");
            rangeField.setAccessible(true);
            rangeField.set(c, range);

            java.lang.reflect.Field atlasField = BaseDefenderConfig.class.getDeclaredField("atlasPath");
            atlasField.setAccessible(true);
            atlasField.set(c, atlas);

            java.lang.reflect.Field assetField = BaseDefenderConfig.class.getDeclaredField("assetPath");
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
}
