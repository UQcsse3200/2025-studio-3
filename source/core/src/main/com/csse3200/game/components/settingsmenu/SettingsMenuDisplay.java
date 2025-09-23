package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Settings menu display and logic.
 */
public class SettingsMenuDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(SettingsMenuDisplay.class);
  private static final String PERCENTAGE_FORMAT = "%.0f%%";
  private final GdxGame game;

  private Table rootTable;
  private Table currentMenu;
  
  // Display Settings Components
  private SelectBox<String> displayModeSelect;
  private TextField fpsText;
  private CheckBox vsyncCheck;
  
  // Game Settings Components
  private SelectBox<String> difficultySelect;
  
  // Audio Settings Components
  private Slider masterVolumeSlider;
  private Slider musicVolumeSlider;
  private Slider soundVolumeSlider;
  private Slider voiceVolumeSlider;

  public SettingsMenuDisplay(GdxGame game) {
    super();
    this.game = game;
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);

    // Add top menu row (title, exit, apply)
    rootTable.add(makeMenuBtns()).expandX().fillX().top();

    // Next row: settings table
    rootTable.row().padTop(30f);
    showMainMenu();

    stage.addActor(rootTable);
  }

  private void showMainMenu() {
    if (currentMenu != null) {
      rootTable.removeActor(currentMenu);
    }
    
    currentMenu = new Table();
    
    // Create main menu buttons
    TextButton displayBtn = ButtonFactory.createButton("Display Settings");
    TextButton gameBtn = ButtonFactory.createButton("Game Settings");
    TextButton audioBtn = ButtonFactory.createButton("Audio Settings");
    
    displayBtn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent changeEvent, Actor actor) {
        showDisplaySettings();
      }
    });
    
    gameBtn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent changeEvent, Actor actor) {
        showGameSettings();
      }
    });
    
    audioBtn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent changeEvent, Actor actor) {
        showAudioSettings();
      }
    });
    
    currentMenu.add(displayBtn).size(200f, 50f).padBottom(20f);
    currentMenu.row();
    currentMenu.add(gameBtn).size(200f, 50f).padBottom(20f);
    currentMenu.row();
    currentMenu.add(audioBtn).size(200f, 50f);
    
    rootTable.add(currentMenu).expandX().expandY();
  }
  
  private void showDisplaySettings() {
    if (currentMenu != null) {
      rootTable.removeActor(currentMenu);
    }
    
    currentMenu = new Table();
    
    // Get current settings
    Settings settings = new Settings();
    
    // Create components
    Label displayModeLabel = new Label("Display Mode:", skin);
    displayModeSelect = new SelectBox<>(skin);
    displayModeSelect.setItems("Windowed", "Fullscreen", "Borderless");
    displayModeSelect.setSelected(settings.getCurrentMode().toString());
    whiten(displayModeLabel);
    
    Label resolutionLabel = new Label("Resolution:", skin);
    SelectBox<String> resolutionSelect = new SelectBox<>(skin);
    resolutionSelect.setItems("1920x1080", "1600x900", "1366x768", "1280x720");
    whiten(resolutionLabel);
    
    Label fpsLabel = new Label("Max FPS:", skin);
    fpsText = new TextField(Integer.toString(settings.getFps()), skin);
    whiten(fpsLabel);
    
    Label uiScaleLabel = new Label("UI Scale:", skin);
    SelectBox<String> uiScaleSelect = new SelectBox<>(skin);
    uiScaleSelect.setItems("Small", "Medium", "Large");
    uiScaleSelect.setSelected(settings.getCurrentUIScale().toString());
    whiten(uiScaleLabel);
    
    Label qualityLabel = new Label("Quality:", skin);
    SelectBox<String> qualitySelect = new SelectBox<>(skin);
    qualitySelect.setItems("Low", "High");
    qualitySelect.setSelected(settings.getQuality().toString());
    whiten(qualityLabel);
    
    Label vsyncLabel = new Label("VSync:", skin);
    vsyncCheck = new CheckBox("", skin);
    vsyncCheck.setChecked(settings.isVsync());
    whiten(vsyncLabel);
    
    // Back button
    TextButton backBtn = ButtonFactory.createButton("Back");
    backBtn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent changeEvent, Actor actor) {
        showMainMenu();
      }
    });
    
    // Layout
    currentMenu.add(displayModeLabel).right().padRight(15f);
    currentMenu.add(displayModeSelect).left().width(150f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(resolutionLabel).right().padRight(15f);
    currentMenu.add(resolutionSelect).left().width(150f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(fpsLabel).right().padRight(15f);
    currentMenu.add(fpsText).left().width(100f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(uiScaleLabel).right().padRight(15f);
    currentMenu.add(uiScaleSelect).left().width(150f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(qualityLabel).right().padRight(15f);
    currentMenu.add(qualitySelect).left().width(150f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(vsyncLabel).right().padRight(15f);
    currentMenu.add(vsyncCheck).left();
    currentMenu.row().padTop(20f);
    
    currentMenu.add(backBtn).size(100f, 40f);
    
    rootTable.add(currentMenu).expandX().expandY();
  }
  
  private void showGameSettings() {
    if (currentMenu != null) {
      rootTable.removeActor(currentMenu);
    }
    
    currentMenu = new Table();
    
    // Get current settings
    Settings settings = new Settings();
    
    // Create components
    Label pauseLabel = new Label("Pause Key:", skin);
    TextField pauseKeyText = new TextField(Input.Keys.toString(settings.getPauseButton()), skin);
    whiten(pauseLabel);
    
    Label skipLabel = new Label("Skip Key:", skin);
    TextField skipKeyText = new TextField(Input.Keys.toString(settings.getSkipButton()), skin);
    whiten(skipLabel);
    
    Label interactionLabel = new Label("Interaction Key:", skin);
    TextField interactionKeyText = new TextField(Input.Keys.toString(settings.getInteractionButton()), skin);
    whiten(interactionLabel);
    
    Label upLabel = new Label("Up Key:", skin);
    TextField upKeyText = new TextField(Input.Keys.toString(settings.getUpButton()), skin);
    whiten(upLabel);
    
    Label downLabel = new Label("Down Key:", skin);
    TextField downKeyText = new TextField(Input.Keys.toString(settings.getDownButton()), skin);
    whiten(downLabel);
    
    Label leftLabel = new Label("Left Key:", skin);
    TextField leftKeyText = new TextField(Input.Keys.toString(settings.getLeftButton()), skin);
    whiten(leftLabel);
    
    Label rightLabel = new Label("Right Key:", skin);
    TextField rightKeyText = new TextField(Input.Keys.toString(settings.getRightButton()), skin);
    whiten(rightLabel);
    
    Label difficultyLabel = new Label("Difficulty:", skin);
    difficultySelect = new SelectBox<>(skin);
    difficultySelect.setItems("Easy", "Normal", "Hard");
    difficultySelect.setSelected(settings.getDifficulty().toString());
    whiten(difficultyLabel);
    
    // Back button
    TextButton backBtn = ButtonFactory.createButton("Back");
    backBtn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent changeEvent, Actor actor) {
        showMainMenu();
      }
    });
    
    // Layout
    currentMenu.add(pauseLabel).right().padRight(15f);
    currentMenu.add(pauseKeyText).left().width(100f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(skipLabel).right().padRight(15f);
    currentMenu.add(skipKeyText).left().width(100f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(interactionLabel).right().padRight(15f);
    currentMenu.add(interactionKeyText).left().width(100f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(upLabel).right().padRight(15f);
    currentMenu.add(upKeyText).left().width(100f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(downLabel).right().padRight(15f);
    currentMenu.add(downKeyText).left().width(100f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(leftLabel).right().padRight(15f);
    currentMenu.add(leftKeyText).left().width(100f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(rightLabel).right().padRight(15f);
    currentMenu.add(rightKeyText).left().width(100f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(difficultyLabel).right().padRight(15f);
    currentMenu.add(difficultySelect).left().width(150f);
    currentMenu.row().padTop(20f);
    
    currentMenu.add(backBtn).size(100f, 40f);
    
    rootTable.add(currentMenu).expandX().expandY();
  }
  
  private void showAudioSettings() {
    if (currentMenu != null) {
      rootTable.removeActor(currentMenu);
    }
    
    currentMenu = new Table();
    
    // Get current settings
    Settings settings = new Settings();
    
    // Create components
    Label masterVolumeLabel = new Label("Master Volume:", skin);
    masterVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
    masterVolumeSlider.setValue(settings.getMasterVolume());
    final Label masterVolumeValueLabel = new Label(String.format(PERCENTAGE_FORMAT, settings.getMasterVolume() * 100), skin);
    whiten(masterVolumeLabel);
    whiten(masterVolumeValueLabel);
    
    Label musicVolumeLabel = new Label("Music Volume:", skin);
    musicVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
    musicVolumeSlider.setValue(settings.getMusicVolume());
    final Label musicVolumeValueLabel = new Label(String.format(PERCENTAGE_FORMAT, settings.getMusicVolume() * 100), skin);
    whiten(musicVolumeLabel);
    whiten(musicVolumeValueLabel);
    
    Label soundVolumeLabel = new Label("Sound Volume:", skin);
    soundVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
    soundVolumeSlider.setValue(settings.getSoundVolume());
    final Label soundVolumeValueLabel = new Label(String.format(PERCENTAGE_FORMAT, settings.getSoundVolume() * 100), skin);
    whiten(soundVolumeLabel);
    whiten(soundVolumeValueLabel);
    
    Label voiceVolumeLabel = new Label("Voice Volume:", skin);
    voiceVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
    voiceVolumeSlider.setValue(settings.getVoiceVolume());
    final Label voiceVolumeValueLabel = new Label(String.format(PERCENTAGE_FORMAT, settings.getVoiceVolume() * 100), skin);
    whiten(voiceVolumeLabel);
    whiten(voiceVolumeValueLabel);
    
    // Back button
    TextButton backBtn = ButtonFactory.createButton("Back");
    backBtn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent changeEvent, Actor actor) {
        showMainMenu();
      }
    });
    
    // Slider listeners
    masterVolumeSlider.addListener((Event event) -> {
      float value = masterVolumeSlider.getValue();
      masterVolumeValueLabel.setText(String.format(PERCENTAGE_FORMAT, value * 100));
      return true;
    });
    
    musicVolumeSlider.addListener((Event event) -> {
      float value = musicVolumeSlider.getValue();
      musicVolumeValueLabel.setText(String.format(PERCENTAGE_FORMAT, value * 100));
      return true;
    });
    
    soundVolumeSlider.addListener((Event event) -> {
      float value = soundVolumeSlider.getValue();
      soundVolumeValueLabel.setText(String.format(PERCENTAGE_FORMAT, value * 100));
      return true;
    });
    
    voiceVolumeSlider.addListener((Event event) -> {
      float value = voiceVolumeSlider.getValue();
      voiceVolumeValueLabel.setText(String.format(PERCENTAGE_FORMAT, value * 100));
      return true;
    });
    
    // Layout
    currentMenu.add(masterVolumeLabel).right().padRight(15f);
    currentMenu.add(masterVolumeSlider).width(200f).left();
    currentMenu.add(masterVolumeValueLabel).left().padLeft(10f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(musicVolumeLabel).right().padRight(15f);
    currentMenu.add(musicVolumeSlider).width(200f).left();
    currentMenu.add(musicVolumeValueLabel).left().padLeft(10f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(soundVolumeLabel).right().padRight(15f);
    currentMenu.add(soundVolumeSlider).width(200f).left();
    currentMenu.add(soundVolumeValueLabel).left().padLeft(10f);
    currentMenu.row().padTop(10f);
    
    currentMenu.add(voiceVolumeLabel).right().padRight(15f);
    currentMenu.add(voiceVolumeSlider).width(200f).left();
    currentMenu.add(voiceVolumeValueLabel).left().padLeft(10f);
    currentMenu.row().padTop(20f);
    
    currentMenu.add(backBtn).size(100f, 40f);
    
    rootTable.add(currentMenu).expandX().expandY();
  }


  private Table makeMenuBtns() {
    // Exit button (from main branch)
    ImageButton exitBtn =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));
    exitBtn.setSize(60f, 60f);
    exitBtn.setPosition(
        20f, // padding from left
        stage.getHeight() - 60f - 20f // padding from top
        );
    exitBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Exit button clicked");
            exitMenu();
          }
        });
    stage.addActor(exitBtn);

    // Apply button
    TextButton applyBtn = ButtonFactory.createButton("Apply");
    applyBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Apply button clicked");
            applyChanges();
            if (!ServiceLocator.getProfileService().isActive()) {
              game.setScreen(ScreenType.MAIN_MENU);
            } else {
              game.setScreen(ScreenType.WORLD_MAP);
            }
          }
        });

    // Title
    Label title = new Label("Settings", skin, "title");

    Table table = new Table();
    table.setFillParent(true);
    table.top().padTop(10f).padLeft(10f).padRight(10f);
    table.add(title).expandX().center();

    // Apply button bottom-right
    Table bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().right().pad(20f);
    bottomRow.add(applyBtn).size(100f, 50f);
    stage.addActor(bottomRow);

    return table;
  }

  private void applyChanges() {
    // Apply display settings if they exist
    if (fpsText != null) {
      Settings settings = new Settings();
      Integer fpsVal = parseOrNull(fpsText.getText());
      if (fpsVal != null) {
        settings.setFps(fpsVal);
      }
      if (displayModeSelect != null) {
        String mode = displayModeSelect.getSelected();
        switch (mode) {
          case "Windowed":
            settings.setCurrentMode(Settings.Mode.WINDOWED);
            break;
          case "Fullscreen":
            settings.setCurrentMode(Settings.Mode.FULLSCREEN);
            break;
          case "Borderless":
            settings.setCurrentMode(Settings.Mode.BORDERLESS);
            break;
          default:
            settings.setCurrentMode(Settings.Mode.WINDOWED);
            break;
        }
      }
      if (vsyncCheck != null) {
        settings.setVsync(vsyncCheck.isChecked());
      }
    }
    
    // Apply game settings if they exist
    if (difficultySelect != null) {
      Settings settings = new Settings();
      String difficulty = difficultySelect.getSelected();
      switch (difficulty) {
        case "Easy":
          settings.setDifficulty(Settings.Difficulty.EASY);
          break;
        case "Normal":
          settings.setDifficulty(Settings.Difficulty.NORMAL);
          break;
        case "Hard":
          settings.setDifficulty(Settings.Difficulty.HARD);
          break;
        default:
          settings.setDifficulty(Settings.Difficulty.NORMAL);
          break;
      }
      // Note: Key bindings would need to be implemented with proper key input handling
    }
    
    // Apply audio settings if they exist
    if (masterVolumeSlider != null) {
      Settings settings = new Settings();
      settings.setMasterVolume(masterVolumeSlider.getValue());
      settings.setMusicVolume(musicVolumeSlider.getValue());
      settings.setSoundVolume(soundVolumeSlider.getValue());
      settings.setVoiceVolume(voiceVolumeSlider.getValue());
      // Note: Settings would need to be saved to persistent storage
    }
  }

  private void exitMenu() {
    if (!ServiceLocator.getProfileService().isActive()) {
      game.setScreen(ScreenType.MAIN_MENU);
    } else {
      game.setScreen(ScreenType.WORLD_MAP);
    }
  }

  private Integer parseOrNull(String num) {
    try {
      return Integer.parseInt(num, 10);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void update() {
    stage.act(ServiceLocator.getTimeSource().getDeltaTime());
  }

  @Override
  public void dispose() {
    rootTable.clear();
    super.dispose();
  }

  private static void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = Color.WHITE;
    label.setStyle(st);
    logger.debug("Labels are white");
  }
}
