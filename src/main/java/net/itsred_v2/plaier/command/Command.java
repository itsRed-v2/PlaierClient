package net.itsred_v2.plaier.command;

import java.util.List;

public interface Command {

    void onCommand(List<String> args);
    
}
