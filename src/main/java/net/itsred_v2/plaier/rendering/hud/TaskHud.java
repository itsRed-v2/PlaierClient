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
import org.jetbrains.annotations.NotNull;

public class TaskHud extends Toggleable implements GuiRenderListener {

    private static final float GUI_SCALE = 0.5f;
    private static final int BAR_BACKGROUND_COLOR = ColorHelper.getArgb(144, 70, 70, 70);
    private static final int BACKGROUND_COLOR = ColorHelper.getArgb(144, 80, 80, 80);
    private static final int TEXT_COLOR = ColorHelper.getArgb(255, 255, 255, 255);
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

    /**
     * Additional information to be displayed.
     */
    private List<String> infoLines = new ArrayList<>();

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
        context.drawText(textRenderer, "§3Plaier §7§l» §fTask log", WIDGET_PADDING*2, WIDGET_PADDING*2, TEXT_COLOR, false);

        // Drawing the widget's content
        context.getMatrices().translate(0, barHeight, 0);
        
        context.fill(0, 0, WIDGET_WIDTH, widgetHeight, BACKGROUND_COLOR);

        for (int i = 0; i < lines.size() && i < WIDGET_LINE_COUNT; i++) {
            int posY = widgetHeight - lineHeight - WIDGET_PADDING - (i * lineHeight);
            LogLine line = lines.get(i);

            int age = tick - line.addedAt;
            int bgOpacity = 64 - (64 * age / LINE_HIGHLIGHT_DURATION);
            if (bgOpacity > 0) {
                int bgColor = ColorHelper.getArgb(bgOpacity, 255, 255, 255);
                context.fill(0, posY - 1, WIDGET_WIDTH, posY - 1 + lineHeight, bgColor);
            }
            
            context.drawText(textRenderer, line.content, WIDGET_PADDING, posY, TEXT_COLOR, false);
        }

        // Drawing additional info below widget
        context.getMatrices().translate(0, widgetHeight + 2, 0);

        for (int i = 0; i < infoLines.size(); i++) {
            drawTextLeftWithBackground(context, textRenderer, Text.of(infoLines.get(i)), WIDGET_WIDTH, i * lineHeight);
        }

        context.getMatrices().pop();
    }

    private static void drawTextLeftWithBackground(DrawContext context, TextRenderer textRenderer, Text text, int x, int y) {
        int textWidth = textRenderer.getWidth(text);
        context.fill(x - textWidth - 1, y, x, y + textRenderer.fontHeight, BACKGROUND_COLOR);
        context.drawText(textRenderer, text, x - textWidth, y + 1, TEXT_COLOR, false);
    }

    public void addMessage(String message) {
        int addedAt = PlaierClient.MC.getTicks();
        List<OrderedText> newLines = ChatMessages.breakRenderedChatMessageLines(
                Text.of(message),
                WIDGET_WIDTH - (WIDGET_PADDING * 2) - 5,
                PlaierClient.MC.getTextRenderer()
        );
        for (OrderedText line : newLines) {
            this.lines.addFirst(new LogLine(line, addedAt));
        }
    }

    public void clear() {
        this.lines.clear();
    }

    public void setInfoLines(@NotNull List<String> infoLines) {
        this.infoLines = infoLines;
    }

    private record LogLine(OrderedText content, int addedAt) {}

}
