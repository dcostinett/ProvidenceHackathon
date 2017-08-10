package org.providence.hackathon.hackathon.dagger;

import org.providence.hackathon.hackathon.BaseActivity;
import org.providence.hackathon.hackathon.FeedbackApplication;

import javax.inject.Singleton;

import dagger.Component;

/**
 *
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(BaseActivity activity);

    void inject(FeedbackApplication application);
}
