package com.csse3200.game.components.currency;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.services.CurrencyService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;

/**
 * 左上角“阳光”HUD：太阳图标 + 数字。
 * 通过 CurrencyService 读取当前阳光数量。
 */
public class SunlightHudDisplay extends UIComponent {

  private transient CurrencyService currencyService;
  private transient ResourceService resources;

  private Table table;
  private Label amountLabel;

  @Override
public void create() {
  super.create();

  // 1) service
  ResourceService resources = ServiceLocator.getResourceService();
  CurrencyService currencyService = ServiceLocator.getCurrencyService();

  // 2) 贴图（太阳图标）
  Texture sunTex = resources.getAsset("images/normal_sunlight.png", Texture.class);
  Image sunIcon = new Image(sunTex);
  sunIcon.setSize(22f, 22f);

  // 3) 数字（不用 skin，直接用默认字体）
  Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.BLACK);
  amountLabel = new Label(String.valueOf(currencyService.get()), style);
  amountLabel.setFontScale(3f);

  // 4) 左上角布局
  table = new Table();
  table.setFillParent(true);
  table.top().left().padTop(48f).padLeft(14f);
  table.add(sunIcon).padRight(6f);
  table.add(amountLabel);

  Stage stage = ServiceLocator.getRenderService().getStage();
  stage.addActor(table);
}

    
 @Override
public void update() {
  CurrencyService cs = ServiceLocator.getCurrencyService();
  amountLabel.setText(String.valueOf(cs.get()));
}



  // Scene2D 走 Stage 渲染，这里不需要自己画
  @Override
  public void draw(SpriteBatch batch) { }

  @Override
  public void dispose() {
    if (table != null) {
      table.remove();
      table = null;
    }
    super.dispose();
  }

  /** 保险地拿当前阳光值（服务可能还没准备好时不崩） */
  private int safeGetSun() {
    if (currencyService == null) {
      try {
        currencyService = ServiceLocator.getCurrencyService();
      } catch (Exception ignored) {}
    }
    // 你们的服务里方法名若不是 getSunlight()，改成真实的方法名
   return currencyService != null ? currencyService.get() : 0;
// 或者：return currencyService != null ? currencyService.getCurrency().getSunlight() : 0;

  }
}
