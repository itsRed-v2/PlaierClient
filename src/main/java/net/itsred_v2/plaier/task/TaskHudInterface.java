package net.itsred_v2.plaier.task;

import java.util.List;

public interface TaskHudInterface {

    void info(String message);

    void chatInfo(String message);

    void fail(String message);

    void success(String message);

    void setInfoLines(List<String> lines);

}
