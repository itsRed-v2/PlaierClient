package net.itsred_v2.plaier.rendering.world;

import java.util.Objects;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.BeforeDebugRenderListener;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BoxRenderer extends Toggleable implements BeforeDebugRenderListener {

    public final float red;
    public final float green;
    public final float blue;
    public final float alpha;
    public Box box = null;

    public BoxRenderer(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
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
        if (box == null)
            return;

        WorldRenderContext context = event.getContext();
        VertexConsumer lineConsumer = Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getLines());
        MatrixStack matrices = Objects.requireNonNull(context.matrixStack());

        Vec3d cam = context.camera().getPos();
        Box drawnBox = box.offset(-cam.x, -cam.y, -cam.z);
        WorldRenderer.drawBox(matrices, lineConsumer, drawnBox, red, green, blue, alpha);

    }


}
