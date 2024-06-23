package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;
import net.minecraft.world.GameMode;

public interface GameModeChangeListener extends Listener {

    void onGameModeChange(GameModeChangeEvent event);

    class GameModeChangeEvent extends Event<GameModeChangeListener> {

        public final GameMode gameMode;

        public GameModeChangeEvent(GameMode gameMode) {
            this.gameMode = gameMode;
        }

        @Override
        public void fire(ArrayList<GameModeChangeListener> listeners) {
            for (GameModeChangeListener l : listeners) {
                l.onGameModeChange(this);
            }
        }

        @Override
        public Class<GameModeChangeListener> getListenerType() {
            return GameModeChangeListener.class;
        }

    }
}
