package net.itsred_v2.plaier.utils.control;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.KeyListener;
import net.itsred_v2.plaier.mixinterface.KeyBindingInterface;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

public class KeyboardController extends Toggleable implements KeyListener {

    private final List<KeyBinding> controlledKeybindings;
    private final GameOptions options;

    public KeyboardController(GameOptions options) {
        this.options = options;
        this.controlledKeybindings = List.of(
                options.forwardKey,
                options.leftKey,
                options.rightKey,
                options.backKey,
                options.sneakKey,
                options.jumpKey,
                options.sprintKey,
                options.attackKey,
                options.useKey,
                options.dropKey
        );
    }

    @Override
    protected void onEnable() {
        PlaierClient.getEventManager().add(KeyListener.class, this);
    }

    @Override
    protected void onDisable() {
        PlaierClient.getEventManager().remove(KeyListener.class, this);
    }

    @Override
    public void onKey(KeyEvent event) {
        if (controlledKeybindings.contains(event.keyBinding)) {
            event.cancel();
        }
    }

    public void unpressAllKeys() {
        for (KeyBinding keyBinding : controlledKeybindings) {
            ((KeyBindingInterface) keyBinding).plaierClient_setPressedBypass(false);
        }
    }

    private void setPressed(KeyBinding keyBinding, boolean pressed) {
        if (!controlledKeybindings.contains(keyBinding)) {
            throw new IllegalArgumentException("Trying to control unlisted keybinding.");
        }
        ((KeyBindingInterface) keyBinding).plaierClient_setPressedBypass(pressed);
    }

    public void forwardKey(boolean pressed) {
        setPressed(options.forwardKey, pressed);
    }

    public void jumpKey(boolean pressed) {
        setPressed(options.jumpKey, pressed);
    }

}
