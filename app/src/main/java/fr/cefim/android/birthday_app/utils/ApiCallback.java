package fr.cefim.android.birthday_app.utils;

public interface ApiCallback {

    void onFailure(String json);

    void onResponseSuccess(String json);

    void onResponseFail(String json);

}
