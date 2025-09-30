package com.csse3200.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.csse3200.game.GdxGame;
import com.csse3200.game.persistence.DeserializedSettings;
import com.csse3200.game.persistence.FileLoader;
import net.dermetfan.utils.Pair;
import java.io.File;
import com.badlogic.gdx.files.FileHandle;


/** This is the launch class for the desktop game. Passes control to libGDX to run GdxGame(). */
public class DesktopLauncher {
  public static void main(String[] arg) {
    FileHandle fileHandle = new FileHandle(System.getProperty("user.home") + File.separator + "The Day We Fought Back" + File.separator + "settings.json");
    DeserializedSettings deserializedSettings = FileLoader.readClass(DeserializedSettings.class, fileHandle);

    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setWindowIcon("app.png");
    config.setTitle("The Day We Fought Back");
    
    switch (deserializedSettings.getCurrentMode()) {
      case FULLSCREEN:
      config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
      break;
      case WINDOWED:
      Pair<Integer, Integer> windowedRes = deserializedSettings.getWindowedResolution();
      config.setWindowedMode(windowedRes.getKey(), windowedRes.getValue());
      break;
      case BORDERLESS:
      config.setWindowedMode(Lwjgl3ApplicationConfiguration.getDisplayMode().width, Lwjgl3ApplicationConfiguration.getDisplayMode().height);
      config.setDecorated(false);
      break;
      default:
      config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
    }

    config.setResizable(false);
    new Lwjgl3Application(new GdxGame(), config);
  }
}
