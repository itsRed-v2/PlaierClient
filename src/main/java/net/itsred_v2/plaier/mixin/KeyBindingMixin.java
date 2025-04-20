package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.KeyListener.KeyEvent;
import net.itsred_v2.plaier.mixinterface.KeyBindingInterface;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements Comparable<KeyBinding>, KeyBindingInterface {

    @Shadow @Final private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;

    @Shadow private boolean pressed;

    @Inject(method = "onKeyPressed",
            at = @At("HEAD"),
            cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        KeyBinding keyBinding = KEY_TO_BINDINGS.get(key);
        if (keyBinding == null)
            return;
        KeyEvent event = new KeyEvent(keyBinding, KeyEvent.Method.ON_PRESS, true);
        PlaierClient.getEventManager().fire(event);

        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(method = "setPressed",
            at = @At("HEAD"),
            cancellable = true)
    public void setPressed(boolean pressed, CallbackInfo ci) {
        KeyBinding instance = (KeyBinding) (Object) this;
        KeyEvent event = new KeyEvent(instance, KeyEvent.Method.SET_PRESSED, pressed);
        PlaierClient.getEventManager().fire(event);

        if (event.isCancelled())
            ci.cancel();
    }

    @Override
    public void plaierClient_setPressedBypass(boolean pressed) {
        this.pressed = pressed;
    }

}
