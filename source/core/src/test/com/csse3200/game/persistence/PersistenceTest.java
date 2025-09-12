package com.csse3200.game.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.csse3200.game.progression.Profile;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersistenceTest {

  @Test
  void testProfile() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      Profile testProfile = new Profile();
      mockFileLoader
          .when(
              () ->
                  FileLoader.readClass(
                      eq(Profile.class), anyString(), eq(FileLoader.Location.EXTERNAL)))
          .thenReturn(testProfile);

      Savefile savefile = new Savefile("testProfile", 1234567890L);
      Persistence.load(savefile);

      assertNotNull(Persistence.profile());
      assertEquals(testProfile, Persistence.profile());
    }
  }

  @Test
  void testLoad() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      Profile expectedProfile = new Profile();
      String expectedPath =
          "The Day We Fought Back"
              + File.separator
              + "saves"
              + File.separator
              + "testProfile$1234567890.json";

      mockFileLoader
          .when(
              () -> FileLoader.readClass(Profile.class, expectedPath, FileLoader.Location.EXTERNAL))
          .thenReturn(expectedProfile);

      Savefile savefile = new Savefile("testProfile", 1234567890L);
      Persistence.load(savefile);

      mockFileLoader.verify(
          () -> FileLoader.readClass(Profile.class, expectedPath, FileLoader.Location.EXTERNAL));
      assertEquals(expectedProfile, Persistence.profile());
    }
  }

  @Test
  void testSave() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      // Create a new profile
      Persistence.load();

      // Set a name for the profile
      Persistence.profile().setName("testProfile");

      Persistence.save();

      mockFileLoader.verify(
          () ->
              FileLoader.writeClass(
                  eq(Persistence.profile()),
                  matches(".*testProfile\\$\\d+\\.json"),
                  eq(FileLoader.Location.EXTERNAL)));
    }
  }

  @Test
  void testFetch() {
    Files mockFiles = mock(Files.class);
    FileHandle mockRootDir = mock(FileHandle.class);
    FileHandle mockSaveFile = mock(FileHandle.class);
    Gdx.files = mockFiles;

    when(mockFiles.external(anyString())).thenReturn(mockRootDir);
    when(mockRootDir.exists()).thenReturn(true);
    when(mockSaveFile.name()).thenReturn("testProfile$1234567890.json");
    when(mockRootDir.list(".json")).thenReturn(new FileHandle[] {mockSaveFile});

    List<Savefile> result = Persistence.fetch();

    // Should return 3 slots, with the first one containing the save
    assertEquals(3, result.size());
    assertNotNull(result.get(0));
    assertEquals("testProfile", result.get(0).getName());
    assertNull(result.get(1));
    assertNull(result.get(2));

    verify(mockRootDir).list(".json");
  }
}
