package net.itsred_v2.plaier.rendering.hud;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.GuiRenderListener;
import net.itsred_v2.plaier.events.LeaveGameSessionListener;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;

public class OverlayMessageHud extends Toggleable implements GuiRenderListener, UpdateListener, LeaveGameSessionListener {

    private static final int TEXT_FADE_IN = 3; // Text fades in during 3 ticks (0.15s)
    private static final int TEXT_FADE_OUT = 20; // Text fades out during 20 ticks (1s)
    private static final int TEXT_STAY = 5 * 20; // Text stays for 5 seconds (fade in and out included)

    private Text message;
    private int remainingTicks = 0;

    @Override
    protected void onEnable() {
        PlaierClient.getEventManager().add(GuiRenderListener.class, this);
        PlaierClient.getEventManager().add(UpdateListener.class, this);
        PlaierClient.getEventManager().add(LeaveGameSessionListener.class, this);
    }

    @Override
    protected void onDisable() {
        PlaierClient.getEventManager().remove(GuiRenderListener.class, this);
        PlaierClient.getEventManager().remove(UpdateListener.class, this);
        PlaierClient.getEventManager().remove(LeaveGameSessionListener.class, this);
    }

    public void setMessage(Text message) {
        this.message = message;
        this.remainingTicks = TEXT_STAY;
    }

    @Override
    public void onGuiRender(GuiRenderEvent event) {
        if (message == null || remainingTicks == 0)
            return;

        DrawContext context = event.getContext();
        TextRenderer textRenderer = PlaierClient.MC.getTextRenderer();

        int textWidth = textRenderer.getWidth(this.message);
        float textCenterY = context.getScaledWindowHeight() - 68 + textRenderer.fontHeight;
        float textCenterX = (float) context.getScaledWindowWidth() / 2;

        float timeRemaining = remainingTicks - PlaierClient.MC.getTickProgress();
        int textFadeInOpacity = Math.min(255, (int) ((TEXT_STAY - timeRemaining) * 255 / TEXT_FADE_IN));
        int textFadeOutOpacity = Math.min(255, (int) (timeRemaining * 255 / TEXT_FADE_OUT));
        int textOpacity = Math.min(textFadeInOpacity, textFadeOutOpacity);
        // Under 8, text opacity glitches and is rendered like opacity 255.
        // Text is practically invisible below opacity 8 anyway, so we can skip rendering it.
        if (textOpacity <= 8)
            return;
        int textColor = ColorHelper.withAlpha(textOpacity, Colors.WHITE);

        context.getMatrices().push();
        context.getMatrices().translate(textCenterX, textCenterY, 0.0F);
        context.drawTextWithBackground(textRenderer, this.message, -textWidth / 2, -4, textWidth, textColor);
        context.getMatrices().pop();
    }

    @Override
    public void onUpdate() {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
        }
    }

    @Override
    public void onLeaveGameSession() {
        this.remainingTicks = 0;
    }
}
