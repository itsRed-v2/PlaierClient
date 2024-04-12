package net.itsred_v2.plaier.ai.pathfinding;

public enum PathFinderExitStatus {
    FOUND,
    STOPPED,
    INVALID_START,
    INVALID_GOAL,
    REACHED_ITERATION_LIMIT,
    TRAPPED,
    UNHANDLED_ERROR
}
