package net.itsred_v2.plaier.rendering.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.BeforeDebugRenderListener;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class PolylineRenderer extends Toggleable implements BeforeDebugRenderListener {

    public final int color;
    public List<Vec3d> vertices = new ArrayList<>();

    public PolylineRenderer(int color) {
        this.color = color;
    }

    @Override
    protected void onEnable() {
        PlaierClient.getEventManager().add(BeforeDebugRenderListener.class, this);
    }

    @Override
    protected void onDisable() {
        PlaierClient.getEventManager().remove(BeforeDebugRenderListener.class, this);
    }

    @Override
    public void beforeDebugRender(BeforeDebugRenderEvent event) {
        WorldRenderContext context = event.getContext();

        VertexConsumer vertexConsumer = Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getDebugLineStrip(1.0));
        Matrix4f matrix = Objects.requireNonNull(context.matrixStack()).peek().getPositionMatrix();
        Vec3d cam = context.camera().getPos();

        for (Vec3d vert : vertices) {
            vertexConsumer.vertex(matrix, (float) (vert.x - cam.x), (float) (vert.y - cam.y), (float) (vert.z - cam.z)).color(color);
        }
    }

}
