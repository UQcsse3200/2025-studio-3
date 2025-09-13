package com.csse3200.game.components;

import static org.junit.Assert.assertEquals;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
public class ProjectileComponentTest {
  @Test
  void getsDamage() {
    ProjectileComponent component = new ProjectileComponent(10);
    assertEquals(10, component.getDamage());
  }
}
