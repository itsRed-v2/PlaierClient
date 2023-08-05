package net.itsred_v2.plaier.commands;

import java.util.Objects;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.ChatOutputListener;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class EchoCmd implements ChatOutputListener {

    @Override
    public void onChatOutput(ChatOutputEvent event) {
        String message = event.getMessage();
        if (!message.startsWith(":echo "))
            return;

        event.cancel();
        ClientPlayerEntity player = PlaierClient.MC.player;
        Objects.requireNonNull(player).sendMessage(Text.of(message.substring(5)), false);
    }
    
}
