package net.itsred_v2.plaier.rendering;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.GuiRenderListener;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;

public class PressedKeysHud implements GuiRenderListener {

    private boolean enabled = false;

    public void enable() {
        if (enabled) return;
        enabled = true;
        PlaierClient.getEventManager().add(GuiRenderListener.class, this);
    }

    public void disable() {
        if (!enabled) return;
        enabled = false;
        PlaierClient.getEventManager().remove(GuiRenderListener.class, this);
    }

    @Override
    public void onGuiRender(GuiRenderEvent event) {
        DrawContext context = event.getContext();
        TextRenderer textRenderer = PlaierClient.MC.getTextRenderer();

        context.getMatrices().push();
        context.getMatrices().translate(5, 5, 5);

        GameOptions options = PlaierClient.MC.getOptions();
        drawKey(context, textRenderer, 1, 0, 1, "Z", options.forwardKey.isPressed());
        drawKey(context, textRenderer, 0, 1, 1, "Q", options.leftKey.isPressed());
        drawKey(context, textRenderer, 1, 1, 1, "S", options.backKey.isPressed());
        drawKey(context, textRenderer, 2, 1, 1, "D", options.rightKey.isPressed());
        drawKey(context, textRenderer, 3, 0, 3, "Space", options.jumpKey.isPressed());
        drawKey(context, textRenderer, 3, 1, 3, "Shift", options.sneakKey.isPressed());
        drawKey(context, textRenderer, 0, 2, 6, "Sprinting", PlaierClient.getPlayer().isSprinting());

        context.getMatrices().pop();
    }

    private static void drawKey(DrawContext context, TextRenderer textRenderer, int offsetX, int offsetY, int width, String label, boolean pressed) {
        int offsetXpx = offsetX * 17;
        int offsetYpx = offsetY * 17;
        int heightPx = (17) - 2;
        int widthPx = (width * 17) - 2;

        context.fill(offsetXpx, offsetYpx, widthPx + offsetXpx, heightPx + offsetYpx, 0x88000000);
        context.fill(offsetXpx + 1, offsetYpx + 1, offsetXpx + widthPx - 1, offsetYpx + heightPx - 1, pressed ? 0x44000000 : 0x22FFFFFF);

        int cellCenterX = offsetXpx + (widthPx / 2);
        int cellCenterY = offsetYpx + (heightPx / 2);
        drawTextCentered(context, textRenderer, label, cellCenterX, cellCenterY);
    }

    private static void drawTextCentered(DrawContext context, TextRenderer textRenderer, String text, int centerX, int centerY) {
        int textPositionY = centerY - (textRenderer.fontHeight / 2);
        int textPositionX = centerX - (textRenderer.getWidth(text) / 2);
        context.drawText(textRenderer, text, textPositionX + 1, textPositionY + 1, 0xFFFFFF, true);
    }

}
