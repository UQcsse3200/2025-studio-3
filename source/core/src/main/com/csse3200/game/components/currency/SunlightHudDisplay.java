package com.csse3200.game.components.currency;

import com.csse3200.game.ui.UIComponent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/** 左上角显示“阳光”数值，只读取 Currency，不做计算。 */
public class SunlightHudDisplay extends UIComponent {
  private final Currency currency;
  private Table table;
  private Label label;

  public SunlightHudDisplay(Currency currency) { this.currency = currency; }

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
    int amount = currency.getSunlight();   // 读 Currency.java 里的 getter
    label.setText("☀ " + amount);
  }

  @Override public void dispose() { table.remove(); super.dispose(); }
}
