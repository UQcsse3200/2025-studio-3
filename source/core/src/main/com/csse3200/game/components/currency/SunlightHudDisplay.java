package com.csse3200.game.components.currency;

import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class SunlightHudDisplay extends UIComponent {
  private Table table;
  private Label label;

  public SunlightHudDisplay() {}

  @Override public void create() {
    super.create();
    table = new Table(); table.setFillParent(true); table.top().left();
    label = new Label("☀ 0", skin);
    table.add(label).padTop(10f).padLeft(10f);
    stage.addActor(table);
    refresh();
  }

  @Override public void update() { refresh(); }

  private void refresh() {
    int amount = ServiceLocator.getCurrencyService().get();
    label.setText("☀ " + amount);
  }

  @Override public void dispose() { table.remove(); super.dispose(); }

   @Override
  public void draw(SpriteBatch batch) {

  }
}
