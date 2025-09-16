package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.progression.Profile;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for ProfileService functionality. */
@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
  private ProfileService profileService;

  @BeforeEach
  void setUp() {
    profileService = new ProfileService();
  }

  @Test
  void testInitialState() {
    assertNull(profileService.getCurrentProfile());
    assertEquals(0, profileService.getCurrentSlot());
    assertFalse(profileService.hasProfile());
  }

  @Test
  void testCreateProfile() {
    try (MockedStatic<Persistence> persistenceMock = mockStatic(Persistence.class)) {
      Profile mockProfile = mock(Profile.class);
      persistenceMock.when(Persistence::profile).thenReturn(mockProfile);

      profileService.createProfile("TestProfile", 1);

      assertTrue(profileService.hasProfile());
      assertEquals(mockProfile, profileService.getCurrentProfile());
      assertEquals(1, profileService.getCurrentSlot());

      persistenceMock.verify(() -> Persistence.create("TestProfile", 1));
    }
  }

  @Test
  void testCreateProfileInvalidSlot() {
    assertThrows(
        IllegalArgumentException.class, () -> profileService.createProfile("TestProfile", 0));
    assertThrows(
        IllegalArgumentException.class, () -> profileService.createProfile("TestProfile", 4));
  }

  @Test
  void testLoadProfile() {
    try (MockedStatic<Persistence> persistenceMock = mockStatic(Persistence.class)) {
      Profile mockProfile = mock(Profile.class);
      Savefile mockSavefile = mock(Savefile.class);
      when(mockSavefile.getSlot()).thenReturn(2);

      persistenceMock.when(Persistence::profile).thenReturn(mockProfile);

      profileService.loadProfile(mockSavefile);

      assertTrue(profileService.hasProfile());
      assertEquals(mockProfile, profileService.getCurrentProfile());
      assertEquals(2, profileService.getCurrentSlot());

      persistenceMock.verify(() -> Persistence.load(mockSavefile));
    }
  }

  @Test
  void testLoadProfileNullSavefile() {
    assertThrows(IllegalArgumentException.class, () -> profileService.loadProfile(null));
  }

  @Test
  void testSaveCurrentProfile() {
    try (MockedStatic<Persistence> persistenceMock = mockStatic(Persistence.class)) {
      Profile mockProfile = mock(Profile.class);
      Savefile mockSavefile = createMockSavefile(1);
      persistenceMock.when(Persistence::profile).thenReturn(mockProfile);

      profileService.loadProfile(mockSavefile);
      profileService.saveCurrentProfile();

      persistenceMock.verify(Persistence::save);
    }
  }

  @Test
  void testSaveCurrentProfileNoProfile() {
    assertThrows(IllegalStateException.class, () -> profileService.saveCurrentProfile());
  }

  @Test
  void testSaveProfileToSlot() {
    try (MockedStatic<Persistence> persistenceMock = mockStatic(Persistence.class)) {
      profileService.loadProfile(createMockSavefile(1));

      profileService.saveProfileToSlot(3);

      assertEquals(3, profileService.getCurrentSlot());
      persistenceMock.verify(() -> Persistence.save(3));
    }
  }

  @Test
  void testSaveProfileToSlotInvalidSlot() {
    profileService.loadProfile(createMockSavefile(1));

    assertThrows(IllegalArgumentException.class, () -> profileService.saveProfileToSlot(0));
    assertThrows(IllegalArgumentException.class, () -> profileService.saveProfileToSlot(4));
  }

  @Test
  void testGetAllSaves() {
    try (MockedStatic<Persistence> persistenceMock = mockStatic(Persistence.class)) {
      List<Savefile> mockSaves =
          Arrays.asList(
              createMockSavefile(1),
              null, // Empty slot
              createMockSavefile(3));
      persistenceMock.when(Persistence::fetch).thenReturn(mockSaves);

      List<Savefile> result = profileService.getAllSaves();

      assertEquals(mockSaves, result);
      persistenceMock.verify(Persistence::fetch);
    }
  }

  @Test
  void testGetSaveFromSlot() {
    try (MockedStatic<Persistence> persistenceMock = mockStatic(Persistence.class)) {
      List<Savefile> mockSaves =
          Arrays.asList(
              createMockSavefile(1),
              null, // Empty slot
              createMockSavefile(3));
      persistenceMock.when(Persistence::fetch).thenReturn(mockSaves);

      Savefile result1 = profileService.getSaveFromSlot(1);
      Savefile result2 = profileService.getSaveFromSlot(2);
      Savefile result3 = profileService.getSaveFromSlot(3);

      assertNotNull(result1);
      assertNull(result2);
      assertNotNull(result3);
    }
  }

  @Test
  void testGetSaveFromSlotInvalidSlot() {
    assertThrows(IllegalArgumentException.class, () -> profileService.getSaveFromSlot(0));
    assertThrows(IllegalArgumentException.class, () -> profileService.getSaveFromSlot(4));
  }

  @Test
  void testClearCurrentProfile() {
    profileService.loadProfile(createMockSavefile(1));
    assertTrue(profileService.hasProfile());

    profileService.clearCurrentProfile();

    assertFalse(profileService.hasProfile());
    assertNull(profileService.getCurrentProfile());
    assertEquals(0, profileService.getCurrentSlot());
  }

  @Test
  void testIsSlotEmpty() {
    try (MockedStatic<Persistence> persistenceMock = mockStatic(Persistence.class)) {
      List<Savefile> mockSaves =
          Arrays.asList(
              createMockSavefile(1),
              null, // Empty slot
              createMockSavefile(3));
      persistenceMock.when(Persistence::fetch).thenReturn(mockSaves);

      assertFalse(profileService.isSlotEmpty(1));
      assertTrue(profileService.isSlotEmpty(2));
      assertFalse(profileService.isSlotEmpty(3));
    }
  }

  @Test
  void testGetUsedSlotCount() {
    try (MockedStatic<Persistence> persistenceMock = mockStatic(Persistence.class)) {
      List<Savefile> mockSaves =
          Arrays.asList(
              createMockSavefile(1),
              null, // Empty slot
              createMockSavefile(3));
      persistenceMock.when(Persistence::fetch).thenReturn(mockSaves);

      int count = profileService.getUsedSlotCount();

      assertEquals(2, count);
    }
  }

  private Savefile createMockSavefile(int slot) {
    Savefile savefile = mock(Savefile.class);
    when(savefile.getSlot()).thenReturn(slot);
    return savefile;
  }
}
