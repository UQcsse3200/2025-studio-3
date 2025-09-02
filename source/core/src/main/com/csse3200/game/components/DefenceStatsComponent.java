package com.csse3200.game.components;

import com.csse3200.game.entities.configs.BaseDefenceConfig.State;
import com.csse3200.game.entities.configs.BaseDefenceConfig.Type;

public class DefenceStatsComponent extends CombatStatsComponent {


/**
 * Component used to store information related to combat such as health, attack, etc. Any entities
 * which engage it combat should have an instance of this class registered. This class can be
 * extended for more specific combat needs.
 */
  private Type type;
  private int range;
  private State state;
  private int attackSpeed;
  private int critChance;

  public DefenceStatsComponent(int health, int baseAttack, Type type, 
                            int range, State state, int attackSpeed, int critChance) {

    super(health, baseAttack);
    setType(type);
    setRange(range);
    setState(state);
    setAttackSpeed(attackSpeed);
    setCritChance(critChance);
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Type getType() {
      return type;
  }
  
  public void setRange(int range) {
    this.range = range;
  }

  public int getRange() {
    return range;
  }

  public void setState(State state) {
    this.state = state;
  }

  public State getState() {
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
