package com.csse3200.game.components;

import com.csse3200.game.progression.skilltree.Skill;
import com.csse3200.game.services.ServiceLocator;

/**
 * An extensions of CombatStatsComponent for defender-type entities.
 *
 * <p>This component stores additional combat-related stats beyond health and base attack, such as
 * range, attack speed, and critical hit chance.
 */
public class DefenderStatsComponent extends CombatStatsComponent {

  /** Maximum range (in game units) at which the defender can engage targets. */
  private int range;

  /** Rate of attacks, typically in attacks per second or ticks per attack. */
  private float attackSpeed;

  /** Chance (percentage) of delivering a critical hit when attacking. */
  private float critChance;

  /** Scrap cost of the defender. */
  private int cost;

  // Initialises multiplier values to be applied to base stats from having unlocked skills
  private static final float ATTACK_UPGRADE =
      ServiceLocator.getProfileService()
          .getProfile()
          .getSkillset()
          .getUpgradeValue(Skill.StatType.ATTACK_DAMAGE);
  private static final float HEALTH_UPGRADE =
      ServiceLocator.getProfileService()
          .getProfile()
          .getSkillset()
          .getUpgradeValue(Skill.StatType.HEALTH);
  private static final float SPEED_UPGRADE =
      ServiceLocator.getProfileService()
          .getProfile()
          .getSkillset()
          .getUpgradeValue(Skill.StatType.FIRING_SPEED);
  private static final float CRIT_UPGRADE =
      ServiceLocator.getProfileService()
          .getProfile()
          .getSkillset()
          .getUpgradeValue(Skill.StatType.CRIT_CHANCE);

  /**
   * Creates a new DefenceStatsComponent with the given stats.
   *
   * @param health the maximum health of the defender
   * @param baseAttack the base attack damage
   * @param range the maximum attack range
   * @param attackSpeed the speed of attacks
   * @param critChance the critical hit chance
   * @param cost the scrap cost of the defender
   */
  public DefenderStatsComponent(
      int health, int baseAttack, int range, float attackSpeed, float critChance, int cost) {

    // Initialises health and attack stats with consideration of skill upgrades
    super((int) Math.ceil(health * HEALTH_UPGRADE), (int) Math.ceil(baseAttack * ATTACK_UPGRADE));

    // Initialise all additional defence stats
    setRange(range);
    setAttackSpeed(attackSpeed);
    setCritChance(critChance);
    setCost(cost);
  }

  /** Sets the defender's attack range. */
  public void setRange(int range) {
    if (range < 0) {
      this.range = 0;
    } else {
      this.range = range;
    }
  }

  /**
   * @return the defender's maximum attack range
   */
  public int getRange() {
    return range;
  }

  /** Sets the defender's attack speed. */
  public void setAttackSpeed(float attackSpeed) {
    if (attackSpeed < 0) {
      this.attackSpeed = 0;
    } else {
      this.attackSpeed = (float) Math.ceil(attackSpeed * SPEED_UPGRADE);
    }
  }

  /**
   * @return the defender's attack speed
   */
  public float getAttackSpeed() {
    return attackSpeed;
  }

  /** Sets the defender's critical hit chance (as a percentage). */
  public void setCritChance(float critChance) {
    this.critChance = (int) (critChance + CRIT_UPGRADE);
  }

  /**
   * @return the defender's critical hit chance (percentage)
   */
  public float getCritChance() {
    return critChance;
  }

  /**
   * Sets the scrap cost of the defender. If the provided value is negative, the cost is set to 0.
   *
   * @param cost new cost value.
   */
  public void setCost(int cost) {
    if (cost < 0) {
      this.cost = 0;
    } else {
      this.cost = cost;
    }
  }

  /**
   * Returns the scrap cost of the defender.
   *
   * @return the cost amount.
   */
  public int getCost() {
    return cost;
  }
}
