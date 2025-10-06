package com.csse3200.game.persistence;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class SavefileTest {
  private Savefile savefile;
  private final String name = "TestSave";
  private final Long date = 1704067200000L;

  @BeforeEach
  void setUp() {
    savefile = new Savefile(name, date, 1);
  }

  @Test
  void testConstructor() {
    Savefile save = new Savefile("MySave", 1234567890L, 2);
    assertEquals("MySave", save.getName());
    assertEquals(1234567890L, save.getDate());
    assertEquals(2, save.getSlot());
  }

  @Test
  void testFromStringValid() {
    String saveString = "TestSave$1704067200000$1";
    Savefile result = Savefile.fromString(saveString);
    assertNotNull(result);
    assertEquals("TestSave", result.getName());
    assertEquals(1704067200000L, result.getDate());
    assertEquals(1, result.getSlot());
  }

  @Test
  void testFromStringInvalid() {
    String saveString = "TestSave$1704067200000";
    Savefile result = Savefile.fromString(saveString);
    assertNull(result);
    String saveString2 = "TestSave$17040672000x0$1";
    Savefile result2 = Savefile.fromString(saveString2);
    assertNull(result2);
  }

  @Test
  void testToString() {
    String result = savefile.toString();
    String expected = "TestSave$1704067200000$1";
    assertEquals(expected, result);
  }

  @Test
  void testGetDisplayNameWithUUID() {
    String uuidString = "550e8400-e29b-41d4-a716-446655440000";
    Savefile save = new Savefile(uuidString, date, 1);
    assertEquals("Autosave", save.getDisplayName());
  }

  @Test
  void testGetDisplayNameWithRegularName() {
    assertEquals("TestSave", savefile.getDisplayName());
  }

  @Test
  void testGetDisplayDate() {
    String result = savefile.getDisplayDate();
    assertNotNull(result);
  }
}
