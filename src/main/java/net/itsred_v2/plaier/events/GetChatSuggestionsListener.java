package net.itsred_v2.plaier.events;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public interface GetChatSuggestionsListener extends Listener {

    void onGetChatSuggestions(GetChatSuggestionsEvent event);

    class GetChatSuggestionsEvent extends Event<GetChatSuggestionsListener> {

        private Collection<String> suggestions = null;

        public void replaceSuggestions(Collection<String> newSuggestions) {
            this.suggestions = newSuggestions;
        }

        @Nullable
        public Collection<String> getNewSuggestions() {
            return this.suggestions;
        }

        @Override
        public void fire(ArrayList<GetChatSuggestionsListener> listeners) {
            for (GetChatSuggestionsListener l : listeners) {
                l.onGetChatSuggestions(this);
            }
        }

        @Override
        public Class<GetChatSuggestionsListener> getListenerType() {
            return GetChatSuggestionsListener.class;
        }

    }
}
