package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.AfterCameraUpdateListener;
import net.itsred_v2.plaier.events.SetCamPosListener;
import net.itsred_v2.plaier.events.SetCamRotationListener;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow private boolean thirdPerson;

    @Shadow protected abstract void setPos(Vec3d pos);

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Redirect(method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    public void setPosRedirect(Camera instance, double x, double y, double z) {
        SetCamPosListener.SetCamPosEvent event = new SetCamPosListener.SetCamPosEvent(new Vec3d(x, y, z));
        EventManager.fire(event);
        this.setPos(event.getPosition());
    }

    @Redirect(method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0))
    public void setRotationRedirect(Camera instance, float yaw, float pitch) {
        setRotationRedirectImpl(yaw, pitch);
    }

    @Redirect(method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 2))
    public void setRotationRedirectSleeping(Camera instance, float yaw, float pitch) {
        setRotationRedirectImpl(yaw, pitch);
    }

    @Unique
    private void setRotationRedirectImpl(float yaw, float pitch) {
        SetCamRotationListener.SetCamRotationEvent event = new SetCamRotationListener.SetCamRotationEvent(yaw, pitch);
        EventManager.fire(event);
        this.setRotation(event.yaw, event.pitch);
    }

    @Inject(method = "update", at = @At("TAIL"))
    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        AfterCameraUpdateListener.AfterCameraUpdateEvent event = new AfterCameraUpdateListener.AfterCameraUpdateEvent(thirdPerson);
        EventManager.fire(event);

        if (event.thirdPerson != thirdPerson) {
            this.thirdPerson = event.thirdPerson;
        }
    }

}
