package com.csse3200.game.physics.raycast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

/**
 * Cast a ray against all colliders that match the layermask. The closest hit will be stored, or
 * none if no hit occurred.
 */
public class SingleHitCallback implements RayCastCallback {
  private short layerMask = ~0;
  private RaycastHit hit;
  private boolean didHit;

  @Override
  public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
    if ((fixture.getFilterData().categoryBits & layerMask) != 0) {
      didHit = true;
      hit.setFixture(fixture);
      hit.setPoint(point);
      hit.setNormal(normal);
      return fraction; // Continue in case of closer object
    }
    return 1; // Ignore this collision, it wasn't in the layer mask.
  }

  public void setLayerMask(short layerMask) {
    this.layerMask = layerMask;
  }

  public short getLayerMask() {
    return layerMask;
  }

  public RaycastHit getHit() {
    return hit;
  }

  public boolean didHit() {
    return didHit;
  }

  public void setHit(RaycastHit hit) {
    this.hit = hit;
  }

  public void setDidHit(boolean didHit) {
    this.didHit = didHit;
  }
}
