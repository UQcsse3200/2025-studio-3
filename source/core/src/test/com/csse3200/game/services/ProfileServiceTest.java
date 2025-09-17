package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.progression.Profile;
import java.util.Arrays;
import java.util.List;
import net.dermetfan.utils.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
  private ProfileService profileService;
  private MockedStatic<Persistence> persistenceMock;

  @BeforeEach
  void setUp() {
    profileService = new ProfileService();
    persistenceMock = mockStatic(Persistence.class, withSettings().strictness(Strictness.LENIENT));
    persistenceMock
        .when(() -> Persistence.create(anyString(), anyInt()))
        .thenReturn(new Pair<>(new Profile(), 1));
    persistenceMock
        .when(() -> Persistence.load(any(Savefile.class)))
        .thenReturn(new Pair<>(new Profile(), 1));
    persistenceMock
        .when(() -> Persistence.save(anyInt(), any(Profile.class)))
        .thenAnswer(invocation -> null);
    persistenceMock
        .when(Persistence::fetch)
        .thenReturn(Arrays.asList(createMockSavefile(1), null, createMockSavefile(3)));
  }

  @AfterEach
  void tearDown() {
    if (persistenceMock != null) {
      persistenceMock.close();
    }
  }

  @Test
  void testInitialState() {
    assertNotNull(profileService.getProfile());
    assertEquals(0, profileService.getCurrentSlot());
    assertFalse(profileService.isActive());
  }

  @Test
  void testCreateProfile() {
    Profile mockProfile = new Profile();
    Pair<Profile, Integer> mockPair = new Pair<>(mockProfile, 1);
    persistenceMock.when(() -> Persistence.create("TestProfile", 1)).thenReturn(mockPair);

    profileService.createProfile("TestProfile", 1);

    assertTrue(profileService.isActive());
    assertEquals(mockProfile, profileService.getProfile());
    assertEquals(1, profileService.getCurrentSlot());

    persistenceMock.verify(() -> Persistence.create("TestProfile", 1));
  }

  @Test
  void testLoadProfile() {
    Profile mockProfile = new Profile();
    Savefile mockSavefile = mock(Savefile.class);
    when(mockSavefile.getSlot()).thenReturn(2);
    Pair<Profile, Integer> mockPair = new Pair<>(mockProfile, 2);
    persistenceMock.when(() -> Persistence.load(mockSavefile)).thenReturn(mockPair);

    profileService.loadProfile(mockSavefile);

    assertTrue(profileService.isActive());
    assertEquals(mockProfile, profileService.getProfile());
    assertEquals(2, profileService.getCurrentSlot());

    persistenceMock.verify(() -> Persistence.load(mockSavefile));
  }

  @Test
  void testSaveCurrentProfile() {
    Profile mockProfile = new Profile();
    Savefile mockSavefile = createMockSavefile(1);
    Pair<Profile, Integer> mockPair = new Pair<>(mockProfile, 1);
    persistenceMock.when(() -> Persistence.load(mockSavefile)).thenReturn(mockPair);

    profileService.loadProfile(mockSavefile);
    profileService.saveCurrentProfile();

    persistenceMock.verify(() -> Persistence.save(1, mockProfile));
  }

  @Test
  void testSaveCurrentProfileNoProfile() {
    assertThrows(IllegalStateException.class, () -> profileService.saveCurrentProfile());
  }

  @Test
  void testSaveProfileToSlot() {
    Profile mockProfile = new Profile();
    Savefile mockSavefile = createMockSavefile(1);
    Pair<Profile, Integer> mockPair = new Pair<>(mockProfile, 1);
    persistenceMock.when(() -> Persistence.load(mockSavefile)).thenReturn(mockPair);

    profileService.loadProfile(mockSavefile);
    profileService.saveProfileToSlot(3);

    assertEquals(3, profileService.getCurrentSlot());
    persistenceMock.verify(() -> Persistence.save(3, mockProfile));
  }

  @Test
  void testSaveProfileToSlotInvalidSlot() {
    Profile mockProfile = new Profile();
    Savefile mockSavefile = createMockSavefile(1);
    Pair<Profile, Integer> mockPair = new Pair<>(mockProfile, 1);
    persistenceMock.when(() -> Persistence.load(mockSavefile)).thenReturn(mockPair);
    profileService.loadProfile(mockSavefile);

    assertThrows(IllegalArgumentException.class, () -> profileService.saveProfileToSlot(0));
    assertThrows(IllegalArgumentException.class, () -> profileService.saveProfileToSlot(4));
  }

  @Test
  void testGetAllSaves() {
    List<Savefile> mockSaves =
        Arrays.asList(
            createMockSavefile(1),
            null, // Empty slot
            createMockSavefile(3));
    persistenceMock.when(Persistence::fetch).thenReturn(mockSaves);

    List<Savefile> result = profileService.getAllSaves();

    assertEquals(mockSaves, result);
  }

  @Test
  void testGetSaveFromSlot() {
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

  @Test
  void testGetSaveFromSlotInvalidSlot() {
    assertThrows(IllegalArgumentException.class, () -> profileService.getSaveFromSlot(0));
    assertThrows(IllegalArgumentException.class, () -> profileService.getSaveFromSlot(4));
  }

  @Test
  void testClear() {
    Profile mockProfile = new Profile();
    Savefile mockSavefile = createMockSavefile(1);
    Pair<Profile, Integer> mockPair = new Pair<>(mockProfile, 1);
    persistenceMock.when(() -> Persistence.load(mockSavefile)).thenReturn(mockPair);
    profileService.loadProfile(mockSavefile);

    assertTrue(profileService.isActive());

    profileService.clear();

    assertFalse(profileService.isActive());
    assertNotNull(profileService.getProfile()); // Should have a new default profile
    assertEquals(0, profileService.getCurrentSlot());
  }

  @Test
  void testIsSlotEmpty() {
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

  @Test
  void testGetUsedSlotCount() {
    List<Savefile> mockSaves =
        Arrays.asList(
            createMockSavefile(1),
            null, // Empty slot
            createMockSavefile(3));
    persistenceMock.when(Persistence::fetch).thenReturn(mockSaves);

    int count = profileService.getUsedSlotCount();

    assertEquals(2, count);
  }

  private Savefile createMockSavefile(int slot) {
    return new Savefile("testProfile", 1234567890L, slot);
  }
}
