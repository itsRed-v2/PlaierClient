package net.itsred_v2.plaier.task;

public class TaskLifeManager {

    private Task task;

    /**
     * Starts a new task if no other task is currently running.
     * @param newTask the task to start
     * @return true if the task was started successfully
     */
    public boolean startTask(Task newTask) {
        clearTaskIfDone();

        if (task != null)
            return false;

        task = newTask;
        task.start();
        return true;
    }

    /**
     * Stops the task currently running, if there is one.
     * @return true if a task was stopped, false if there was no task
     */
    public boolean stopTask() {
        clearTaskIfDone();

        if (task == null)
            return false;

        task.terminate();
        task = null;
        return true;
    }

    public void clearTaskIfDone() {
        if (task != null && task.isDone()) {
            task = null;
        }
    }

}
