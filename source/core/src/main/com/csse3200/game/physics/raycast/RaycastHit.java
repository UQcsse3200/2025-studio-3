package com.csse3200.game.physics.raycast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

/** Stores information about a raycast hit. */
public class RaycastHit {
  /** Fixture which was hit. */
  private Fixture fixture;

  /** Point at which the raycast hit the fixture. */
  private Vector2 point;

  /** the normal vector of the collider surface at the hit point. */
  private Vector2 normal;

  public Fixture getFixture() {
    return fixture;
  }

  public Vector2 getPoint() {
    return point;
  }
  
  public Vector2 getNormal() {
    return normal;
  }

  public void setFixture(Fixture fixture) {
    this.fixture = fixture;
  }

  public void setPoint(Vector2 point) {
    this.point = point;
  }
  
  public void setNormal(Vector2 normal) {
    this.normal = normal;
  }
}
