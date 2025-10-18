package com.csse3200.game.components.currency;

import com.badlogic.gdx.graphics.Texture;
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
    float width = stage.getViewport().getWorldWidth();
    Texture sunTex =
        ServiceLocator.getResourceService()
            .getAsset("images/entities/currency/scrap_metal.png", Texture.class);
    Image sunIcon = new Image(sunTex);

    amountLabel = ui.heading(String.valueOf(ServiceLocator.getCurrencyService().get()));

    table = new Table();
    table.setFillParent(true);
    table.top().left().padTop(100f).padLeft(0.015f * width);
    table.add(sunIcon).size(40f, 40f).padRight(8f);
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
  public void dispose() {
    if (table != null) {
      table.remove();
      table = null;
    }
    super.dispose();
  }
}
