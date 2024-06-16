package net.itsred_v2.plaier.session;

import net.itsred_v2.plaier.task.TaskManager;

public class Session {

    private final TaskManager taskManager;

    private boolean terminated = false;

    public Session() {
        taskManager = new TaskManager();
    }

    public TaskManager getTaskManager() {
        throwIfTerminated();
        return taskManager;
    }

    public void terminate() {
        taskManager.onSessionEnd();
        terminated = true;
    }

    private void throwIfTerminated() {
        if (terminated) {
            throw new UnsupportedOperationException("Trying to use session but it is terminated.");
        }
    }
}
