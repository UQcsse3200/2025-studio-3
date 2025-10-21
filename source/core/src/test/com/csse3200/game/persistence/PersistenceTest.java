package com.csse3200.game.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.Profile;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(GameExtension.class)
class PersistenceTest {

  @Test
  void testLoad() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      Profile testProfile = new Profile();
      mockFileLoader
          .when(
              () ->
                  FileLoader.readClass(
                      eq(Profile.class), anyString(), eq(FileLoader.Location.EXTERNAL)))
          .thenReturn(testProfile);

      Savefile savefile = new Savefile("testProfile", 1234567890L, 1);
      var result = Persistence.load(savefile);

      assertNotNull(result);
      assertEquals(testProfile, result.getKey());
      assertEquals(1, result.getValue());
    }
  }

  @Test
  void testLoadWithPath() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      Profile expectedProfile = new Profile();
      String expectedPath =
          "The Day We Fought Back"
              + File.separator
              + "saves"
              + File.separator
              + "testProfile$1234567890$1.json";

      mockFileLoader
          .when(
              () -> FileLoader.readClass(Profile.class, expectedPath, FileLoader.Location.EXTERNAL))
          .thenReturn(expectedProfile);

      Savefile savefile = new Savefile("testProfile", 1234567890L, 1);
      var result = Persistence.load(savefile);

      mockFileLoader.verify(
          () -> FileLoader.readClass(Profile.class, expectedPath, FileLoader.Location.EXTERNAL));
      assertEquals(expectedProfile, result.getKey());
      assertEquals(1, result.getValue());
    }
  }

  @Test
  void testSave() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      // Create a new profile
      mockFileLoader
          .when(
              () ->
                  FileLoader.writeClass(
                      any(Profile.class), anyString(), eq(FileLoader.Location.EXTERNAL)))
          .thenAnswer(inv -> null);

      Files mockFiles = mock(Files.class);
      FileHandle mockRoot = mock(FileHandle.class);
      Gdx.files = mockFiles;
      when(mockFiles.external(anyString())).thenReturn(mockRoot);
      when(mockRoot.exists()).thenReturn(true);
      when(mockRoot.list(".json")).thenReturn(new FileHandle[0]);

      Persistence.create("testProfile", 1);
      mockFileLoader.verify(
          () ->
              FileLoader.writeClass(
                  any(Profile.class),
                  matches(".*testProfile\\$\\d+\\$1\\.json"),
                  eq(FileLoader.Location.EXTERNAL)),
          atLeastOnce());
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
    when(mockSaveFile.name()).thenReturn("testProfile$1234567890$1.json");
    when(mockRootDir.list(".json")).thenReturn(new FileHandle[] {mockSaveFile});

    List<Savefile> result = Persistence.fetch();

    // Should return 3 slots, with the first one containing the save
    assertEquals(3, result.size());
    assertNotNull(result.get(0));
    assertEquals("testProfile", result.get(0).getName());
    assertEquals(1, result.get(0).getSlot());
    assertNull(result.get(1));
    assertNull(result.get(2));

    verify(mockRootDir).list(".json");
  }

  @Test
  void testLoadFailure() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      mockFileLoader
          .when(
              () ->
                  FileLoader.readClass(
                      eq(Profile.class), anyString(), eq(FileLoader.Location.EXTERNAL)))
          .thenReturn(null);
      Savefile savefile = new Savefile("testProfile", 1234567890L, 1);

      assertThrows(IllegalStateException.class, () -> Persistence.load(savefile));
    }
  }

  @Test
  void testCreate() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      mockFileLoader
          .when(
              () ->
                  FileLoader.writeClass(
                      any(Profile.class), anyString(), eq(FileLoader.Location.EXTERNAL)))
          .thenAnswer(inv -> null);
      Files mockFiles = mock(Files.class);
      FileHandle mockRoot = mock(FileHandle.class);
      Gdx.files = mockFiles;
      when(mockFiles.external(anyString())).thenReturn(mockRoot);
      when(mockRoot.exists()).thenReturn(true);
      when(mockRoot.list(".json")).thenReturn(new FileHandle[0]);

      var result = Persistence.create("testProfile", 2);
      Profile profile = result.getKey();

      assertNotNull(profile);
      assertEquals("testProfile", profile.getName());

      mockFileLoader.verify(
          () ->
              FileLoader.writeClass(
                  any(Profile.class),
                  matches(".*testProfile\\$\\d+\\$2\\.json"),
                  eq(FileLoader.Location.EXTERNAL)),
          atLeastOnce());
    }
  }

  @Test
  void testCreateWithNullName() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      mockFileLoader
          .when(
              () ->
                  FileLoader.writeClass(
                      any(Profile.class), anyString(), eq(FileLoader.Location.EXTERNAL)))
          .thenAnswer(inv -> null);
      Files mockFiles = mock(Files.class);
      FileHandle mockRoot = mock(FileHandle.class);
      Gdx.files = mockFiles;
      when(mockFiles.external(anyString())).thenReturn(mockRoot);
      when(mockRoot.exists()).thenReturn(true);
      when(mockRoot.list(".json")).thenReturn(new FileHandle[0]);
      var result = Persistence.create(null, 1);
      Profile profile = result.getKey();

      assertNotNull(profile);
      assertNotNull(profile.getName()); // Should have default name

      mockFileLoader.verify(
          () ->
              FileLoader.writeClass(
                  any(Profile.class),
                  matches(".*\\$\\d+\\$1\\.json"),
                  eq(FileLoader.Location.EXTERNAL)),
          atLeastOnce());
    }
  }

  @Test
  void testSaveWithProfile() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      mockFileLoader
          .when(
              () ->
                  FileLoader.writeClass(
                      any(Profile.class), anyString(), eq(FileLoader.Location.EXTERNAL)))
          .thenAnswer(inv -> null);
      Files mockFiles = mock(Files.class);
      FileHandle mockRoot = mock(FileHandle.class);
      Gdx.files = mockFiles;
      when(mockFiles.external(anyString())).thenReturn(mockRoot);
      when(mockRoot.exists()).thenReturn(true);
      when(mockRoot.list(".json")).thenReturn(new FileHandle[0]);
      Profile testProfile = new Profile();
      Persistence.save(1, testProfile);

      mockFileLoader.verify(
          () ->
              FileLoader.writeClass(
                  any(Profile.class),
                  matches(".*\\$\\d+\\$1\\.json"),
                  eq(FileLoader.Location.EXTERNAL)),
          atLeastOnce());
    }
  }

  @Test
  void testSaveInvalidSlot() {
    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      Profile testProfile = new Profile();

      // Test invalid slots - should not call writeClass for invalid slots
      Persistence.save(0, testProfile);
      Persistence.save(4, testProfile);

      // Should not call writeClass for invalid slots
      mockFileLoader.verifyNoInteractions();
    }
  }

  @Test
  void testFetchEmptyDirectory() {
    Files mockFiles = mock(Files.class);
    FileHandle mockRootDir = mock(FileHandle.class);
    Gdx.files = mockFiles;

    when(mockFiles.external(anyString())).thenReturn(mockRootDir);
    when(mockRootDir.exists()).thenReturn(true);
    when(mockRootDir.list(".json")).thenReturn(new FileHandle[0]);

    List<Savefile> result = Persistence.fetch();

    assertEquals(3, result.size());
    assertNull(result.get(0));
    assertNull(result.get(1));
    assertNull(result.get(2));
  }

  @Test
  void testFetchWithInvalidFile() {
    Files mockFiles = mock(Files.class);
    FileHandle mockRootDir = mock(FileHandle.class);
    FileHandle mockInvalidFile = mock(FileHandle.class);
    Gdx.files = mockFiles;

    when(mockFiles.external(anyString())).thenReturn(mockRootDir);
    when(mockRootDir.exists()).thenReturn(true);
    when(mockInvalidFile.name()).thenReturn("invalid-file.txt");
    when(mockRootDir.list(".json")).thenReturn(new FileHandle[] {mockInvalidFile});

    List<Savefile> result = Persistence.fetch();

    assertEquals(3, result.size());
    assertNull(result.get(0));
    assertNull(result.get(1));
    assertNull(result.get(2));
  }

  @Test
  void testFetchWithInvalidSlot() {
    Files mockFiles = mock(Files.class);
    FileHandle mockRootDir = mock(FileHandle.class);
    FileHandle mockSaveFile = mock(FileHandle.class);
    Gdx.files = mockFiles;

    when(mockFiles.external(anyString())).thenReturn(mockRootDir);
    when(mockRootDir.exists()).thenReturn(true);
    when(mockSaveFile.name()).thenReturn("testProfile$1234567890$5.json");
    when(mockRootDir.list(".json")).thenReturn(new FileHandle[] {mockSaveFile});

    List<Savefile> result = Persistence.fetch();

    assertEquals(3, result.size());
    assertNull(result.get(0));
    assertNull(result.get(1));
    assertNull(result.get(2));
  }

  @Test
  void testFetchWithInvalidTimestamp() {
    Files mockFiles = mock(Files.class);
    FileHandle mockRootDir = mock(FileHandle.class);
    FileHandle mockSaveFile = mock(FileHandle.class);
    Gdx.files = mockFiles;

    when(mockFiles.external(anyString())).thenReturn(mockRootDir);
    when(mockRootDir.exists()).thenReturn(true);
    when(mockSaveFile.name()).thenReturn("testProfile$invalid$1.json");
    when(mockRootDir.list(".json")).thenReturn(new FileHandle[] {mockSaveFile});

    List<Savefile> result = Persistence.fetch();

    assertEquals(3, result.size());
    assertNull(result.get(0));
    assertNull(result.get(1));
    assertNull(result.get(2));
  }

  @Test
  void testFetchMultipleSaves() {
    Files mockFiles = mock(Files.class);
    FileHandle mockRootDir = mock(FileHandle.class);
    FileHandle mockSaveFile1 = mock(FileHandle.class);
    FileHandle mockSaveFile2 = mock(FileHandle.class);
    Gdx.files = mockFiles;

    when(mockFiles.external(anyString())).thenReturn(mockRootDir);
    when(mockRootDir.exists()).thenReturn(true);
    when(mockSaveFile1.name()).thenReturn("save1$1234567890$1.json");
    when(mockSaveFile2.name()).thenReturn("save2$1234567891$2.json");
    when(mockRootDir.list(".json")).thenReturn(new FileHandle[] {mockSaveFile1, mockSaveFile2});

    List<Savefile> result = Persistence.fetch();

    assertEquals(3, result.size());
    assertNotNull(result.get(0));
    assertEquals("save1", result.get(0).getName());
    assertEquals(1, result.get(0).getSlot());
    assertNotNull(result.get(1));
    assertEquals("save2", result.get(1).getName());
    assertEquals(2, result.get(1).getSlot());
    assertNull(result.get(2));
  }

  @Test
  void testSaveOverwrite() {
    Files mockFiles = mock(Files.class);
    FileHandle mockRootDir = mock(FileHandle.class);
    FileHandle mockExistingFile = mock(FileHandle.class);
    Gdx.files = mockFiles;

    when(mockFiles.external(anyString())).thenReturn(mockRootDir);
    when(mockRootDir.exists()).thenReturn(true);
    when(mockExistingFile.name()).thenReturn("existing$1234567890$1.json");
    when(mockRootDir.list(".json")).thenReturn(new FileHandle[] {mockExistingFile});

    try (MockedStatic<FileLoader> mockFileLoader = Mockito.mockStatic(FileLoader.class)) {
      mockFileLoader
          .when(
              () ->
                  FileLoader.writeClass(
                      any(Profile.class), anyString(), eq(FileLoader.Location.EXTERNAL)))
          .thenAnswer(inv -> null);

      Persistence.create("newProfile", 1);

      mockFileLoader.verify(
          () ->
              FileLoader.writeClass(
                  any(Profile.class),
                  matches(".*newProfile\\$\\d+\\$1\\.json"),
                  eq(FileLoader.Location.EXTERNAL)),
          atLeastOnce());
    }
  }
}
