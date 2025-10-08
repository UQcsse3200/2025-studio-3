package com.csse3200.game.input;

import static org.mockito.Mockito.*;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.extensions.GameExtension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class InputServiceTest {

  @Test
  void shouldRegisterInputHandler() {
    int keycode = 1;
    InputComponent inputComponent = spy(InputComponent.class);
    when(inputComponent.getInputPriority()).thenReturn(1);

    InputService inputService = new InputService();
    inputService.register(inputComponent);

    inputService.keyDown(keycode);
    verify(inputComponent).keyDown(keycode);
  }

  @Test
  void shouldUnregisterInputHandler() {
    int keycode = 1;
    InputComponent inputComponent = spy(InputComponent.class);
    when(inputComponent.getInputPriority()).thenReturn(1);

    InputService inputService = new InputService();
    inputService.register(inputComponent);
    inputService.unregister(inputComponent);

    inputService.keyDown(keycode);
    verify(inputComponent, times(0)).keyDown(keycode);
  }

  @Test
  void shouldHandleKeyInputs()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Test keyDown
    Method keyDownMethod = InputComponent.class.getDeclaredMethod("keyDown", int.class);
    Method keyDownServiceMethod = InputService.class.getDeclaredMethod("keyDown", int.class);
    shouldCallInputHandlersInPriorityOrder(keyDownMethod, keyDownServiceMethod, 1);

    // Test keyTyped
    Method keyTypedMethod = InputComponent.class.getDeclaredMethod("keyTyped", char.class);
    Method keyTypedServiceMethod = InputService.class.getDeclaredMethod("keyTyped", char.class);
    shouldCallInputHandlersInPriorityOrder(keyTypedMethod, keyTypedServiceMethod, 'a');

    // Test keyUp
    Method keyUpMethod = InputComponent.class.getDeclaredMethod("keyUp", int.class);
    Method keyUpServiceMethod = InputService.class.getDeclaredMethod("keyUp", int.class);
    shouldCallInputHandlersInPriorityOrder(keyUpMethod, keyUpServiceMethod, 1);
  }

  @Test
  void shouldHandleMouseAndTouchInputs()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Test mouseMoved
    Method mouseMovedMethod =
        InputComponent.class.getDeclaredMethod("mouseMoved", int.class, int.class);
    Method mouseMovedServiceMethod =
        InputService.class.getDeclaredMethod("mouseMoved", int.class, int.class);
    shouldCallInputHandlersInPriorityOrder(mouseMovedMethod, mouseMovedServiceMethod, 5, 6);

    // Test scrolled
    Method scrolledMethod =
        InputComponent.class.getDeclaredMethod("scrolled", float.class, float.class);
    Method scrolledServiceMethod =
        InputService.class.getDeclaredMethod("scrolled", float.class, float.class);
    shouldCallInputHandlersInPriorityOrder(scrolledMethod, scrolledServiceMethod, 5f, 6f);

    // Test touchDown
    Method touchDownMethod =
        InputComponent.class.getDeclaredMethod(
            "touchDown", int.class, int.class, int.class, int.class);
    Method touchDownServiceMethod =
        InputService.class.getDeclaredMethod(
            "touchDown", int.class, int.class, int.class, int.class);
    shouldCallInputHandlersInPriorityOrder(touchDownMethod, touchDownServiceMethod, 5, 6, 7, 8);
  }

  @Test
  void shouldHandleTouchGestures()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Test touchDragged
    Method touchDraggedMethod =
        InputComponent.class.getDeclaredMethod("touchDragged", int.class, int.class, int.class);
    Method touchDraggedServiceMethod =
        InputService.class.getDeclaredMethod("touchDragged", int.class, int.class, int.class);
    shouldCallInputHandlersInPriorityOrder(touchDraggedMethod, touchDraggedServiceMethod, 5, 6, 7);

    // Test touchUp
    Method touchUpMethod =
        InputComponent.class.getDeclaredMethod(
            "touchUp", int.class, int.class, int.class, int.class);
    Method touchUpServiceMethod =
        InputService.class.getDeclaredMethod("touchUp", int.class, int.class, int.class, int.class);
    shouldCallInputHandlersInPriorityOrder(touchUpMethod, touchUpServiceMethod, 5, 6, 7, 8);

    // Test fling
    Method flingMethod =
        InputComponent.class.getDeclaredMethod("fling", float.class, float.class, int.class);
    Method flingServiceMethod =
        InputService.class.getDeclaredMethod("fling", float.class, float.class, int.class);
    shouldCallInputHandlersInPriorityOrder(flingMethod, flingServiceMethod, 5f, 6f, 7);
  }

  @Test
  void shouldHandlePanGestures()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Test longPress
    Method longPressMethod =
        InputComponent.class.getDeclaredMethod("longPress", float.class, float.class);
    Method longPressServiceMethod =
        InputService.class.getDeclaredMethod("longPress", float.class, float.class);
    shouldCallInputHandlersInPriorityOrder(longPressMethod, longPressServiceMethod, 5f, 6f);

    // Test pan
    Method panMethod =
        InputComponent.class.getDeclaredMethod(
            "pan", float.class, float.class, float.class, float.class);
    Method panServiceMethod =
        InputService.class.getDeclaredMethod(
            "pan", float.class, float.class, float.class, float.class);
    shouldCallInputHandlersInPriorityOrder(panMethod, panServiceMethod, 5f, 6f, 7f, 8f);

    // Test panStop
    Method panStopMethod =
        InputComponent.class.getDeclaredMethod(
            "panStop", float.class, float.class, int.class, int.class);
    Method panStopServiceMethod =
        InputService.class.getDeclaredMethod(
            "panStop", float.class, float.class, int.class, int.class);
    shouldCallInputHandlersInPriorityOrder(panStopMethod, panStopServiceMethod, 5f, 6f, 7, 8);
  }

  @Test
  void shouldHandlePinch()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method =
        InputComponent.class.getDeclaredMethod(
            "pinch", Vector2.class, Vector2.class, Vector2.class, Vector2.class);
    Method serviceMethod =
        InputService.class.getDeclaredMethod(
            "pinch", Vector2.class, Vector2.class, Vector2.class, Vector2.class);
    shouldCallInputHandlersInPriorityOrder(
        method, serviceMethod, Vector2.Zero, Vector2.Zero, Vector2.Zero, Vector2.Zero);
  }

  @Test
  void shouldHandleTap()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method =
        InputComponent.class.getDeclaredMethod(
            "tap", float.class, float.class, int.class, int.class);
    Method serviceMethod =
        InputService.class.getDeclaredMethod("tap", float.class, float.class, int.class, int.class);
    shouldCallInputHandlersInPriorityOrder(method, serviceMethod, 5f, 6f, 7, 8);
  }

  @Test
  void shouldHandleTouchDownGesture()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method =
        InputComponent.class.getDeclaredMethod(
            "touchDown", float.class, float.class, int.class, int.class);
    Method serviceMethod =
        InputService.class.getDeclaredMethod(
            "touchDown", float.class, float.class, int.class, int.class);
    shouldCallInputHandlersInPriorityOrder(method, serviceMethod, 5f, 6f, 7, 8);
  }

  @Test
  void shouldHandleZoom()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = InputComponent.class.getDeclaredMethod("zoom", float.class, float.class);
    Method serviceMethod = InputService.class.getDeclaredMethod("zoom", float.class, float.class);
    shouldCallInputHandlersInPriorityOrder(method, serviceMethod, 5f, 6f);
  }

  /**
   * This is a generic method that is used to test that each of the InputService's registered input
   * handlers are called by descending priority order. As well as, that the InputService returns as
   * soon as the input is handled.
   *
   * @param method input component method
   * @param serviceMethod input service method
   * @param args method args
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  private void shouldCallInputHandlersInPriorityOrder(
      Method method, Method serviceMethod, Object... args)
      throws InvocationTargetException, IllegalAccessException {
    InputComponent inputComponent1 = spy(InputComponent.class);
    InputComponent inputComponent2 = spy(InputComponent.class);
    InputComponent inputComponent3 = spy(InputComponent.class);

    when(inputComponent1.getInputPriority()).thenReturn(100);
    when(inputComponent2.getInputPriority()).thenReturn(1);
    when(inputComponent3.getInputPriority()).thenReturn(10);

    when(method.invoke(inputComponent1, args)).thenReturn(false);
    when(method.invoke(inputComponent2, args)).thenReturn(true);
    when(method.invoke(inputComponent3, args)).thenReturn(true);

    InputService inputService = new InputService();
    inputService.register(inputComponent1);
    inputService.register(inputComponent2);
    inputService.register(inputComponent3);

    serviceMethod.invoke(inputService, args);
    method.invoke(verify(inputComponent1, times(1)), args);
    method.invoke(verify(inputComponent2, times(0)), args);
    method.invoke(verify(inputComponent3, times(1)), args);
  }
}
