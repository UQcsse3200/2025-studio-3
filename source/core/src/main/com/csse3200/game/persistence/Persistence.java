package com.csse3200.game.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.progression.wallet.Wallet;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence utilities for loading and saving user profiles.
 *
 * <p>Save files follow the format: {@code <profilename>$<unixtime>$<slot>.json}
 */
public class Persistence {
    private static final Logger logger = LoggerFactory.getLogger(Persistence.class);

    private static final String ROOT_DIR = "The Day We Fought Back" + File.separator + "saves";
    private static final String SAVE_FILE_PATTERN = "^(.+?)\\$(\\d{10,13})(?:\\$(\\d+))?\\.json$";
    private static final String FILE_EXTENSION = ".json";

    static final class ProfileSnapshot {
        private String name;
        private String currentLevel;
        private Wallet wallet;
        private Inventory inventory;
        private SkillSet skillset;
        private Statistics statistics;
        private Arsenal arsenal;
        private float worldMapX;
        private float worldMapY;
        private int worldMapZoomIdx;

        // getters
        public String getName() { return name; }
        public String getCurrentLevel() { return currentLevel; }
        public Wallet getWallet() { return wallet; }
        public Inventory getInventory() { return inventory; }
        public SkillSet getSkillset() { return skillset; }
        public Statistics getStatistics() { return statistics; }
        public Arsenal getArsenal() { return arsenal; }
        public float getWorldMapX() { return worldMapX; }
        public float getWorldMapY() { return worldMapY; }
        public int getWorldMapZoomIdx() { return worldMapZoomIdx; }

        // setters
        public void setName(String v) { this.name = v; }
        public void setCurrentLevel(String v) { this.currentLevel = v; }
        public void setWallet(Wallet v) { this.wallet = v; }
        public void setInventory(Inventory v) { this.inventory = v; }
        public void setSkillset(SkillSet v) { this.skillset = v; }
        public void setStatistics(Statistics v) { this.statistics = v; }
        public void setArsenal(Arsenal v) { this.arsenal = v; }
        public void setWorldMapX(float v) { this.worldMapX = v; }
        public void setWorldMapY(float v) { this.worldMapY = v; }
        public void setWorldMapZoomIdx(int v) { this.worldMapZoomIdx = v; }
    }


    /** Prevent instantiation of this static utility class. */
    private Persistence() {
        throw new IllegalStateException("Instantiating static util class");
    }

    /** Builds the absolute external path for the given savefile. */
    private static String getPath(Savefile save) {
        return ROOT_DIR + File.separator + save.toString() + FILE_EXTENSION;
    }

    /**
     * Loads a profile from a savefile.
     *
     * <p>Order of attempts:
     * <ol>
     *   <li>Try reading the JSON directly into {@link Profile} (new snapshot JSON maps to the same field names).
     *   <li>If that fails, read {@link ProfileSnapshot} and reconstruct a {@link Profile}.
     * </ol>
     *
     * @param save Savefile descriptor
     * @return Pair of (Profile, slot)
     */
    public static Pair<Profile, Integer> load(Savefile save) {
        String path = getPath(save);

        // 1) Prefer reading JSON directly into Profile (field names match the snapshot keys).
        Profile asProfile = FileLoader.readClass(Profile.class, path, FileLoader.Location.EXTERNAL);
        if (asProfile != null) {
            // Backward compatibility: ensure currentLevel has a default if missing in old saves.
            if (asProfile.getCurrentLevel() == null || asProfile.getCurrentLevel().isEmpty()) {
                asProfile.setCurrentLevel("levelOne");
            }
            return new Pair<>(asProfile, save.getSlot());
        }

        // 2) Fallback: read snapshot and reconstruct minimal Profile.
        ProfileSnapshot dto = FileLoader.readClass(ProfileSnapshot.class, path, FileLoader.Location.EXTERNAL);
        if (dto != null) {
            Profile p = new Profile();
            if (dto.getName() != null) p.setName(dto.getName());
            p.setCurrentLevel(
                    (dto.getCurrentLevel() == null || dto.getCurrentLevel().isEmpty())
                            ? "levelOne" : dto.getCurrentLevel());
            p.setWorldMapX(dto.getWorldMapX());
            p.setWorldMapY(dto.getWorldMapY());
            p.setWorldMapZoomIdx(dto.getWorldMapZoomIdx());
            return new Pair<>(p, save.getSlot());
        }


        throw new IllegalStateException("Failed to load profile, creating new one.");
    }

    /**
     * Creates a new user profile and immediately persists it to the given slot.
     *
     * @param profileName Optional profile name (null = use default)
     * @param slot Slot number (1-3)
     * @return Pair of (new Profile, slot)
     */
    public static Pair<Profile, Integer> create(String profileName, int slot) {
        Profile profile = new Profile();
        if (profileName != null) {
            profile.setName(profileName);
        }
        save(slot, profile); // Ensure currentLevel is written at creation time
        return new Pair<>(profile, slot);
    }

    /** Ensures the save directory exists. */
    private static void ensureDirectoryExists() {
        FileHandle dir = Gdx.files.external(ROOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            logger.info("Created save directory: {}", ROOT_DIR);
        }
    }

    /**
     * Scans the save directory and returns the latest save per slot (1..3).
     *
     * @return List of 3 entries (null = empty slot)
     */
    public static List<Savefile> fetch() {
        List<Savefile> saves = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            saves.add(null);
        }

        ensureDirectoryExists();
        FileHandle rootDir = Gdx.files.external(ROOT_DIR);
        FileHandle[] files = rootDir.list(FILE_EXTENSION);
        if (files.length == 0) {
            return saves;
        }

        for (FileHandle file : files) {
            Savefile savefile = parseSavefile(file);
            if (savefile != null) {
                saves.set(savefile.getSlot() - 1, savefile);
            }
        }

        return saves;
    }

    /**
     * Parses a savefile descriptor from a filename.
     *
     * @param file File handle to parse
     * @return Savefile or null if filename is invalid
     */
    private static Savefile parseSavefile(FileHandle file) {
        Pattern filePattern = Pattern.compile(SAVE_FILE_PATTERN);
        Matcher matcher = filePattern.matcher(file.name());
        if (!matcher.matches()) {
            logger.error("Failed to parse savefile");
            return null;
        }

        String profileName = matcher.group(1);
        String timestampStr = matcher.group(2);
        String slotStr = matcher.group(3);
        long timestamp;
        int slot;

        try {
            timestamp = Long.parseLong(timestampStr);
            slot = Integer.parseInt(slotStr);
            if (slot > 3 || slot < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            logger.error("Failed to parse savefile");
            return null;
        }

        return new Savefile(profileName, timestamp, slot);
    }

    /**
     * Saves the given profile to the specified slot.
     *
     * <p>This writes a {@link ProfileSnapshot} to guarantee 'currentLevel' is persisted.
     *
     * @param slot Slot number (1-3)
     * @param profile Profile to persist
     */
    public static void save(int slot, Profile profile) {
        if (slot > 3 || slot < 1) {
            logger.error("Invalid slot: {}", slot);
            return;
        }

        // Remove the existing save in this slot (if any)
        List<Savefile> saves = fetch();
        try {
            if (saves.get(slot - 1) != null) {
                delete(saves.get(slot - 1));
            }
        } catch (IndexOutOfBoundsException ignored) {
            // Slot is empty; nothing to delete
        }
        // Compose a new filename (includes slot)
        String filename =
                profile.getName() + "$" + System.currentTimeMillis() + "$" + slot + FILE_EXTENSION;
        String path = ROOT_DIR + File.separator + filename;

        // Build the snapshot DTO
        ProfileSnapshot dto = new ProfileSnapshot();
        dto.setName(profile.getName());
        dto.setCurrentLevel(profile.getCurrentLevel());
        dto.setWallet(profile.getWallet());
        dto.setInventory(profile.getInventory());
        dto.setSkillset(profile.getSkillset());
        dto.setStatistics(profile.getStatistics());
        dto.setArsenal(profile.getArsenal());
        dto.setWorldMapX(profile.getWorldMapX());
        dto.setWorldMapY(profile.getWorldMapY());
        dto.setWorldMapZoomIdx(profile.getWorldMapZoomIdx());
        FileLoader.writeClass(dto, path, FileLoader.Location.EXTERNAL);

        // Write the snapshot JSON
        FileLoader.writeClass(dto, path, FileLoader.Location.EXTERNAL);
        logger.info("Saved profile to slot {}: {}", slot, filename);
    }

    /**
     * Deletes the given savefile from disk.
     *
     * @param save Savefile descriptor
     */
    private static void delete(Savefile save) {
        String path = getPath(save);
        FileHandle file = Gdx.files.external(path);
        if (file.exists()) {
            boolean success = file.delete();
            if (success) {
                logger.info("Deleted savefile: {}", path);
            } else {
                logger.error("Failed to delete savefile: {}", path);
            }
        } else {
            logger.warn("Savefile does not exist: {}", path);
        }
    }
}
