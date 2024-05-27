package net.itsred_v2.plaier.ai.pathfinding;

import java.util.List;

public record PathFinderOutput(
        PathFinderExitStatus exitStatus,
        List<Node> path,
        PathFinder.PathValidator pathValidator,
        long calculationTime
) {

}
