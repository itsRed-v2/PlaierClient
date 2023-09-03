package net.itsred_v2.plaier.utils;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;

public class BlockHelper {

    private final ClientWorld world;

    public BlockHelper(ClientWorld world) {
        this.world = world;
    }

    public @Nullable BlockState getBlockState(BlockPos pos) {
        if (isInaccessible(pos)) return null;
        return world.getBlockState(pos);
    }

    public boolean isInaccessible(BlockPos pos) {
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
