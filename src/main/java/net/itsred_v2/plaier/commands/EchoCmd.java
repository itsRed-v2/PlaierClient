package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.ChatOutputListener;

public class EchoCmd implements ChatOutputListener {

    @Override
    public void onChatOutput(ChatOutputEvent event) {
        String message = event.getMessage();
        if (!message.startsWith(":echo "))
            return;

        event.cancel();

        PlaierClient.getCurrentSession().getMessenger().send(message.substring(6));

    }
    
}
