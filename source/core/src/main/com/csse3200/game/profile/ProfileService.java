package com.csse3200.game.profile;

import java.io.File;

import com.csse3200.game.files.FileLoader;

/**
 * Service class for managing user profiles.
 * 
 * This has somewhat been modelled off the UserSettings file.
 * Note: At some point this could be joined or integrated with settings, as
 * settings are typically are profile-based thing.
 * 
 * To initialise this service, load() should be called from the GdxGame class.
 */
public class ProfileService {
    private static Profile profile;
    private static final String ROOT_DIR = "CSSE3200Game";
    private static final String SAVE_FILE = "savefile.json";

    private ProfileService() {
        throw new IllegalStateException("Instantiating static util class");
    }

    /**
     * Load the user profile. For the purposes of sprint one, there is only one
     * profile. Later, this could be extended to allow multiple profiles.
     * 
     * Loads from the savefile, or returns a new profile if it doesn't exist.
     */
    public static void load() {
        // Load the savefile, or return a new profile if it doesn't exist.
        String path = ROOT_DIR + File.separator + SAVE_FILE;
        Profile savedProfile = FileLoader.readClass(
                Profile.class, path, FileLoader.Location.EXTERNAL);
        if (savedProfile != null) {
            profile = savedProfile;
        } else {
            profile = new Profile();
        }
    }

    /**
     * Save the current user profile to the savefile.
     */
    public static void save() {
        String path = ROOT_DIR + File.separator + SAVE_FILE;
        FileLoader.writeClass(profile, path, FileLoader.Location.EXTERNAL);
    }

    /**
     * Get the current user profile.
     * 
     * @return the current user profile.
     */
    public static Profile get() {
        return profile;
    }
}
