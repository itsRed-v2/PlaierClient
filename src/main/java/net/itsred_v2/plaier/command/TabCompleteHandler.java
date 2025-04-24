package net.itsred_v2.plaier.command;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.ChatSuggestorRefreshListener;
import net.itsred_v2.plaier.events.GetChatSuggestionsListener;
import net.itsred_v2.plaier.mixinterface.ChatScreenInterface;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabCompleteHandler implements GetChatSuggestionsListener, ChatSuggestorRefreshListener {

    @Override
    public void onGetChatSuggestions(GetChatSuggestionsEvent event) {
        Screen currentScreen = PlaierClient.MC.getCurrentScreen();
        if (!(currentScreen instanceof ChatScreen chatScreen)) {
            throw new IllegalStateException(
                    "GetChatSuggestions event fired but the current screen is not an instance of ChatScreen");
        }

        String chatFieldText = ((ChatScreenInterface) chatScreen).plaierClient$getChatFieldText();
        if (!chatFieldText.startsWith(CommandHandler.COMMAND_TOKEN)) return;

        chatFieldText = chatFieldText.substring(1); // removing the command token
        List<String> args = new ArrayList<>(List.of(chatFieldText.split(" ")));

        // the split function does not split on the last character (trailing empty strings are not included in the split array)
        if (chatFieldText.endsWith(" ")) {
            args.add("");
        }

        if (args.size() == 1) {
            Collection<String> suggestions = Commands.COMMAND_MAP.keySet()
                    .stream()
                    .map(commandName -> CommandHandler.COMMAND_TOKEN + commandName)
                    .toList();
            event.replaceSuggestions(suggestions);
        } else {
            String cmdName = args.removeFirst();
            Command cmd = Commands.getByName(cmdName);
            if (cmd == null) {
                event.replaceSuggestions(List.of());
            } else {
                event.replaceSuggestions(cmd.onTabComplete(args));
            }
        }
    }

    @Override
    public void afterChatSuggestorRefresh(ChatSuggestorRefreshEvent event) {
        if (event.chatFieldText.startsWith(CommandHandler.COMMAND_TOKEN)
                && (event.windowIsNull || !event.completingSuggestions)) {
            event.showSuggestionWindow();
        }
    }

}
