package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.control.RotationUtils;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LookAtCmd extends Command {

    @Override
    public void onCommand(@NotNull List<String> args) {
        if (args.size() != 3) {
            this.sendSyntaxErrorMessage();
            return;
        }

        double posX = Double.parseDouble(args.get(0));
        double posY = Double.parseDouble(args.get(1));
        double posZ = Double.parseDouble(args.get(2));

        Vec3d pos = new Vec3d(posX, posY, posZ);
        RotationUtils.facePos(pos);

    }

    @Override
    public List<String> getHelp() {
        return List.of("Changes your rotation so you are looking precisely towards the given position");
    }

    @Override
    public List<String> getUse() {
        return List.of(":lookat <x> <y> <z>");
    }

    @Override
    public List<String> getNames() {
        return List.of("lookat");
    }

}
