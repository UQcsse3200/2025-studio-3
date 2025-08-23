package com.csse3200.game.profile;

// import java.util.HashSet;
// import java.util.Set;

/**
 * Represents a user profile in the game. Allows customization of player
 * attributes and tracking of progress.
 * 
 * For example, to add money to the player, we could potentially call:
 * profile.wallet().addMoney(amount);
 *
 * Or, to register an event that triggers a change in stats, we could
 * potentially call: profile.statistics().register(event);
 *
 * Or, to track overall progress through levels, we could potentially call:
 * profile.setProgress(level);
 */
public class Profile {
    private String name;
    // private Wallet wallet; # The player's wallet (incl. coins & skill points)
    // private Inventory inventory; # The player's inventory of items (not defences)
    // private Skillset skillset; # The player's skills / skill tree
    // private Achievements achievements; # The player's achievements
    // private Statistics statistics; # The player's statistics
    // private Progress progress; # The player's overall progress
    // private Arsenal arsenal; # The player's unlocked defences

    public Profile() {
        // Initialize default values for profile attributes
        this.name = "Default";
        // this.wallet = new Wallet();
        // this.inventory = new Inventory();
        // this.skillset = new Skillset();
        // this.achievements = new Achievements();
        // this.statistics = new Statistics();
        // this.progress = new Progress();
    }

    /**
     * Change the name of the profile. May be useful if we implement multiple
     * saves/profiles.
     * 
     * @return the name of the profile.
     */
    public String getName() {
        return name;
    }

    /**
     * Change the name of the profile. May be useful if we implement multiple
     * saves/profiles.
     * 
     * @param name the new name of the profile.
     */
    public void setName(String name) {
        this.name = name;
    }

    // /**
    // * Get the wallet associated with the profile.
    // *
    // * @return the wallet of the profile.
    // */
    // public Wallet wallet() {
    // return wallet;
    // }

    // /**
    // * Get the inventory associated with the profile.
    // *
    // * @return the inventory of the profile.
    // */
    // public Inventory inventory() {
    // return inventory;
    // }

    // /**
    // * Get the skillset associated with the profile.
    // *
    // * @return the skillset of the profile.
    // */
    // public Skillset skillset() {
    // return skillset;
    // }

    // /**
    // * Get the achievements associated with the profile.
    // *
    // * @return the achievements of the profile.
    // */
    // public Achievements achievements() {
    // return achievements;
    // }

    // /**
    // * Get the statistics associated with the profile.
    // *
    // * @return the statistics of the profile.
    // */
    // public Statistics statistics() {
    // return statistics;
    // }

    // private class Progress {
    // private Set<String> completedLevels = new HashSet<>();
    // private int currentLevel = 1;

    // public void completeLevel(String levelId) {
    // completedLevels.add(levelId);
    // }

    // public boolean isLevelComplete(String levelId) {
    // return completedLevels.contains(levelId);
    // }

    // public int getCompletedCount() {
    // return completedLevels.size();
    // }

    // public void setCurrentLevel(int level) {
    // this.currentLevel = level;
    // }

    // public int getCurrentLevel() {
    // return currentLevel;
    // }

    // public Set<String> getCompletedLevels() {
    // return new HashSet<>(completedLevels);
    // }
    // }
}
