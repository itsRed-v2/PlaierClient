package net.itsred_v2.plaier.utils;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BlockHelper {

    private final WorldView world;

    public BlockHelper(WorldView world) {
        this.world = world;
    }

    public @Nullable BlockState getBlockState(BlockPos pos) {
        if (isOutOfWorld(pos)) return null;
        return world.getBlockState(pos);
    }

    public boolean isOutOfWorld(BlockPos pos) {
        return isUnloaded(pos) || isOutOfHeightLimit(pos);
    }

    public boolean isUnloaded(BlockPos pos) {
        int x = ChunkSectionPos.getSectionCoord(pos.getX());
        int z = ChunkSectionPos.getSectionCoord(pos.getZ());
        return world.getChunk(x, z, null, false) == null;
    }

    public boolean isOutOfHeightLimit(BlockPos pos) {
        return world.isOutOfHeightLimit(pos.getY());
    }

}
