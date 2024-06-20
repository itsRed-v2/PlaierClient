package net.itsred_v2.plaier;

import net.itsred_v2.plaier.rendering.PressedKeysHud;
import net.itsred_v2.plaier.tasks.pathProcessing.WalkPathProcessor;

public class DebugOptions {

    private final PressedKeysHud pressedKeysHud = new PressedKeysHud();

    public void everything(boolean enabled) {
        pathProcessorTargetNode(enabled);
        pressedKeys(enabled);
    }

    public void pathProcessorTargetNode(boolean enabled) {
        if (enabled) WalkPathProcessor.debugRenderer.enable();
        else WalkPathProcessor.debugRenderer.disable();
    }

    public void pressedKeys(boolean enabled) {
        if (enabled) this.pressedKeysHud.enable();
        else this.pressedKeysHud.disable();
    }

}
