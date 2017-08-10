package org.providence.hackathon.hackathon;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.providence.hackathon.hackathon.model.ApiResponse;
import org.providence.hackathon.hackathon.model.AudioFeedback;
import org.providence.hackathon.hackathon.model.ImageFeedback;
import org.providence.hackathon.hackathon.model.TextFeedback;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */

public class NetworkService {
    private static final String TAG = NetworkService.class.getSimpleName();
    private static final String BASE_URL = "https://zvmyujgt49.execute-api.us-west-2.amazonaws.com";

    private CustomerFeedbackClient mClient;

    private OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder().header("Content-Type", "application/json");

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }
    }).build();

    public NetworkService() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        Retrofit retrofit = builder.build();
        mClient = retrofit.create(CustomerFeedbackClient.class);
    }

    public interface CustomerFeedbackClient {
        @POST("/test/text")
        Observable<Response<ApiResponse>> sendTextFeedback(@Body TextFeedback feedback);

        @POST("/test/photo")
        Observable<Response<ApiResponse>> sendImageFeedback(@Part MultipartBody.Part image, @Part("name") RequestBody name);

        @POST("/test/voice")
        Observable<Response<ApiResponse>> sendAudioFeedback(@Part MultipartBody.Part image, @Part("name") RequestBody name);
    }

    public void postTextFeedback(final Context context, final TextFeedback feedback) {
        mClient.sendTextFeedback(feedback)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Subscriber<retrofit2.Response>() {

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "postTextFeedback onComplete");
                        Intent intent = new Intent();
                        intent.setAction(CustomerFeedbackActivity.TEXT_FEEDBACK_SENT_ACTION);
                        context.sendBroadcast(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "postTextFeedback onError");
                    }

                    @Override
                    public void onNext(Response response) {
                        Log.d(TAG, "postTextFeedback onNext");
                    }
                });
    }

    public void postAudioFeedback(final Context context, final AudioFeedback feedback) {
        mClient.sendAudioFeedback(feedback.audio, feedback.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Subscriber<Response<ApiResponse>>() {
                   @Override
                   public void onCompleted() {
                       Log.d(TAG, "postAudioFeedback onComplete");
                       Intent intent = new Intent();
                       intent.setAction(CustomerFeedbackActivity.AUDIO_FEEDBACK_SENT_ACTION);
                       context.sendBroadcast(intent);
                   }

                   @Override
                   public void onError(Throwable e) {
                       Log.d(TAG, "Error posting audio");
                   }

                   @Override
                   public void onNext(Response<ApiResponse> apiResponseResponse) {

                   }
                });
    }

    public void postImageFeedback(final Context context, final ImageFeedback feedback) {
        mClient.sendImageFeedback(feedback.image, feedback.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<ApiResponse>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "postImageFeedback onComplete");
                        Intent intent = new Intent();
                        intent.setAction(CustomerFeedbackActivity.IMAGE_FEEDBACK_SENT_ACTION);
                        context.sendBroadcast(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Error posting image");
                    }

                    @Override
                    public void onNext(Response<ApiResponse> apiResponseResponse) {

                    }
                });
    }
}
