package net.itsred_v2.plaier.events;

import net.itsred_v2.plaier.event.CancellableEvent;
import net.itsred_v2.plaier.event.Listener;
import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;

public interface KeyListener extends Listener {

    void onKey(KeyEvent event);

    class KeyEvent extends CancellableEvent<KeyListener> {

        public final KeyBinding keyBinding;
        public final Method method;
        public final boolean isPressed;

        public KeyEvent(KeyBinding keyBinding, Method method, boolean isPressed) {
            this.keyBinding = keyBinding;
            this.isPressed = isPressed;
            this.method = method;
        }

        @Override
        public void fire(ArrayList<KeyListener> listeners) {
            for (KeyListener l : listeners) {
                l.onKey(this);
            }
        }

        @Override
        public Class<KeyListener> getListenerType() {
            return KeyListener.class;
        }

        public enum Method {
            ON_PRESS,
            SET_PRESSED
        }

    }
}
