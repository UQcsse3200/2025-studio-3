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
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

public class ScrapHudDisplay extends UIComponent {
  private Table table;
  private Label amountLabel;

  @Override
  public void create() {
    super.create();

    Texture sunTex = ServiceLocator.getResourceService().getAsset("images/entities/currency/scrap_metal.png", Texture.class);
    Image sunIcon = new Image(sunTex);
    sunIcon.setSize(22f, 22f);

    Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.BLACK);
    amountLabel = new Label(String.valueOf(ServiceLocator.getCurrencyService().get()), style);
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
  public void draw(SpriteBatch batch) {
    // No drawing needed
  }

  @Override
  public void dispose() {
    if (table != null) {
      table.remove();
      table = null;
    }
    super.dispose();
  }
}
