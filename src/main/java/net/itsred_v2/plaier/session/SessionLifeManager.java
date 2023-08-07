package net.itsred_v2.plaier.session;

import java.util.Objects;

import net.itsred_v2.plaier.events.LeaveGameSessionListener;
import net.itsred_v2.plaier.events.StartGameSessionListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SessionLifeManager implements LeaveGameSessionListener, StartGameSessionListener {

    private @Nullable Session session;

    @Override
    public void onStartGameSession() {
        session = new Session();
    }

    @Override
    public void onLeaveGameSession() {
        Objects.requireNonNull(session).terminate();
        session = null;
    }

    public @NotNull Session getCurrentSession() {
        if (session == null) {
            throw new RuntimeException("Trying to access session while null.");
        }
        return session;
    }

}
