package net.itsred_v2.plaier.task;

public interface TaskOutputConsumer {

    void info(String message);

    void chatInfo(String message);

    void fail(String message);

    void success(String message);

}
