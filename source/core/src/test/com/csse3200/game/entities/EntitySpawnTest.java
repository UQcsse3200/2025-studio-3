package com.csse3200.game.entities;

import com.csse3200.game.entities.factories.WaveFactory;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests for EntitySpawn that avoid any rounding-up specific assertions.
 * We inject a mocked WaveFactory so tests are deterministic and don’t depend on asset files.
 */
@ExtendWith(GameExtension.class)
class EntitySpawnTest {

    /** Helper to create a spawner with a mocked WaveFactory returning the provided values. */
    private EntitySpawn makeSpawner(int robotWeight, int waveWeight, int minSpawn) {
        EntitySpawn spawner = new EntitySpawn("ignored-wave", robotWeight);

        WaveFactory factory = mock(WaveFactory.class);
        when(factory.getWaveWeight()).thenReturn(waveWeight);
        when(factory.getMinZombiesSpawn()).thenReturn(minSpawn);

        try {
            Field f = EntitySpawn.class.getDeclaredField("waveFactory");
            f.setAccessible(true);
            f.set(spawner, factory);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to inject WaveFactory mock", e);
        }

        return spawner;
    }

    @Test
    void testNoSpawnWhenRobotWeightZero() {
        // robotWeight <= 0 short-circuits to no entities
        EntitySpawn spawner = makeSpawner(/* robotWeight */ 0, /* waveWeight */ 10, /* min */ 1);
        spawner.spawnEnemies();
        assertEquals(0, spawner.getEntities().length);
    }

    @Test
    void testNoSpawnWhenWaveWeightZero() {
        EntitySpawn spawner = makeSpawner(/* robotWeight */ 2, /* waveWeight */ 0, /* min */ 1);
        spawner.spawnEnemies();
        assertEquals(0, spawner.getEntities().length);
    }

    @Test
    void testSpawnExactDivisible() {
        // 10 % 2 == 0, so 10/2 = 5
        EntitySpawn spawner = makeSpawner(/* robotWeight */ 2, /* waveWeight */ 10, /* min */ 1);
        spawner.spawnEnemies();
        assertEquals(5, spawner.getEntities().length);
    }

    @Test
    void testSpawnRespectsMinimum() {
        // wave=3, robot=5 -> not divisible so waveWeight+=1 => 4; 4/5=0 -> below min => bump to min (2)
        EntitySpawn spawner = makeSpawner(/* robotWeight */ 5, /* waveWeight */ 3, /* min */ 2);
        spawner.spawnEnemies();
        assertEquals(2, spawner.getEntities().length);
    }

    @Test
    void testSpawnWhenAboveMinimum() {
        // wave=12, robot=5 -> not divisible so waveWeight+=1 => 13; 13/5 = 2; min=1 -> stays 2
        EntitySpawn spawner = makeSpawner(/* robotWeight */ 5, /* waveWeight */ 12, /* min */ 1);
        spawner.spawnEnemies();
        assertEquals(2, spawner.getEntities().length);
    }
}
