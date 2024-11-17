package net.itsred_v2.plaier.task;

public abstract class Task {

    public TaskHudInterface output;

    public abstract void start();

    public abstract void terminate();

    public abstract boolean isDone();

    public void setOutput(TaskHudInterface output) {
        this.output = output;
    }

}
