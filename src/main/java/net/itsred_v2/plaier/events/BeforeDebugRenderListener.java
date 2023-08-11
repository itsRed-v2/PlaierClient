package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;

public interface BeforeDebugRenderListener extends Listener {

    void beforeDebugRender(BeforeDebugRenderEvent event);

    class BeforeDebugRenderEvent extends Event<BeforeDebugRenderListener> {

        private final WorldRenderContext context;

        public BeforeDebugRenderEvent(WorldRenderContext context) {
            this.context = context;
        }

        public WorldRenderContext getContext() {
            return context;
        }

        @Override
        public void fire(ArrayList<BeforeDebugRenderListener> listeners) {
            for (BeforeDebugRenderListener l : listeners) {
                l.beforeDebugRender(this);
            }
        }

        @Override
        public Class<BeforeDebugRenderListener> getListenerType() {
            return BeforeDebugRenderListener.class;
        }
    }
}
