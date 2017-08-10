package org.providence.hackathon.hackathon.dagger;

import android.content.Context;

import org.providence.hackathon.hackathon.NetworkService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 *
 */

@Module
public class AppModule {

    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public NetworkService provideNetworkService() {
        return new NetworkService();
    }
}
