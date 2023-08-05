package net.itsred_v2.plaier.session;

import java.util.Objects;

import net.itsred_v2.plaier.events.LeaveGameSessionListener;
import net.itsred_v2.plaier.events.StartGameSessionListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SessionManager implements LeaveGameSessionListener, StartGameSessionListener {

    private @Nullable Session session;

    @Override
    public void onStartGameSession() {
        session = new Session();
    }

    @Override
    public void onLeaveGameSession() {
        session = null;
    }

    public @NotNull Session getCurrentSession() {
        return Objects.requireNonNull(session);
    }

}
