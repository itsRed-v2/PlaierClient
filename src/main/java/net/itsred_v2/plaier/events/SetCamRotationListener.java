package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;

public interface SetCamRotationListener extends Listener {

    void onSetCamRotation(SetCamRotationEvent event);

    class SetCamRotationEvent extends Event<SetCamRotationListener> {

        public float yaw;
        public float pitch;

        public SetCamRotationEvent(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        @Override
        public void fire(ArrayList<SetCamRotationListener> listeners) {
            for (SetCamRotationListener l : listeners) {
                l.onSetCamRotation(this);
            }
        }

        @Override
        public Class<SetCamRotationListener> getListenerType() {
            return SetCamRotationListener.class;
        }
    }

}
