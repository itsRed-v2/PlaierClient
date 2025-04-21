package net.itsred_v2.plaier.command;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.commands.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Commands {

    public static final List<Command> COMMANDS = List.of(
            new AutoToolCmd(), new DebugCmd(), new EchoCmd(), new FreecamCmd(),
            new HelpCmd(), new LookAtCmd(), new OverlayCmd(), new PathFindCmd(),
            new PinCmd(), new SayCmd(), new StopTaskCmd()
    );

    public static final Map<String, Command> COMMAND_MAP = new HashMap<>();

    public static void initializeCommandMap() {
        if (!COMMAND_MAP.isEmpty()) {
            PlaierClient.LOGGER.warn("Commands.initializeCommandMap() ran twice !");
            return;
        }

        for (Command cmd : COMMANDS) {
            for (String name : cmd.getNames()) {
                if (COMMAND_MAP.containsKey(name)) {
                    PlaierClient.LOGGER.warn("Conflict of command names: two commands named '{}'", name);
                } else {
                    COMMAND_MAP.put(name, cmd);
                }
            }
        }

    }

    @Nullable
    public static Command getByName(String name) {
        return COMMAND_MAP.get(name);
    }
}
