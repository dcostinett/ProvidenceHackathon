package org.providence.hackathon.hackathon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

/**
 *
 */

public class BaseActivity extends AppCompatActivity {
    @Inject
    protected NetworkService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FeedbackApplication.getInstance().getAppComponent().inject(this);
    }
}
