package com.csse3200.game.components.currency;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.services.CurrencyService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

public class SunlightHudDisplay extends UIComponent {

  private transient CurrencyService currencyService;
  private transient ResourceService resources;

  private Table table;
  private Label amountLabel;

  @Override
  public void create() {
    super.create();

    ResourceService resources = ServiceLocator.getResourceService();
    CurrencyService currencyService = ServiceLocator.getCurrencyService();

    Texture sunTex = resources.getAsset("images/scrap_metal.png", Texture.class);
    Image sunIcon = new Image(sunTex);
    sunIcon.setSize(22f, 22f);

    Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.BLACK);
    amountLabel = new Label(String.valueOf(currencyService.get()), style);
    amountLabel.setFontScale(3f);

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

  @Override
  public void draw(SpriteBatch batch) {}

  @Override
  public void dispose() {
    if (table != null) {
      table.remove();
      table = null;
    }
    super.dispose();
  }

  private int safeGetSun() {
    if (currencyService == null) {
      try {
        currencyService = ServiceLocator.getCurrencyService();
      } catch (Exception ignored) {
      }
    }

    return currencyService != null ? currencyService.get() : 0;
  }
}
