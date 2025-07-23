package com.deco2800.game.ai.tasks;

/**
 * A priority task is a task that also has a priority. This is required for an AI task component to
 * decide which task to run.
 */
public interface PriorityTask extends Task {
  int getPriority();
}
