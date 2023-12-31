package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;
import net.itsred_v2.plaier.utils.control.RotationHelper;
import net.itsred_v2.plaier.session.Session;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class LookAtCmd implements Command {

    @Override
    public void onCommand(@NotNull List<String> args) {
        Session session = PlaierClient.getCurrentSession();
        Messenger messenger = session.getMessenger();
        RotationHelper rotationHelper = session.getRotationHelper();

        if (args.size() != 3) {
            messenger.send("§cInvalid syntax.");
            return;
        }

        double posX = Double.parseDouble(args.get(0));
        double posY = Double.parseDouble(args.get(1));
        double posZ = Double.parseDouble(args.get(2));

        Vec3d pos = new Vec3d(posX, posY, posZ);
        rotationHelper.facePos(pos);

    }

}
