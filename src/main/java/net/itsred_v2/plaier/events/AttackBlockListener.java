package net.itsred_v2.plaier.events;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public interface AttackBlockListener extends Listener {

    void onAttackBlock(AttackBlockEvent event);

    class AttackBlockEvent extends Event<AttackBlockListener> {

        public final BlockPos pos;

        public AttackBlockEvent(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public void fire(ArrayList<AttackBlockListener> listeners) {
            for (AttackBlockListener l : listeners) {
                l.onAttackBlock(this);
            }
        }

        @Override
        public Class<AttackBlockListener> getListenerType() {
            return AttackBlockListener.class;
        }
    }
}
