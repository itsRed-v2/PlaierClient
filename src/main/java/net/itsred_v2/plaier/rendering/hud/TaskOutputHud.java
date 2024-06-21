package net.itsred_v2.plaier.rendering.hud;

import java.util.ArrayList;
import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.GuiRenderListener;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class TaskOutputHud extends Toggleable implements GuiRenderListener {

    private static final float GUI_SCALE = 0.5f;
    private static final int BAR_BACKGROUND_COLOR = ColorHelper.Argb.getArgb(120, 0, 0, 0);
    private static final int BACKGROUND_COLOR = ColorHelper.Argb.getArgb(100, 0, 0, 0);
    private static final int TEXT_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255);
    private static final int WIDGET_WIDTH = 300;
    private static final int WIDGET_LINE_COUNT = 11;
    /**
     * The padding between the border of the widget box and the text.
     */
    private static final int WIDGET_PADDING = 2;
    /**
     * Newly added lines are highlighted with a fading light background.
     * This controls how long the fading animation takes.
     */
    private static final int LINE_HIGHLIGHT_DURATION = 60; // 3 seconds

    /**
     * The lines of text rendered in the widget box
     */
    private final List<LogLine> lines = new ArrayList<>();

    @Override
    protected void onEnable() {
        PlaierClient.getEventManager().add(GuiRenderListener.class, this);
    }

    @Override
    protected void onDisable() {
        PlaierClient.getEventManager().remove(GuiRenderListener.class, this);
    }

    @Override
    public void onGuiRender(GuiRenderEvent event) {
        DrawContext context = event.getContext();
        TextRenderer textRenderer = PlaierClient.MC.getTextRenderer();
        int lineHeight = textRenderer.fontHeight;
        int widgetHeight = lineHeight * WIDGET_LINE_COUNT + WIDGET_PADDING * 2;
        float windowWidth = context.getScaledWindowWidth() / GUI_SCALE;
        int tick = PlaierClient.MC.getTicks();

        context.getMatrices().push();
        context.getMatrices().scale(GUI_SCALE, GUI_SCALE, 1.0f);
        context.getMatrices().translate(windowWidth - WIDGET_WIDTH - 4, 4, 50.0f);

        // Drawing the widget's top bar
        int barHeight = textRenderer.fontHeight + WIDGET_PADDING * 3;
        context.fill(0, 0, WIDGET_WIDTH, barHeight, BAR_BACKGROUND_COLOR);
        context.drawTextWithShadow(textRenderer, "§3Plaier §7§l» §fTask output", WIDGET_PADDING*2, WIDGET_PADDING*2, TEXT_COLOR);

        // Drawing the widget's content
        context.getMatrices().translate(0, barHeight, 0);
        
        context.fill(0, 0, WIDGET_WIDTH, widgetHeight, BACKGROUND_COLOR);

        for (int i = 0; i < lines.size() && i < WIDGET_LINE_COUNT; i++) {
            int posY = widgetHeight - lineHeight - WIDGET_PADDING - (i * lineHeight);
            LogLine line = lines.get(i);

            int age = tick - line.addedAt;
            int bgOpacity = 127 - (127 * age / LINE_HIGHLIGHT_DURATION);
            if (bgOpacity > 0) {
                int bgColor = ColorHelper.Argb.getArgb(bgOpacity, 255, 255, 255);
                context.fill(0, posY, WIDGET_WIDTH, posY + lineHeight, bgColor);
            }
            
            context.drawTextWithShadow(textRenderer, line.content, WIDGET_PADDING, posY, TEXT_COLOR);
        }

        context.getMatrices().pop();
    }

    public void addMessage(String message) {
        int addedAt = PlaierClient.MC.getTicks();
        List<OrderedText> newLines = ChatMessages.breakRenderedChatMessageLines(
                Text.of(message),
                WIDGET_WIDTH - (WIDGET_PADDING * 2) - 5,
                PlaierClient.MC.getTextRenderer()
        );
        for (OrderedText line : newLines) {
            this.lines.add(0, new LogLine(line, addedAt));
        }
    }

    private record LogLine(OrderedText content, int addedAt) {}

}
