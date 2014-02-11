package ru.team55.gra.api.users;

public interface UserAccountsCallback {

    //TODO: Переделать потом для совместимости
    void onSuccess();
    void onError(int code, String Message);

}