package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;
import net.minecraft.util.math.Vec3d;

public interface SetCamPosListener extends Listener {

    void onSetCamPos(SetCamPosEvent event);

    class SetCamPosEvent extends Event<SetCamPosListener> {

        public Vec3d pos;

        public SetCamPosEvent(Vec3d pos) {
            this.pos = pos;
        }

        public void setPosition(Vec3d pos) {
            this.pos = pos;
        }

        public Vec3d getPosition() {
            return this.pos;
        }

        @Override
        public void fire(ArrayList<SetCamPosListener> listeners) {
            for (SetCamPosListener l : listeners) {
                l.onSetCamPos(this);
            }
        }

        @Override
        public Class<SetCamPosListener> getListenerType() {
            return SetCamPosListener.class;
        }
    }


}
