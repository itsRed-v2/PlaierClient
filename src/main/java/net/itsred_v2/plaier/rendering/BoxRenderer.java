package net.itsred_v2.plaier.rendering;

import java.util.Objects;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.BeforeDebugRenderListener;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BoxRenderer implements BeforeDebugRenderListener {

    public final float red;
    public final float green;
    public final float blue;
    public final float alpha;
    public Box box = null;
    private boolean enabled = false;

    public BoxRenderer(float red, float green, float blue, float alpha) {
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
        if (box == null)
            return;

        WorldRenderContext context = event.getContext();
        VertexConsumerProvider consumers = Objects.requireNonNull(context.consumers());
        VertexConsumer lineConsumer = consumers.getBuffer(RenderLayer.getLines());

        Vec3d cam = context.camera().getPos();
        Box drawnBox = box.offset(-cam.x, -cam.y, -cam.z);
        WorldRenderer.drawBox(context.matrixStack(), lineConsumer, drawnBox, red, green, blue, alpha);

    }


}
