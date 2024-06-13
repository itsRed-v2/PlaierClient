package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;
import net.minecraft.client.gui.DrawContext;

public interface GuiRenderListener extends Listener {

    void onGuiRender(GuiRenderEvent event);

    class GuiRenderEvent extends Event<GuiRenderListener> {

        private final DrawContext context;

        public GuiRenderEvent(DrawContext context) {
            this.context = context;
        }

        public DrawContext getContext() {
            return context;
        }

        @Override
        public void fire(ArrayList<GuiRenderListener> listeners) {
            for (GuiRenderListener l : listeners) {
                l.onGuiRender(this);
            }
        }

        @Override
        public Class<GuiRenderListener> getListenerType() {
            return GuiRenderListener.class;
        }
    }
}
