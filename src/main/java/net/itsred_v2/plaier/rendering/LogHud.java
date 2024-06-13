package net.itsred_v2.plaier.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.GuiRenderListener;
import net.itsred_v2.plaier.events.StartGameSessionListener;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class LogHud implements GuiRenderListener, StartGameSessionListener {

    private static final float GUI_SCALE = 0.5f;
    private static final int BACKGROUND_COLOR = ColorHelper.Argb.getArgb(50, 0, 0, 0);
    private static final int TEXT_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255);
    private static final int WIDGET_WIDTH = 300;
    private static final int WIDGET_LINE_COUNT = 11;
    /**
     * The padding between the border of the widget box and the text.
     */
    private static final int WIDGET_PADDING = 2;
    /**
     * The lines of text rendered in the widget box
     */
    private final List<OrderedText> lines = new ArrayList<>();

    public LogHud() {
        PlaierClient.getEventManager().add(GuiRenderListener.class, this);
        PlaierClient.getEventManager().add(StartGameSessionListener.class, this);
    }

    @Override
    public void onStartGameSession() {
        this.lines.clear();
    }

    @Override
    public void onGuiRender(GuiRenderEvent event) {
        DrawContext context = event.getContext();
        TextRenderer textRenderer = PlaierClient.MC.textRenderer;
        int lineHeight = textRenderer.fontHeight;
        int widgetHeight = lineHeight * WIDGET_LINE_COUNT + WIDGET_PADDING * 2;
        float windowWidth = context.getScaledWindowWidth() / GUI_SCALE;

        context.getMatrices().push();
        context.getMatrices().scale(GUI_SCALE, GUI_SCALE, 1.0f);
        context.getMatrices().translate(windowWidth - WIDGET_WIDTH - 4, 4, 50.0f);

        context.fill(0, 0, WIDGET_WIDTH, widgetHeight, BACKGROUND_COLOR);

        for (int i = 0; i < lines.size() && i < WIDGET_LINE_COUNT; i++) {
            int posY = widgetHeight - lineHeight - WIDGET_PADDING - (i * lineHeight);
            context.drawText(textRenderer, lines.get(i), WIDGET_PADDING, posY, TEXT_COLOR, true);
        }

        context.getMatrices().pop();
    }

    public void addMessage(String message) {
        List<OrderedText> newLines = ChatMessages.breakRenderedChatMessageLines(
                Text.of(message),
                WIDGET_WIDTH - (WIDGET_PADDING * 2) - 5,
                PlaierClient.MC.textRenderer
        );
        Collections.reverse(newLines);
        this.lines.addAll(0, newLines);
    }

}
