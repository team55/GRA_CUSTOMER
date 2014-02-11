package ru.team55.gra.api;


public interface BackgroundTaskCallback {

    void startProgress();
    void stopProgress(BackgroundTaskType type);

}
