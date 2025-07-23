package com.deco2800.game.entities.factories;

import com.deco2800.game.components.CameraComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.rendering.Renderer;
import com.deco2800.game.services.ServiceLocator;

public class RenderFactory {

  public static Entity createCamera() {
    return new Entity().addComponent(new CameraComponent());
  }

  public static Renderer createRenderer() {
    Entity camera = createCamera();
    ServiceLocator.getEntityService().register(camera);
    CameraComponent camComponent = camera.getComponent(CameraComponent.class);

    return new Renderer(camComponent);
  }

  private RenderFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
