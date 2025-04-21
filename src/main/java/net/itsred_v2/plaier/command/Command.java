package net.itsred_v2.plaier.command;

import net.itsred_v2.plaier.utils.Messenger;

import java.util.List;

public abstract class Command {

    public abstract void onCommand(List<String> args);

    public abstract List<String> getHelp();

    public abstract List<String> getUse();

    public abstract List<String> getNames();

    public String getName() {
        return getNames().getFirst();
    }

    public List<String> getAliases() {
        return getNames().subList(1, getNames().size());
    }

    public void sendSyntaxErrorMessage() {
        Messenger.chat("§cInvalid syntax.");
        this.sendUse();
    }

    public void sendUse() {
        Messenger.chat("§cUse:");
        for (String useLine : this.getUse()) {
            Messenger.chat("§7- §6" + useLine);
        }

        Messenger.chat("§cUse §6:help %s §cfor more help", this.getName());
    }

}
