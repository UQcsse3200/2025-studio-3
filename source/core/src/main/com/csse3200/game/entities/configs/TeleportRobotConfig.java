package com.csse3200.game.entities.configs;

public class TeleportRobotConfig extends BaseEnemyConfig{
    public String getName() {
        return "Teleport Robot";
    }

    public String getAtlasFile() {
        return "images/robot_placeholder.atlas";
    }

    public String getDefaultSprite() {
        return "images/default_enemy_image.png";
    }

    public int getHealth() {
        return 60;
    }

    public int getAttack() {
        return 10;
    }

    public float getMovementSpeed() {
        return 20;
    }

    public float getScale() {
        return 1f;
    }

    /**
     * Constant cooldown between teleport attempts (seconds).
     */
    public float getTeleportCooldownSeconds() {
        return 6f;
    }

    /**
     * Probability (0..1) to perform a teleport when the cooldown elapses.
     */
    public float getTeleportChance() {
        return 0.6f;
    }

    /**
     * Maximum number of teleports this enemy may perform during its lifetime.
     * Use a non-positive number (e.g., 0 or -1) to indicate 'no cap'.
     */
    public int getMaxTeleports() {
        return 3;
    }

    /**
     * Duration of invulnerability (milliseconds) immediately after teleport.
     */
    public int getInvulnerabilityMs() {
        return 200;
    }

    /**
     * Optional: animation/sprite identifiers for teleport effects.
     * Return null if not used.
     */
    public String getFxOutId() {
        return null;
    }

    public String getFxInId() {
        return null;
    }

    public String getTeleportSfxId() {
        return null;
    }
}
