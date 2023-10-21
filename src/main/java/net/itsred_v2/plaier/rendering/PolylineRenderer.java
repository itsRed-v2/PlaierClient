package net.itsred_v2.plaier.rendering;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.BeforeDebugRenderListener;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;

public class PolylineRenderer implements BeforeDebugRenderListener {

    public int color;
    public List<Vec3d> vertices = new ArrayList<>();

    private boolean enabled = false;

    public PolylineRenderer(int color) {
        this.color = color;
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
        Vec3d camPos = event.getContext().camera().getPos();

        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        for (Vec3d v : vertices) {
            Vec3d vec = v.subtract(camPos);
            bufferBuilder.vertex(vec.x, vec.y, vec.z).color(color).next();
        }

        tessellator.draw();

        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }

}
