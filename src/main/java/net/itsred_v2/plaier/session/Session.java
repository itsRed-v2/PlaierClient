package net.itsred_v2.plaier.session;

import net.itsred_v2.plaier.task.TaskLifeManager;

public class Session {

    private final TaskLifeManager taskManager;

    private boolean terminated = false;

    public Session() {
        taskManager = new TaskLifeManager();
    }

    public TaskLifeManager getTaskManager() {
        throwIfTerminated();
        return taskManager;
    }

    public void terminate() {
        taskManager.stopTask();
        terminated = true;
    }

    private void throwIfTerminated() {
        if (terminated) {
            throw new UnsupportedOperationException("Trying to use session but it is terminated.");
        }
    }
}
