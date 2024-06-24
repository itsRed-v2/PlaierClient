package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.GuiRenderListener;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private DebugHud debugHud;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V",
                    remap = false,
                    ordinal = 3))
    public void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (debugHud.shouldShowDebugHud())
            return;

        EventManager.fire(new GuiRenderListener.GuiRenderEvent(context));
    }

}
