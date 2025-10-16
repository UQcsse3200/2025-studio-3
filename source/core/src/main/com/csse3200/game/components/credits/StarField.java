package com.csse3200.game.components.credits;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.csse3200.game.data.credits.Star;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarField extends Actor {
  private final List<Star> stars = new ArrayList<>();
  private final Random random = new Random();
  private Texture starTexture;
  private float time;
  private boolean initialized = false;

  // config
  private final int STAR_COUNT = 100;

  public void setDriftY(float driftY) {
    this.driftY = driftY;
  }

  private float driftY = 60f;

  private Pixmap makeStar() {
    Pixmap pm = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
    pm.setColor(new Color(64f/255, 68f/255, 99f/255, 1f));
    pm.fillCircle(2, 2, 2);
    return pm;
  }

  public StarField() {
    Pixmap pm = makeStar();
    this.starTexture = new Texture(pm);
    pm.dispose();
  }

  private void ensureInit() {
    if (initialized) return;
    if (getWidth() <= 1f || getHeight() <= 1f) return;
    for (int i = 0; i < STAR_COUNT; i++) {
      stars.add(randomStar());
    }
    initialized = true;
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    this.time += delta;

    for (Star star : stars) {
      float speedMul = MathUtils.lerp(1.2f, 0.35f, star.getZ()); // closer are faster
      star.setY(star.getY() + speedMul * delta * driftY);

      // wrap around the edges with a new random x
      if (star.getY() > getHeight() + 2) {
        star.setY(-2);
        star.setX(random.nextInt((int) getWidth()));
      }
    }
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    ensureInit();
    if (!initialized) return;

    batch.setColor(Color.WHITE);
    for (Star star : stars) {
      batch.draw(starTexture, star.getX(), star.getY());
    }
  }

  @Override
  public void clear() {
    super.clear();
    stars.clear();
    starTexture.dispose();
  }

  private Star randomStar() {
    Star star = new Star();
    star.setX(random.nextFloat() * getWidth());
    star.setY(random.nextFloat() * getHeight());
    star.setZ(random.nextFloat()); // closeness to the "camera"
    return star;
  }
}
