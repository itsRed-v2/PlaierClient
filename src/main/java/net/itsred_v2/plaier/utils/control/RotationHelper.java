package net.itsred_v2.plaier.utils.control;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class RotationHelper {

    private final ClientPlayerEntity player;

    public RotationHelper(ClientPlayerEntity player) {
        this.player = player;
    }

    public void facePos(Vec3d pos) {
        Rotation rotation = getRotationsToFace(pos);
        player.setYaw(rotation.yaw());
        player.setPitch(rotation.pitch());
    }

    public void facePosHorizontally(Vec3d pos) {
        player.setYaw(getYawToFace(pos));
        player.setPitch(0);
    }

    public Rotation getRotationsToFace(Vec3d pos) {
        Vec3d eyePos = player.getEyePos();
        double dx = eyePos.x - pos.x;
        double dy = eyePos.y - pos.y;
        double dz = eyePos.z - pos.z;
        double dxz = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) + 90f;
        float pitch = (float) Math.toDegrees(Math.atan2(dy, dxz));

        return new Rotation(yaw, pitch);
    }

    public float getYawToFace(Vec3d pos) {
        Vec3d eyePos = player.getEyePos();
        double dx = eyePos.x - pos.x;
        double dz = eyePos.z - pos.z;
        return (float) Math.toDegrees(Math.atan2(dz, dx)) + 90f;
    }

    public record Rotation(float yaw, float pitch) {

    }

}
