package net.itsred_v2.plaier.utils;

public abstract class Toggleable {

    private boolean enabled;

    public void enable() {
        if (enabled) return;
        enabled = true;
        onEnable();
    }

    public void disable() {
        if (!enabled) return;
        enabled = false;
        onDisable();
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

}
