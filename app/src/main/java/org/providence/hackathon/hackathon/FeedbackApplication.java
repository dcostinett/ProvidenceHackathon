package org.providence.hackathon.hackathon;

import android.app.Application;

import org.providence.hackathon.hackathon.dagger.AppComponent;
import org.providence.hackathon.hackathon.dagger.Injection;

/**
 *
 */

public class FeedbackApplication extends Application {
    private static FeedbackApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Injection.create(this.getApplicationContext())
                .getComponent().inject(this);

        instance = this;
    }

    public AppComponent getAppComponent() {
        return Injection.instance().getComponent();
    }

    public static FeedbackApplication getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance is noll, onCreate not called");
        }
        return instance;
    }
}
