package net.itsred_v2.plaier.events;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;

import java.util.ArrayList;

public interface ChatSuggestorRefreshListener extends Listener {

    void afterChatSuggestorRefresh(ChatSuggestorRefreshEvent event);

    class ChatSuggestorRefreshEvent extends Event<ChatSuggestorRefreshListener> {

        public final String chatFieldText;
        public final boolean windowIsNull;
        public final boolean completingSuggestions;

        private boolean shouldShowSuggestionWindow = false;

        public ChatSuggestorRefreshEvent(String chatFieldText, boolean windowIsNull, boolean completingSuggestions) {
            this.chatFieldText = chatFieldText;
            this.windowIsNull = windowIsNull;
            this.completingSuggestions = completingSuggestions;
        }

        public void showSuggestionWindow() {
            this.shouldShowSuggestionWindow = true;
        }

        public boolean shouldShowSuggestionWindow() {
            return shouldShowSuggestionWindow;
        }

        @Override
        public void fire(ArrayList<ChatSuggestorRefreshListener> listeners) {
            for (ChatSuggestorRefreshListener l : listeners) {
                l.afterChatSuggestorRefresh(this);
            }
        }

        @Override
        public Class<ChatSuggestorRefreshListener> getListenerType() {
            return ChatSuggestorRefreshListener.class;
        }

    }
}
