package ru.team55.network;

public interface NetworkReadyCallback {
    void Ready();
    void OnError(int code, String error);
}
