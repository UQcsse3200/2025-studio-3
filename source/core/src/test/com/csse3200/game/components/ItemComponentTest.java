package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.components.items.ItemComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class ItemComponentTest {

  @Test
  void testGrenadeTypeStored() {
    ItemComponent c = new ItemComponent(ItemComponent.Type.GRENADE);
    assertSame(ItemComponent.Type.GRENADE, c.getType());
  }

  @Test
  void testCoffeeTypeStored() {
    ItemComponent c = new ItemComponent(ItemComponent.Type.COFFEE);
    assertSame(ItemComponent.Type.COFFEE, c.getType());
  }

  @Test
  void testBuffTypeStored() {
    ItemComponent c = new ItemComponent(ItemComponent.Type.BUFF);
    assertSame(ItemComponent.Type.BUFF, c.getType());
  }

  @Test
  void testEmpTypeStored() {
    ItemComponent c = new ItemComponent(ItemComponent.Type.EMP);
    assertSame(ItemComponent.Type.EMP, c.getType());
  }

  @Test
  void testNukeTypeStored() {
    ItemComponent c = new ItemComponent(ItemComponent.Type.NUKE);
    assertSame(ItemComponent.Type.NUKE, c.getType());
  }

  @Test
  void testAttachToEntityAndRetrieve() {
    Entity e = new Entity();
    ItemComponent c = new ItemComponent(ItemComponent.Type.BUFF);
    e.addComponent(c);

    ItemComponent retrieved = e.getComponent(ItemComponent.class);
    assertNotNull(retrieved);
    assertSame(c, retrieved);
    assertSame(ItemComponent.Type.BUFF, retrieved.getType());
  }

  @Test
  void testNullTypeAccepted_CurrentBehavior() {
    ItemComponent c = assertDoesNotThrow(() -> new ItemComponent(null));
    assertNull(c.getType());
  }
}