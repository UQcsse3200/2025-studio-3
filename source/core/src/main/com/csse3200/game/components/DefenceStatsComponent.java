package com.csse3200.game.components;

public class DefenceStatsComponent extends CombatStatsComponent {

  /**
   * Component used to store information related to combat which are specific to defence 
   * entities. Any defence entities which engage it combat should have an instance of this class 
   * registered
   */

  // Note health and baseAttack come from super()
  private int type;
  private int range;
  private int state;
  private int attackSpeed;
  private int critChance;

  public DefenceStatsComponent(int health, int baseAttack, int type,
      int range, int state, int attackSpeed, int critChance) {

    super(health, baseAttack);
    setType(type);
    setRange(range);
    setState(state);
    setAttackSpeed(attackSpeed);
    setCritChance(critChance);
  }

  // Setters  & getters for access
  public void setType(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public void setRange(int range) {
    this.range = range;
  }

  public int getRange() {
    return range;
  }

  public void setState(int state) {
    this.state = state;
  }

  public int getState() {
    return state;
  }

  public void setAttackSpeed(int attackSpeed) {
    this.attackSpeed = attackSpeed;
  }

  public int getAttackSpeed() {
    return attackSpeed;
  }

  public void setCritChance(int critChance) {
    this.critChance = critChance;
  }

  public int getCritChance() {
    return critChance;
  }
}
