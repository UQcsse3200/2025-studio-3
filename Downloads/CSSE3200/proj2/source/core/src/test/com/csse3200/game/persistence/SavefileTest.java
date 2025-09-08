package com.csse3200.game.persistence;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(GameExtension.class)
class SavefileTest {
  private Savefile savefile;
  private final String name = "TestSave";
  private final Long date = 1704067200000L;

  @BeforeEach
  void setUp() {
    savefile = new Savefile(name, date);
  }

  @Test
  void testConstructor() {
    Savefile save = new Savefile("MySave", 1234567890L);
    assertEquals("MySave", save.getName());
    assertEquals(1234567890L, save.getDate());
  }

  @Test
  void testFromStringValid() {
    String saveString = "TestSave$1704067200000";
    Savefile result = Savefile.fromString(saveString);
    assertNotNull(result);
    assertEquals("TestSave", result.getName());
    assertEquals(1704067200000L, result.getDate());
  }

  @Test
  void testToString() {
    String result = savefile.toString();
    String expected = "TestSave$1704067200000";
    assertEquals(expected, result);
  }

  @Test
  void testGetDisplayNameWithUUID() {
    String uuidString = "550e8400-e29b-41d4-a716-446655440000";
    Savefile save = new Savefile(uuidString, date);
    assertEquals("Autosave", save.getDisplayName());
  }

  @Test
  void testGetDisplayNameWithRegularName() {
    assertEquals("TestSave", savefile.getDisplayName());
  }

  @Test
  void testGetDisplayDate() {
    String result = savefile.getDisplayDate();
    System.out.println(result);
    assertNotNull(result); 
  }
}