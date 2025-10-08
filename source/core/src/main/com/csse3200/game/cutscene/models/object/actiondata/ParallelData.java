package com.csse3200.game.cutscene.models.object.actiondata;

import java.util.List;

/**
 * Stores the data for the parallel event (runs actions at the same time)
 *
 * @param actions The actions that will run in parallel
 * @param await Wait for all actions to complete before continuing
 */
public record ParallelData(List<ActionData> actions, boolean await) implements ActionData {}
