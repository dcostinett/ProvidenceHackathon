package org.providence.hackathon.hackathon.dagger;

import android.content.Context;


/**
 */

public class Injection {
    private AppComponent appStateComponent;
    private static Injection instance;

    private Injection(Context context) {
        appStateComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(context)).build();
    }

    public static Injection create(Context context) {
        if (instance == null) {
            instance = new Injection(context);
        }
        return instance;
    }

    public static Injection instance() { return instance; }

    public AppComponent getComponent() { return appStateComponent; }
}