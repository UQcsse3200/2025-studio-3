package com.csse3200.game.components;

import com.csse3200.game.progression.skilltree.Skill;
import com.csse3200.game.services.ServiceLocator;

/**
 * An extensions of CombatStatsComponent for defender-type entities.
 *
 * <p>This component stores additional combat-related stats beyond health and base attack, such as
 * range, attack speed, and critical hit chance.
 */
public class DefenceStatsComponent extends CombatStatsComponent {

  /** Integer identifier for the type of defender (e.g., tower, trap, etc.). */
  private int type;

  /** Maximum range (in game units) at which the defender can engage targets. */
  private int range;

  /** Current state of the defender (could represent idle, attacking, etc.). */
  private int state;

  /** Rate of attacks, typically in attacks per second or ticks per attack. */
  private int attackSpeed;

  /** Chance (percentage) of delivering a critical hit when attacking. */
  private int critChance;

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
   * @param type the type identifier of this defender
   * @param range the maximum attack range
   * @param state the current combat/behavioural state
   * @param attackSpeed the speed of attacks
   * @param critChance the critical hit chance
   */
  public DefenceStatsComponent(
      int health, int baseAttack, int type, int range, int state, int attackSpeed, int critChance) {

    // Initialises health and attack stats with consideration of skill upgrades
    super((int) Math.ceil(health * HEALTH_UPGRADE), (int) Math.ceil(baseAttack * ATTACK_UPGRADE));

    // Initialise all additional defence stats
    setType(type);
    setRange(range);
    setState(state);
    setAttackSpeed(attackSpeed);
    setCritChance(critChance);
  }

  /** Sets the type of defender. */
  public void setType(int type) {
    this.type = type;
  }

  /**
   * @return the defender type identifier
   */
  public int getType() {
    return type;
  }

  /** Sets the defender's attack range. */
  public void setRange(int range) {
    this.range = range;
  }

  /**
   * @return the defender's maximum attack range
   */
  public int getRange() {
    return range;
  }

  /** Sets the defender's current state. */
  public void setState(int state) {
    this.state = state;
  }

  /**
   * @return the defender's current state
   */
  public int getState() {
    return state;
  }

  /** Sets the defender's attack speed. */
  public void setAttackSpeed(int attackSpeed) {
    this.attackSpeed = (int) Math.ceil(attackSpeed * SPEED_UPGRADE);
  }

  /**
   * @return the defender's attack speed
   */
  public int getAttackSpeed() {
    return attackSpeed;
  }

  /** Sets the defender's critical hit chance (as a percentage). */
  public void setCritChance(int critChance) {
    this.critChance = (int) (critChance + CRIT_UPGRADE);
  }

  /**
   * @return the defender's critical hit chance (percentage)
   */
  public int getCritChance() {
    return critChance;
  }
}
