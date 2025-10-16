package com.csse3200.game.components.credits;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.data.credits.CreditsData;
import com.csse3200.game.data.credits.Entry;
import com.csse3200.game.data.credits.Section;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

import java.util.List;

public class CreditsDisplay extends UIComponent {
  private static final List<Section> sections = CreditsData.SECTIONS;
  private final GdxGame game;

  private Stack layers;
  private StarField starField;
  private ScrollPane scrollPane;
  private Table root;

  private float speed = 60f;

  private static TextureRegionDrawable colorTexture(Color color) {
    Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGB565);
    bg.setColor(color);
    bg.fill();
    return new TextureRegionDrawable(new TextureRegion(new Texture(bg)));
  }

  public CreditsDisplay(Stage stage, GdxGame game) {
    super.create();
    this.game = game;

    layers = new Stack();
    layers.setFillParent(true);
    stage.addActor(layers);

    Image background = new Image(colorTexture(new Color(14f/255, 10f/255, 20f/255, 1f)));
    background.setScaling(Scaling.fill);
    background.setAlign(Align.center);
    background.setFillParent(true);
    layers.add(background);

    starField = new StarField();
    layers.addActor(starField);

    root = new Table();

    buildContent();

    scrollPane = new ScrollPane(root);
    scrollPane.setScrollingDisabled(true, false);
    scrollPane.setFadeScrollBars(false);
    scrollPane.setSmoothScrolling(true);
    scrollPane.setOverscroll(false, false);

    scrollPane.setFillParent(true);

    layers.add(scrollPane);

    stage.act(0f);
    scrollPane.layout();
    scrollPane.setScrollPercentY(0f);
  }

  private void buildContent() {
    root.top();
    root.defaults().pad(10);

    // Title Card
    Image titleCard = new Image(ServiceLocator.getResourceService().getAsset("images/backgrounds/bg-text.png", Texture.class));
    titleCard.setScaling(Scaling.fillX);
    titleCard.setAlign(Align.center);
    root.add(titleCard).width(Value.percentWidth(0.4f, root)).padTop(200f * ui.getUIScale()).row();

    for (Section section : sections) {
      Table sectionTable = new Table();
      sectionTable.defaults();

      root.add(ui.title(section.title())).padTop(50f).row();

      for (Entry entry : section.names()) {
        Label firstName = ui.text(entry.first());
        Label lastName = ui.text(entry.last());

        firstName.setAlignment(Align.right);
        lastName.setAlignment(Align.left);

        sectionTable.add(firstName).fillX().uniformX().padRight(10f);
        sectionTable.add(lastName).fillX().uniformX().padLeft(10f).row();
      }

      root.add(sectionTable).row();
    }

    // add a thank you for playing section that fills the whole screen
    root.add(ui.title("Thank you for playing!")).padTop(510f * ui.getUIScale()).padBottom(510f * ui.getUIScale()).row();
  }

  @Override
  public void update() {
    float delta = ServiceLocator.getTimeSource().getDeltaTime();

    float y = scrollPane.getScrollY();
    float maxY = scrollPane.getMaxY();

    if (maxY > 0f) {
      y = Math.min(y + speed * delta, maxY);
      scrollPane.setScrollY(y);
      scrollPane.updateVisualScroll();
    }

    if (y >= maxY - 1f) {
      game.setScreen(GdxGame.ScreenType.MAIN_MENU);
    }
  }

  public void setSpeed(float speed) {
    this.speed = speed;
    this.starField.setDriftY(speed);
  }
}
