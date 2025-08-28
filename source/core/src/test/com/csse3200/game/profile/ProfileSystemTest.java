package com.csse3200.game.profile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import com.csse3200.game.extensions.GameExtension;

@ExtendWith(GameExtension.class)
class ProfileSystemTest {
    @BeforeEach
    void setUp() {
        ProfileService.load();
    }

    @Test
    void testLoadProfile() {
        Profile profile = ProfileService.get();
        assertNotNull(profile);
    }

    @Test
    void testSaveProfile() {
        ProfileService.get().setName("Testing Profile");
        ProfileService.save();
        ProfileService.load();
        assertEquals("Testing Profile", ProfileService.get().getName());
    }
}
