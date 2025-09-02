package com.csse3200.game.entities;

import com.csse3200.game.entities.factories.WaveFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class EntitySpawnTest {

    private WaveFactory waveFactoryMock;

    @BeforeEach
    void setup() {
        waveFactoryMock = Mockito.mock(WaveFactory.class);
    }

    /**
     * Helper method to inject mocked WaveFactory into EntitySpawn.
     */
    private EntitySpawn createEntitySpawnWithMock(int robotWeight) {
        EntitySpawn entitySpawn = new EntitySpawn("test", robotWeight);
        // Replace the private final waveFactory via reflection
        try {
            var field = EntitySpawn.class.getDeclaredField("waveFactory");
            field.setAccessible(true);
            field.set(entitySpawn, waveFactoryMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return entitySpawn;
    }

    @Test
    void testNoSpawnWhenRobotWeightZero() {
        EntitySpawn entitySpawn = createEntitySpawnWithMock(0);

        Mockito.when(waveFactoryMock.getWaveWeight()).thenReturn(10);
        Mockito.when(waveFactoryMock.getMinZombiesSpawn()).thenReturn(1);

        entitySpawn.spawnEnemies();
        assertEquals(0, entitySpawn.getEntities().length);
    }

    @Test
    void testNoSpawnWhenWaveWeightZero() {
        EntitySpawn entitySpawn = createEntitySpawnWithMock(2);

        Mockito.when(waveFactoryMock.getWaveWeight()).thenReturn(0);
        Mockito.when(waveFactoryMock.getMinZombiesSpawn()).thenReturn(1);

        entitySpawn.spawnEnemies();
        assertEquals(0, entitySpawn.getEntities().length);
    }

    @Test
    void testSpawnExactDivisible() {
        EntitySpawn entitySpawn = createEntitySpawnWithMock(2);

        Mockito.when(waveFactoryMock.getWaveWeight()).thenReturn(10);
        Mockito.when(waveFactoryMock.getMinZombiesSpawn()).thenReturn(2);

        entitySpawn.spawnEnemies();

        Entity[] result = entitySpawn.getEntities();
        assertEquals(5, result.length, "Should spawn 5 robots since 10/2 = 5");
        for (Entity entity : result) {
            assertNotNull(entity);
        }
    }

    @Test
    void testSpawnRoundingUpWaveWeight() {
        EntitySpawn entitySpawn = createEntitySpawnWithMock(3);

        Mockito.when(waveFactoryMock.getWaveWeight()).thenReturn(10); // not divisible by 3
        Mockito.when(waveFactoryMock.getMinZombiesSpawn()).thenReturn(2);

        entitySpawn.spawnEnemies();

        Entity[] result = entitySpawn.getEntities();
        assertEquals(4, result.length, "WaveWeight=10 should round up to 11, then 11/3=3 robots, but min=2");
        for (Entity entity : result) {
            assertNotNull(entity);
        }
    }

    @Test
    void testSpawnRespectsMinimum() {
        EntitySpawn entitySpawn = createEntitySpawnWithMock(10);

        Mockito.when(waveFactoryMock.getWaveWeight()).thenReturn(10); // 10/10 = 1 robot
        Mockito.when(waveFactoryMock.getMinZombiesSpawn()).thenReturn(3);

        entitySpawn.spawnEnemies();

        Entity[] result = entitySpawn.getEntities();
        assertEquals(3, result.length, "Should spawn at least the minimum (3 robots)");
    }
}
