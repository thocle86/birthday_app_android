package fr.cefim.android.birthday_app.utils;

// TODO : comprendre cette interface
public interface ApiCallback {

    void fail(String json);
    void success(String json);
}
