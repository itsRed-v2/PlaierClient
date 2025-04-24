package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.GetChatSuggestionsListener;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ClientCommandSource.class)
public abstract class ClientCommandSourceMixin implements CommandSource {
    
    @Inject(method = "getChatSuggestions",
            at = @At("HEAD"),
            cancellable = true)
    public void onGetChatSuggestions(CallbackInfoReturnable<Collection<String>> cir) {
        GetChatSuggestionsListener.GetChatSuggestionsEvent event = new GetChatSuggestionsListener.GetChatSuggestionsEvent();
        PlaierClient.getEventManager().fire(event);

        if (event.getNewSuggestions() != null) {
            cir.setReturnValue(event.getNewSuggestions());
        }

    }
    
}
