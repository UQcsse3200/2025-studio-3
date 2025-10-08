package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.csse3200.game.components.npc.CarrierHealthWatcherComponent;

@ExtendWith(GameExtension.class)
class CarrierHealthWatcherComponentTest {

  @Test
  void firesWhenAtOrBelowThreshold() {
    Entity carrier = new Entity()
            .addComponent(new CombatStatsComponent(100, 0))
            .addComponent(new CarrierHealthWatcherComponent(0.5f));
    AtomicInteger spawnCount = new AtomicInteger(0);
    carrier.getEvents().addListener("spawnMinion", spawnCount::incrementAndGet);
    carrier.create();

    // Above threshold -> no fire
    carrier.getComponent(CombatStatsComponent.class).setHealth(60);
    carrier.getComponent(CarrierHealthWatcherComponent.class).update();
    assertEquals(0, spawnCount.get(), "Should not trigger above threshold");

    // Equal to threshold -> fire
    carrier.getComponent(CombatStatsComponent.class).setHealth(50);
    carrier.getComponent(CarrierHealthWatcherComponent.class).update();
    assertEquals(1, spawnCount.get(), "Should trigger at threshold");

    // Below threshold -> still only once
    carrier.getComponent(CombatStatsComponent.class).setHealth(40);
    carrier.getComponent(CarrierHealthWatcherComponent.class).update();
    assertEquals(1, spawnCount.get(), "Should only trigger once");
  }

  @Test
  void doesNotFireAboveThreshold() {
    Entity carrier = new Entity()
            .addComponent(new CombatStatsComponent(80, 0))
            .addComponent(new CarrierHealthWatcherComponent(0.25f)); // threshold = 20
    AtomicInteger spawnCount = new AtomicInteger(0);
    carrier.getEvents().addListener("spawnMinion", spawnCount::incrementAndGet);
    carrier.create();

    carrier.getComponent(CombatStatsComponent.class).setHealth(21);
    carrier.getComponent(CarrierHealthWatcherComponent.class).update();

    assertEquals(0, spawnCount.get());
  }

  @Test
  void firesOnlyOnceEvenIfHealthFluctuates() {
    Entity carrier = new Entity()
            .addComponent(new CombatStatsComponent(100, 0))
            .addComponent(new CarrierHealthWatcherComponent(0.3f)); // threshold = 30
    AtomicInteger spawnCount = new AtomicInteger(0);
    carrier.getEvents().addListener("spawnMinion", spawnCount::incrementAndGet);
    carrier.create();

    // Cross threshold -> fire
    carrier.getComponent(CombatStatsComponent.class).setHealth(30);
    carrier.getComponent(CarrierHealthWatcherComponent.class).update();
    assertEquals(1, spawnCount.get());

    // Go back up (healed) and down again -> still only once
    carrier.getComponent(CombatStatsComponent.class).setHealth(70);
    carrier.getComponent(CarrierHealthWatcherComponent.class).update();
    carrier.getComponent(CombatStatsComponent.class).setHealth(10);
    carrier.getComponent(CarrierHealthWatcherComponent.class).update();

    assertEquals(1, spawnCount.get(), "Trigger must be one-shot");
  }

  @Test
  void safeWhenNoCombatStats() {
    Entity carrier = new Entity()
            .addComponent(new CarrierHealthWatcherComponent(0.5f));
    AtomicInteger spawnCount = new AtomicInteger(0);
    carrier.getEvents().addListener("spawnMinion", spawnCount::incrementAndGet);
    carrier.create();

    assertDoesNotThrow(() ->
            carrier.getComponent(CarrierHealthWatcherComponent.class).update());
    assertEquals(0, spawnCount.get(), "No stats means no trigger");
  }

  @Test
  void clampsNegativeThresholdToZero_fireOnlyAtZero() {
    // threshold < 0 should clamp to 0 => triggers only when health <= 0
    Entity carrier = new Entity()
            .addComponent(new CombatStatsComponent(10, 0))
            .addComponent(new CarrierHealthWatcherComponent(-0.2f));
    AtomicInteger spawnCount = new AtomicInteger(0);
    carrier.getEvents().addListener("spawnMinion", spawnCount::incrementAndGet);
    carrier.create();

    carrier.getComponent(CombatStatsComponent.class).setHealth(1);
    carrier.getComponent(CarrierHealthWatcherComponent.class).update();
    assertEquals(0, spawnCount.get());

    carrier.getComponent(CombatStatsComponent.class).setHealth(0);
    carrier.getComponent(CarrierHealthWatcherComponent.class).update();
    assertEquals(1, spawnCount.get());
  }

  @Test
  void clampsOverOneThresholdToOne_fireImmediately() {
    // threshold > 1 should clamp to 1 => fires immediately on first update (cur == maxHp)
    Entity carrier = new Entity()
            .addComponent(new CombatStatsComponent(42, 0))
            .addComponent(new CarrierHealthWatcherComponent(1.5f));
    AtomicInteger spawnCount = new AtomicInteger(0);
    carrier.getEvents().addListener("spawnMinion", spawnCount::incrementAndGet);
    carrier.create();

    carrier.getComponent(CarrierHealthWatcherComponent.class).update();
    assertEquals(1, spawnCount.get(), "Should trigger immediately at threshold=1");
  }
}
