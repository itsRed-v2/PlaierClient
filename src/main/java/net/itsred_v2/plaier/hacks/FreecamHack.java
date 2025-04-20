package net.itsred_v2.plaier.hacks;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.AfterCameraUpdateListener;
import net.itsred_v2.plaier.events.ChangeLookDirectionListener;
import net.itsred_v2.plaier.events.KeyListener;
import net.itsred_v2.plaier.events.LeaveGameSessionListener;
import net.itsred_v2.plaier.events.PlayerDeathListener;
import net.itsred_v2.plaier.events.SetCamPosListener;
import net.itsred_v2.plaier.events.SetCamRotationListener;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.events.WorldJoinListener;
import net.itsred_v2.plaier.utils.Messenger;
import net.itsred_v2.plaier.utils.Toggleable;
import net.itsred_v2.plaier.utils.control.PlayerController;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FreecamHack extends Toggleable implements SetCamPosListener, SetCamRotationListener,
        AfterCameraUpdateListener, LeaveGameSessionListener, UpdateListener,
        ChangeLookDirectionListener, WorldJoinListener, PlayerDeathListener, KeyListener {

    private Vec3d camPos;
    private Vec3d prevCamPos;
    private float yaw;
    private float pitch;
    private PlayerController playerController;
    private boolean controlPlayer = false;

    @Override
    protected void onEnable() {
        this.controlPlayer = false;
        this.playerController = new PlayerController();
        this.playerController.enable();
        PlaierClient.MC.getOptions().setPerspective(Perspective.FIRST_PERSON);

        ClientPlayerEntity player = PlaierClient.getPlayer();
        this.camPos = player.getEyePos();
        this.prevCamPos = player.getEyePos();
        this.yaw = player.getYaw();
        this.pitch = player.getPitch();

        PlaierClient.getEventManager().add(SetCamPosListener.class, this);
        PlaierClient.getEventManager().add(SetCamRotationListener.class, this);
        PlaierClient.getEventManager().add(AfterCameraUpdateListener.class, this);
        PlaierClient.getEventManager().add(LeaveGameSessionListener.class, this);
        PlaierClient.getEventManager().add(UpdateListener.class, this);
        PlaierClient.getEventManager().add(ChangeLookDirectionListener.class, this);
        PlaierClient.getEventManager().add(WorldJoinListener.class, this);
        PlaierClient.getEventManager().add(PlayerDeathListener.class, this);
        PlaierClient.getEventManager().add(KeyListener.class, this);
    }

    @Override
    protected void onDisable() {
        this.playerController.disable();
        PlaierClient.getEventManager().remove(SetCamPosListener.class, this);
        PlaierClient.getEventManager().remove(SetCamRotationListener.class, this);
        PlaierClient.getEventManager().remove(AfterCameraUpdateListener.class, this);
        PlaierClient.getEventManager().remove(LeaveGameSessionListener.class, this);
        PlaierClient.getEventManager().remove(UpdateListener.class, this);
        PlaierClient.getEventManager().remove(ChangeLookDirectionListener.class, this);
        PlaierClient.getEventManager().remove(WorldJoinListener.class, this);
        PlaierClient.getEventManager().remove(PlayerDeathListener.class, this);
        PlaierClient.getEventManager().remove(KeyListener.class, this);
    }

    public void setControllingPlayer(boolean controlPlayer) {
        this.controlPlayer = controlPlayer;
        // when the playerController is enabled, all controls are blocked.
        // Hence, if we want the user to be able to control the player, we need to disable it.
        if (controlPlayer) {
            playerController.disable();
        } else {
            playerController.enable();
        }
    }

    public boolean isControllingPlayer() {
        return controlPlayer;
    }

    @Override
    public void onSetCamPos(SetCamPosEvent event) {
        // Lerp between previous and current camera pos for smooth movement
        Vec3d lerpedPos = this.prevCamPos.lerp(this.camPos, PlaierClient.MC.getTickProgress());
        // Replace the game's camera position with our custom position.
        event.setPosition(lerpedPos);
    }

    @Override
    public void onSetCamRotation(SetCamRotationEvent event) {
        // Replace the camera's rotation with our custom rotation.
        event.pitch = this.pitch;
        event.yaw = this.yaw;
    }

    @Override
    public void afterCameraUpdate(AfterCameraUpdateEvent event) {
        // This forces the player to be rendered by making the game
        // think we are in third person mode (even though we aren't)
        event.thirdPerson = true;
    }

    @Override
    public void onLeaveGameSession() {
        // It is very important to disable this hack on session leave.
        // Changing saves with the hack enabled crashes the game
        // by setting the camera's position to absurd values.
        this.disable();
    }

    @Override
    public void onWorldJoin() {
        // It is very important to disable this hack on world (dimension) change.
        // Changing worlds with the hack enabled can crash the game
        // by setting the camera's position to absurd values.
        this.disable();
    }

    @Override
    public void onPlayerDeath() {
        this.disable();
    }

    @Override
    public void onUpdate() {
        Vec3d movement;

        // If we are controlling the player and not the camera,
        // OR if any screen is open (such as chat or inventory)
        // then, don't process camera movements.
        if (controlPlayer || PlaierClient.MC.getCurrentScreen() != null) {
            movement = Vec3d.ZERO;
        } else {
            double movementForward = 0;
            double movementRight = 0;
            double movementUp = 0;

            GameOptions options = PlaierClient.MC.getOptions();
            if (isKeyPressed(options.forwardKey)) movementForward += 1;
            if (isKeyPressed(options.backKey)) movementForward -= 1;
            if (isKeyPressed(options.rightKey)) movementRight += 1;
            if (isKeyPressed(options.leftKey)) movementRight -= 1;
            if (isKeyPressed(options.jumpKey)) movementUp += 1;
            if (isKeyPressed(options.sneakKey)) movementUp -= 1;

            // I don't clearly understand this math compound, but it transforms
            // the movement vector from player-oriented space to world-oriented space.
            double yawInRad = Math.toRadians(this.yaw);
            double movementX = - Math.sin(yawInRad) * movementForward - Math.cos(yawInRad) * movementRight;
            double movementZ = Math.cos(yawInRad) * movementForward - Math.sin(yawInRad) * movementRight;
            movement = new Vec3d(movementX, movementUp, movementZ).normalize();

            if (isKeyPressed(options.sprintKey)) {
                movement = movement.multiply(2);
            }
        }

        // Apply the movement vector
        this.prevCamPos = this.camPos;
        this.camPos = this.camPos.add(movement);
    }

    public static boolean isKeyPressed(KeyBinding keyBinding) {
        // Hacky one-liner to check if a given key-binding is actually pressed.
        // We can't just do keyBinding.isPressed() because values are messed up by the MovementUtils.lockControls() call.
        return InputUtil.isKeyPressed(PlaierClient.MC.getWindowHandle(), KeyBindingHelper.getBoundKeyOf(keyBinding).getCode());
    }

    @Override
    public void onChangeLookDirection(ChangeLookDirectionEvent event) {
        // If we are controlling the player and not the camera, don't process camera rotation control.
        if (controlPlayer) return;

        // Apply the change in direction to the camera's yaw and pitch.
        this.pitch += (float) event.cursorDeltaY * 0.15f;
        this.yaw += (float) event.cursorDeltaX * 0.15f;
        this.pitch = MathHelper.clamp(this.pitch, -90.0f, 90.0f);
    }

    @Override
    public void onKey(KeyEvent event) {
        if (PlaierClient.MC.getOptions().togglePerspectiveKey.equals(event.keyBinding)) {
            event.cancel();
            if (event.method == KeyEvent.Method.ON_PRESS)
                Messenger.overlay("Third-person mode cannot be enabled in freecam.");
        }
    }
}
