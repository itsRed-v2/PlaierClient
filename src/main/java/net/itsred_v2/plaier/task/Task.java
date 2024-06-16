package net.itsred_v2.plaier.task;

public abstract class Task {

    public TaskOutputConsumer output;

    public abstract void start();

    public abstract void terminate();

    public abstract boolean isDone();

    public void setOutput(TaskOutputConsumer output) {
        this.output = output;
    }

}
