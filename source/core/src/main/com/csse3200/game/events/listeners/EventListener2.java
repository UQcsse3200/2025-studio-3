package com.csse3200.game.events.listeners;

/**
 * An event listener with 2 arguments
 *
 * @param <T0> The type of the first event argument
 * @param <T1> The type of the second event argument
 */
@FunctionalInterface
public interface EventListener2<T0, T1> extends EventListener {
  void handle(T0 arg0, T1 arg1);
}
