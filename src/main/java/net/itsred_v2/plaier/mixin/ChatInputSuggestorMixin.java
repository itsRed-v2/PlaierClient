package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.ChatSuggestorRefreshListener.ChatSuggestorRefreshEvent;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {

    @Shadow
    @Final
    TextFieldWidget textField;

    @Shadow
    @Nullable
    private ChatInputSuggestor.SuggestionWindow window;

    @Shadow
    boolean completingSuggestions;

    @Shadow
    public abstract void show(boolean narrateFirstSuggestion);

    @Inject(method = "refresh",
            at = @At("TAIL"))
    public void afterRefresh(CallbackInfo ci) {
        ChatSuggestorRefreshEvent event = new ChatSuggestorRefreshEvent(textField.getText(), window == null, completingSuggestions);
        PlaierClient.getEventManager().fire(event);

        if (event.shouldShowSuggestionWindow()) {
            this.show(false);
        }
    }

}
