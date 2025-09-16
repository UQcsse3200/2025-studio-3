package com.csse3200.game.components;

import com.csse3200.game.persistence.Persistence;
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
  private int baseAttack;

  /**
   * Creates a new combat stats component with the specified health and attack values.
   *
   * @param health the initial health value
   * @param baseAttack the base attack value
   */
  public CombatStatsComponent(int health, int baseAttack) {
    setHealth(health);
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
    //    logger.info(String.valueOf(this.health));

    if (entity != null) {
      if (this.health == 0) {
        // 1) Decide coin amount (don’t rely on entity.getCoins() unless you KNOW it’s set)
        int extraCoins = 3; // TODO: replace with your real drop logic

        // 2) Progression stats (HudDisplay / coins.png reads this)
        if (Persistence.profile() != null) {
          int before = Persistence.profile().wallet().getCoins();
          Persistence.profile().statistics().incrementStatistic("enemiesKilled");
          Persistence.profile().wallet().addCoins(extraCoins);
          Persistence.profile().statistics().incrementStatistic("coinsCollected", extraCoins);
          logger.info(
              "[Death] wallet: {} + {} -> {}",
              before,
              extraCoins,
              Persistence.profile().wallet().getCoins());
        } else {
          logger.warn(
              "[Death] Persistence.profile() is null; cannot update progression wallet/stats");
        }

        // 3) Gameplay currency service (SunlightHudDisplay reads this)
        if (ServiceLocator.getCurrencyService() != null) {
          ServiceLocator.getCurrencyService().add(extraCoins);
          logger.info("[Death] CurrencyService +{}", extraCoins);
        }

        // 4) Now despawn
        entity.getEvents().trigger("despawnRobot", entity);
      }
      entity.getEvents().trigger("updateHealth", this.health);
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

    if (isDead() || getHealth() < 0) {
      entity.getEvents().trigger("entityDeath");
    }
  }
}
