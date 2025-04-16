package net.itsred_v2.plaier.hacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.BeforeDebugRenderListener;
import net.itsred_v2.plaier.events.GuiRenderListener;
import net.itsred_v2.plaier.rendering.world.BoxRenderer;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;

public class PinHack extends Toggleable implements BeforeDebugRenderListener, GuiRenderListener {

    private static final int PLAYER_LINE_COLOR = ColorHelper.getArgb(0, 255, 0);
    private static final int POSITION_LINE_COLOR = ColorHelper.getArgb(0, 255, 255);
    private static final int POSITION_BOX_COLOR = ColorHelper.getArgb(64, 0, 255, 255);

    private final List<UUID> pinnedPlayers = new ArrayList<>();
    private final Map<BlockPos, BoxRenderer> pinnedPositions = new HashMap<>();

    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f modelViewMatrix = new Matrix4f();
    private Vec3d camPos = new Vec3d(0, 0, 0);

    @Override
    protected void onEnable() {
        PlaierClient.getEventManager().add(BeforeDebugRenderListener.class, this);
        PlaierClient.getEventManager().add(GuiRenderListener.class, this);
    }

    @Override
    protected void onDisable() {
        PlaierClient.getEventManager().remove(BeforeDebugRenderListener.class, this);
        PlaierClient.getEventManager().remove(GuiRenderListener.class, this);
    }

    @Override
    public void beforeDebugRender(BeforeDebugRenderEvent event) {
        // Saving rendering info during debug render phase for projection in gui render phase
        this.projectionMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
        this.modelViewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
        this.camPos = event.getContext().camera().getPos();
    }

    @Override
    public void onGuiRender(GuiRenderEvent event) {
        float tickDelta = PlaierClient.MC.getTickProgress();
        DrawContext context = event.getContext();
        VertexConsumer consumer = event.getContext().vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1.0f));
        float halfWidth = (float) context.getScaledWindowWidth() / 2f;
        float halfHeight = (float) context.getScaledWindowHeight() / 2f;
        Matrix4f identity = new Matrix4f();

        for (AbstractClientPlayerEntity player : PlaierClient.MC.getClientWorld().getPlayers()) {
            if (pinnedPlayers.contains(player.getGameProfile().getId())) {
                Vec3d deltaOffset = player.getLerpedPos(tickDelta).subtract(player.getPos());
                Vec3d targetPos = player.getBoundingBox().getCenter().add(deltaOffset);
                Vector3d targetScreenPos = projectWorldCoordinatesToScreenCoordinates(targetPos, context);
                consumer.vertex(identity, halfWidth - 0.5f, halfHeight - 0.5f, 0.0f).color(PLAYER_LINE_COLOR);
                consumer.vertex(identity, (float) targetScreenPos.x, (float) targetScreenPos.y, 0.0f).color(PLAYER_LINE_COLOR);
            }
        }

        for (BlockPos pos : pinnedPositions.keySet()) {
            Vector3d targetScreenPos = projectWorldCoordinatesToScreenCoordinates(pos.toCenterPos(), context);
            consumer.vertex(identity, halfWidth - 0.5f, halfHeight - 0.5f, 0.0f).color(POSITION_LINE_COLOR);
            consumer.vertex(identity, (float) targetScreenPos.x, (float) targetScreenPos.y, 0.0f).color(POSITION_LINE_COLOR);
        }
    }

    private Vector3d projectWorldCoordinatesToScreenCoordinates(Vec3d worldPos, DrawContext context) {
        // This is magic code. Don't understand
        Vector4f position = new Vector4f(worldPos.subtract(camPos).toVector3f(), 1);
        modelViewMatrix.transform(position);
        projectionMatrix.transform(position);
        Vector3d screenCoordinates = new Vector3d(position.x / position.w, position.y / position.w, position.z / position.w);

        // If Z > 1 (which means the object is behind the frustum), take opposite of vector
        // and make its magnitude 2, so it is longer than the screen diagonal (which is sqrt(2))
        if (screenCoordinates.z > 1) {
            double magnitude = Math.sqrt(screenCoordinates.x * screenCoordinates.x + screenCoordinates.y * screenCoordinates.y);
            screenCoordinates.x = -screenCoordinates.x * 2 / magnitude;
            screenCoordinates.y = -screenCoordinates.y * 2 / magnitude;
        }

        // Transform from [–1;1] space (y up) to [0;size] space (y down)
        double halfWidth = context.getScaledWindowWidth() / 2.0;
        double halfHeight = context.getScaledWindowHeight() / 2.0;
        screenCoordinates.x = (screenCoordinates.x + 1) * halfWidth;
        screenCoordinates.y = (-screenCoordinates.y + 1) * halfHeight;

        return screenCoordinates;
    }

    public boolean hasPlayer(UUID playerUuid) {
        return pinnedPlayers.contains(playerUuid);
    }

    public void addPlayer(UUID playerUuid) {
        if (!pinnedPlayers.contains(playerUuid))
            pinnedPlayers.add(playerUuid);

        if (!this.isEnabled())
            this.enable();
    }

    public void removePlayer(UUID playerUuid) {
        pinnedPlayers.remove(playerUuid);

        if (pinnedPlayers.isEmpty() && pinnedPositions.isEmpty())
            this.disable();
    }

    public boolean hasPosition(BlockPos pos) {
        return pinnedPositions.containsKey(pos);
    }

    public void addPosition(BlockPos pos) {
        if (!pinnedPositions.containsKey(pos)) {
            BoxRenderer boxRenderer = new BoxRenderer(POSITION_BOX_COLOR);
            boxRenderer.box = new Box(pos);
            boxRenderer.enable();
            pinnedPositions.put(pos, boxRenderer);
        }

        if (!this.isEnabled())
            this.enable();
    }

    public void removePosition(BlockPos pos) {
        if (pinnedPositions.containsKey(pos)) {
            pinnedPositions.get(pos).disable();
            pinnedPositions.remove(pos);
        }

        if (pinnedPlayers.isEmpty() && pinnedPositions.isEmpty())
            this.disable();
    }

}
