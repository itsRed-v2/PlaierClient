package net.itsred_v2.plaier.rendering;

import java.util.Objects;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.BeforeDebugRenderListener;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CubeHighlighter implements BeforeDebugRenderListener {

    public final float red;
    public final float green;
    public final float blue;
    public final float alpha;
    public BlockPos highlightedPos = null;
    private boolean enabled = false;

    public CubeHighlighter(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public void enable() {
        if (enabled) return;
        enabled = true;
        PlaierClient.getEventManager().add(BeforeDebugRenderListener.class, this);
    }

    public void disable() {
        if (!enabled) return;
        enabled = false;
        PlaierClient.getEventManager().remove(BeforeDebugRenderListener.class, this);
    }

    @Override
    public void beforeDebugRender(BeforeDebugRenderEvent event) {
        if (highlightedPos == null)
            return;

        WorldRenderContext context = event.getContext();
        VertexConsumerProvider consumers = Objects.requireNonNull(context.consumers());
        VertexConsumer lineConsumer = consumers.getBuffer(RenderLayer.getLines());

        Vec3d cam = context.camera().getPos();
        float x1 = (float) (highlightedPos.getX() - cam.x);
        float y1 = (float) (highlightedPos.getY() - cam.y);
        float z1 = (float) (highlightedPos.getZ() - cam.z);
        float x2 = (float) (highlightedPos.getX() + 1 - cam.x);
        float y2 = (float) (highlightedPos.getY() + 1 - cam.y);
        float z2 = (float) (highlightedPos.getZ() + 1 - cam.z);

        WorldRenderer.drawBox(context.matrixStack(), lineConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, alpha);

    }


}
