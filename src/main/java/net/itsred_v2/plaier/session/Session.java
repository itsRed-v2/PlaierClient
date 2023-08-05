package net.itsred_v2.plaier.session;

import java.util.Objects;

import net.itsred_v2.plaier.PlaierClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class Session {

    private final Messenger messenger;

    public Session() {
        messenger = new Messenger(getPlayer());
    }
    
    public @NotNull ClientPlayerEntity getPlayer() {
        return Objects.requireNonNull(PlaierClient.MC.player);
    }

    public Messenger getMessenger() {
        return messenger;
    }
}
