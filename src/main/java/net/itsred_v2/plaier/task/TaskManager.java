package net.itsred_v2.plaier.task;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.LeaveGameSessionListener;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.rendering.hud.TaskOutputHud;
import net.itsred_v2.plaier.utils.Messenger;
import org.jetbrains.annotations.Nullable;

public class TaskManager implements UpdateListener, LeaveGameSessionListener {

    private static final String INFO_PREFIX = "§7[Info] §r";
    private static final String FAIL_PREFIX = "§4[Fail] §c";
    private static final String SUCCESS_PREFIX = "§2[Success] §a";

    /**
     * The hud which displays the task output.
     */
    private final TaskOutputHud taskOutputHud;
    /**
     * The "consumer" callback that the task can call to display text.
     */
    private final TaskOutputConsumer taskOutputConsumer;
    /**
     * The currently running task.
     */
    private @Nullable Task task;
    /**
     * If not -1, this value decreases every tick and when it reaches zero, the output hud is disabled.
     * Used to schedule the disabling of the output hud.
     */
    private int outputHudTicksRemaining = -1;

    public TaskManager() {
        PlaierClient.getEventManager().add(UpdateListener.class, this);
        PlaierClient.getEventManager().add(LeaveGameSessionListener.class, this);

        this.taskOutputHud = new TaskOutputHud();

        this.taskOutputConsumer = new TaskOutputConsumer() {
            @Override
            public void info(String message) {
                taskOutputHud.addMessage(INFO_PREFIX + message);
            }

            @Override
            public void chatInfo(String message) {
                taskOutputHud.addMessage(INFO_PREFIX + message);
                Messenger.chat(INFO_PREFIX + message);
            }

            @Override
            public void fail(String message) {
                taskOutputHud.addMessage(FAIL_PREFIX + message);
                Messenger.chat(FAIL_PREFIX + message);
            }

            @Override
            public void success(String message) {
                taskOutputHud.addMessage(SUCCESS_PREFIX + message);
                Messenger.chat(SUCCESS_PREFIX + message);
            }
        };
    }

    /**
     * Starts a new task if no other task is currently running.
     * @param newTask the task to start
     * @return true if the task was started successfully
     */
    public boolean startTask(Task newTask) {
        if (task != null)
            return false;

        task = newTask;

        task.setOutput(taskOutputConsumer);
        taskOutputHud.enable();
        taskOutputHud.addMessage("§e[TASK STARTED]");
        outputHudTicksRemaining = -1; // Cancel any scheduled disabling.

        task.start();
        return true;
    }

    /**
     * Stops the task currently running, if there is one.
     * Also used to clean up after a task is done.
     * @return true if a task was stopped or cleaned, false if there was no task
     */
    public boolean stopTask() {
        if (task == null)
            return false;

        task.terminate();
        task = null;

        taskOutputHud.addMessage("§e[TASK ENDED]");
        outputHudTicksRemaining = 10 * 20; // 10 seconds
        return true;
    }

    /**
     * Every tick, checking if the task is done and if it is, cleaning up.
     * Also takes care of the outputHud scheduled disabling mechanic.
     */
    @Override
    public void onUpdate() {
        // If the task is done, clean
        if (task != null && task.isDone()) {
            stopTask();
        }

        // Decrementing outputHudTicksRemaining and when it reaches zero, hide the outputHud
        if (outputHudTicksRemaining > 0) {
            outputHudTicksRemaining--;
        } else if (outputHudTicksRemaining == 0) {
            outputHudTicksRemaining = -1;
            taskOutputHud.disable();
        }
    }

    /**
     * At the end of the session, we must clean up and shut down everything.
     */
    @Override
    public void onLeaveGameSession() {
        stopTask();
        taskOutputHud.disable();
        taskOutputHud.clear();
        outputHudTicksRemaining = -1;
    }

}
