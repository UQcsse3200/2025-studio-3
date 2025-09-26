package com.csse3200.game.components;

import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component used to store information related to combat such as health, attack, etc. Any entities
 * which engage it combat should have an instance of this class registered. This class can be
 * extended for more specific combat needs.
 */
public class CombatStatsComponent extends Component {

  private static final Logger logger = LoggerFactory.getLogger(CombatStatsComponent.class);
  private int health;
  private int maxHealth;
  private int baseAttack;

  /**
   * Creates a new combat stats component with the specified health and attack values.
   *
   * @param health the initial health value
   * @param baseAttack the base attack value
   */
  public CombatStatsComponent(int health, int baseAttack) {
    setHealth(health);
    maxHealth = this.health; // setHealth will handle processing this
    setBaseAttack(baseAttack);
  }

  /**
   * Returns true if the entity's has 0 health, otherwise false.
   *
   * @return is player dead
   */
  public Boolean isDead() {
    return health == 0;
  }

  /**
   * Returns the entity's health.
   *
   * @return entity's health
   */
  public int getHealth() {
    return health;
  }

  /**
   * Returns the entity's maximum health.
   *
   * @return entity's maximum health
   */
  public int getMaxHealth() {
    return maxHealth;
  }

  /**
   * Sets the entity's health. Health has a minimum bound of 0.
   *
   * @param health health
   */
  public void setHealth(int health) {
    if (health >= 0) {
      this.health = health;
    } else {
      this.health = 0;
    }

    if (entity != null) {
      if (this.health == 0) {
        // Add coins & update statistics
        // TODO: use config passed into the entity
        int extraCoins = 3;
        ProfileService profileService = ServiceLocator.getProfileService();
        if (profileService != null && profileService.isActive()) {
          int before = profileService.getProfile().getWallet().getCoins();
          profileService.getProfile().getStatistics().incrementStatistic("enemiesKilled");
          profileService.getProfile().getWallet().addCoins(extraCoins);
          profileService
              .getProfile()
              .getStatistics()
              .incrementStatistic("coinsCollected", extraCoins);
          logger.info(
              "[Death] wallet: {} + {} -> {}",
              before,
              extraCoins,
              profileService.getProfile().getWallet().getCoins());
        } else {
          logger.warn("[Death] ProfileService is null; cannot update progression wallet/stats");
        }

        // despawn entity
        entity.getEvents().trigger("despawnRobot", entity);
      }

      entity.getEvents().trigger("updateHealth", this.health, this.maxHealth);
    }
  }

  /**
   * Adds to the player's health. The amount added can be negative.
   *
   * @param health health to add
   */
  public void addHealth(int health) {
    setHealth(this.health + health);
  }

  /**
   * Returns the entity's base attack damage.
   *
   * @return base attack damage
   */
  public int getBaseAttack() {
    return baseAttack;
  }

  /**
   * Sets the entity's attack damage. Attack damage has a minimum bound of 0.
   *
   * @param attack Attack damage
   */
  public void setBaseAttack(int attack) {
    if (attack >= 0) {
      this.baseAttack = attack;
    } else {
      logger.error("Can not set base attack to a negative attack value");
    }
  }

  public void hit(CombatStatsComponent attacker) {
    int newHealth = getHealth() - attacker.getBaseAttack();

    setHealth(newHealth);
    handleDeath();
  }

  /** Triggers death event handlers if a hit causes an entity to die. */
  public void handleDeath() {
    boolean isDead = isDead();
    if (isDead || getHealth() < 0) {
      // checks for components unique to defenders
      if (entity.getComponent(DefenderStatsComponent.class) != null
          || entity.getComponent(GeneratorStatsComponent.class) != null) {
        entity.getEvents().trigger("defenceDeath");
        logger.info("Human has died!");
      } else {
        entity.getEvents().trigger("entityDeath");
      }
    }
  }
}
