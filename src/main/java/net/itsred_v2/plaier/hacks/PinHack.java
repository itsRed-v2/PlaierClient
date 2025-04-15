package net.itsred_v2.plaier.hacks;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.BeforeDebugRenderListener;
import net.itsred_v2.plaier.events.GuiRenderListener;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;

public class PinHack extends Toggleable implements BeforeDebugRenderListener, GuiRenderListener {

    private static final RenderLayer.MultiPhase DEBUG_LINES = RenderLayer.of(
            "debug_lines",
            VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.DEBUG_LINES,
            1536,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderPhase.COLOR_PROGRAM)
                    .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(1.0)))
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .build(false)
    );

    private static final int LINE_COLOR = ColorHelper.Argb.getArgb(0, 255, 0);

    private final List<UUID> pinnedPlayers = new ArrayList<>();

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
        float tickDelta = PlaierClient.MC.getRenderTickCounter().getTickDelta(false);

        for (AbstractClientPlayerEntity player : PlaierClient.MC.getClientWorld().getPlayers()) {
            if (pinnedPlayers.contains(player.getGameProfile().getId())) {
                DrawContext context = event.getContext();

                Vec3d deltaOffset = player.getLerpedPos(tickDelta).subtract(player.getPos());
                Vec3d targetPos = player.getBoundingBox().getCenter().add(deltaOffset);

                Vector3d targetScreenPos = projectWorldCoordinatesToScreenCoordinates(targetPos, context);

                VertexConsumerProvider.Immediate vertexConsumerProvider = event.getContext().getVertexConsumers();
                VertexConsumer consumer = vertexConsumerProvider.getBuffer(DEBUG_LINES);

                float halfWidth = (float) context.getScaledWindowWidth() / 2f;
                float halfHeight = (float) context.getScaledWindowHeight() / 2f;

                Matrix4f identity = new Matrix4f();
                consumer.vertex(identity, halfWidth - 0.5f, halfHeight - 0.5f, 0.0f).color(LINE_COLOR);
                consumer.vertex(identity, (float) targetScreenPos.x, (float) targetScreenPos.y, 0.0f).color(LINE_COLOR);
            }
        }
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
        
        if (pinnedPlayers.isEmpty())
            this.disable();
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

}
