package org.providence.hackathon.hackathon;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.providence.hackathon.hackathon.model.ImageFeedback;
import org.providence.hackathon.hackathon.model.TextFeedback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CustomerFeedbackActivity extends BaseActivity implements LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String EXTRA_USER_EMAIL = "userEmail";
    public static final String EXTRA_FEEDBACK_RESULT = "feedbackResult";
    public static final String TEXT_FEEDBACK_SENT_ACTION = "textFeedbackSent";
    public static final String IMAGE_FEEDBACK_SENT_ACTION = "photoFeedbackSent";
    public static final String AUDIO_FEEDBACK_SENT_ACTION = "audioFeedbackSent";

    private static final String TAG = CustomerFeedbackActivity.class.getSimpleName();
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int IMAGE_CAPTURE_REQUEST_CODE = 200;
    private static final int AUDIO_RECORD_REQUEST_CODE = 300;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 400;

    private static final long ONE_MINUTE = 60 * 1000;
    private static final float MIN_DISTANCE = 250; // meters

    ByteArrayOutputStream mBytes = new ByteArrayOutputStream();

    private LocationManager locationManager;
    private Location lastLocation;

    @BindView(R.id.btnTypeFeedback)
    Button btnTextFeedback;

    @BindView(R.id.btnTakePhoto)
    Button btnPhotoFeedback;

    @BindView(R.id.btnRecordAudio)
    Button btnAudioFeedback;

    @BindView(R.id.feedbackText)
    EditText mTextFeedback;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    public static Intent buildIntent(Context context, String email) {
        Intent intent = new Intent(context, CustomerFeedbackActivity.class);
        intent.putExtra(EXTRA_USER_EMAIL, email);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        if (savedInstanceState == null) {
            String userEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);
            // save email, determine if manager access allowed, etc.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.show_management_activity:
                startManagementActivity();
                return true;
        }

        return false;
    }

    private void startManagementActivity() {
        Intent managementIntent = FeedbackItemListActivity.buildIntent(this);
        startActivity(managementIntent);
    }

    @OnClick(R.id.btnTypeFeedback)
    public void onTypeFeedbackClicked() {
        mTextFeedback.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        mTextFeedback.requestFocus();
        mTextFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTextFeedback.getText().length() > 0) {
                    btnSubmit.setEnabled(true);
                } else {
                    btnSubmit.setEnabled(false);
                }
            }
        });
    }

    @OnClick(R.id.btnTakePhoto)
    public void onTakePhotoClicked() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA}, IMAGE_CAPTURE_REQUEST_CODE);
        } else {
            doImageCapture();
        }
    }

    private void doImageCapture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
        }
    }

    @OnClick(R.id.btnRecordAudio)
    public void onRecordAudioClicked() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_RECORD_REQUEST_CODE);
        } else {
            doRecordAudio();
        }
    }


    private void doRecordAudio() {
        Intent intent = new Intent(this, RecordingActivity.class);
        startActivityForResult(intent, AUDIO_RECORD_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap thumbnail = (Bitmap) extras.get("data");
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, mBytes);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
            } else {
                doImageUpload(mBytes);
            }
        }
    }

    private void doImageUpload(ByteArrayOutputStream bytes) {
        File feedbackImage = new File(Environment.getExternalStorageDirectory(), "feedbackImage.jpg");
        FileOutputStream fo;
        try {
            fo = new FileOutputStream(feedbackImage);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Image captured");
        // now upload bitmap to server

        RequestBody feedbackRequest = RequestBody.create(MediaType.parse("image/jpeg"), feedbackImage);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", feedbackImage.getName(), feedbackRequest);
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), "feedback_image");

        service.postImageFeedback(this, new ImageFeedback(body, name));
    }

    @OnClick(R.id.btnSubmit)
    public void onSubmitClicked() {
        String theFeedback = mTextFeedback.getText().toString();
        TextFeedback textFeedback = new TextFeedback(theFeedback);

        service.postTextFeedback(this, textFeedback);
    }

    @Override
    protected void onStart() {
        super.onStart();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocation();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TEXT_FEEDBACK_SENT_ACTION);
        filter.addAction(AUDIO_FEEDBACK_SENT_ACTION);
        filter.addAction(IMAGE_FEEDBACK_SENT_ACTION);
        registerReceiver(feedbackCompletedReceiver, filter);
    }

    @Override
    protected void onStop() {
        locationManager.removeUpdates(this);
        locationManager = null;

        unregisterReceiver(feedbackCompletedReceiver);

        super.onStop();
    }

    private void startLocation() {
        Location lastKnownLocation = requestLastKnownLocation();
        lastLocation = LocationUtils.isBetterLocation(lastKnownLocation, lastLocation) ? lastKnownLocation : lastLocation;

        // The location may be really old, so only use it to populate the UI while user waits for
        // a fresh location, if it is acceptable.
        if (LocationUtils.isLocationAcceptable(lastLocation)) {
            updateLocation(lastLocation);
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

        if (isLocationServiceEnabled()) {

            // Request updates every 60 sec minimum, or if user moves 250 meter minimum.
            // Null indicates not providing a secondary thread to send updates too. If you have
            // complex logic that is performed on update, you should provide a looper, or use
            // pending requests.
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(ONE_MINUTE, MIN_DISTANCE, criteria, this, null);
            }

            // If you only want a single location update, this method is better...
            // locationManager.requestSingleUpdate(criteria, this, null);
        }
    }

    private boolean isLocationServiceEnabled() {
        List<String> providers = locationManager.getProviders(true);
        return providers.size() > 0;
    }

    private Location requestLastKnownLocation() {
        // Get all providers, both enabled and disabled
        List<String> providers = locationManager.getProviders(false);
        Location bestLocation = null;

        // Loop over all of the providers, GPS, NETWORK, etc looking for the best location.
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            bestLocation = LocationUtils.isBetterLocation(location, bestLocation) ? location : bestLocation;
        }

        Log.d(TAG, "requestLastKnownLocation: " + bestLocation);

        return bestLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
            } else {
                // Permission request was denied.
                Snackbar.make(btnTextFeedback.getRootView(), "Location permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == AUDIO_RECORD_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doRecordAudio();
            }
        } else if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doImageCapture();
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doImageUpload(mBytes);
            }
        }
    }

    private void updateLocation(Location location) {
        lastLocation = location;
        LocationUtils.saveLastLocation(this, location);
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationUtils.saveLastLocation(this, location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    BroadcastReceiver feedbackCompletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int result = intent.getIntExtra(EXTRA_FEEDBACK_RESULT, Activity.RESULT_CANCELED);
            final String action = intent.getAction();

            switch (action) {
                case TEXT_FEEDBACK_SENT_ACTION:
                    mTextFeedback.setText("");
                    mTextFeedback.setVisibility(View.INVISIBLE);
                    btnSubmit.setVisibility(View.INVISIBLE);
                    Toast.makeText(CustomerFeedbackActivity.this, "Feedback sent to server", Toast.LENGTH_LONG).show();
                    break;
                case AUDIO_FEEDBACK_SENT_ACTION:
                    Toast.makeText(CustomerFeedbackActivity.this, "Audio feedback sent", Toast.LENGTH_LONG).show();
                    break;
                case IMAGE_FEEDBACK_SENT_ACTION:
                    Toast.makeText(CustomerFeedbackActivity.this, "Image feedback sent", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
}
