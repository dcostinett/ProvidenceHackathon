package org.providence.hackathon.hackathon;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.common.annotation.KeepForSdkWithFieldsAndMethods;

import org.providence.hackathon.hackathon.model.ApiResponse;
import org.providence.hackathon.hackathon.model.AudioFeedback;
import org.providence.hackathon.hackathon.model.FeedbackItem;
import org.providence.hackathon.hackathon.model.Hits;
import org.providence.hackathon.hackathon.model.ImageFeedback;
import org.providence.hackathon.hackathon.model.SearchCriteria;
import org.providence.hackathon.hackathon.model.SearchResponse;
import org.providence.hackathon.hackathon.model.TextFeedback;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.providence.hackathon.hackathon.FeedbackItemDetailFragment.EXTRA_FEEDBACK_OBJECT;

/**
 *
 */

public class NetworkService {
    private static final String TAG = NetworkService.class.getSimpleName();
    public static final String BASE_URL = "https://zvmyujgt49.execute-api.us-west-2.amazonaws.com/";
    public static final String BASE_SEARCH_URL =
            "https://search-floop-object-index-hjvdwrieeinfqs7qztompbegcq.us-west-2.es.amazonaws.com/";

    private CustomerFeedbackClient mClient;
    private ElasticSearch searchClient;

    private OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder().header("Content-Type", "multipart/form-data");

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

        Retrofit.Builder searchBuilder = new Retrofit.Builder()
                .baseUrl(BASE_SEARCH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        Retrofit search = searchBuilder.build();

        searchClient = search.create(ElasticSearch.class);
    }

    public interface ElasticSearch {
        @Headers("Content-Type: application/json")
        @POST("/metadata-store/_search")
        Observable<Response<SearchResponse>> getFeedbackItems(@Body SearchCriteria search);
    }

    public interface CustomerFeedbackClient {
        @PUT("/test/text/{id}")
        Observable<Response<ApiResponse>> sendTextFeedback(@Path("id") String name, @Body TextFeedback feedback);

        @Multipart
        @PUT("/test/photo/{id}")
        Observable<Response<ApiResponse>> sendImageFeedback(@Path("id") String fileName, @Part MultipartBody.Part image, @Part("name") RequestBody name);

        @Multipart
        @PUT("/test/voice/{id}")
        Observable<Response<ApiResponse>> sendAudioFeedback(@Path("id") String fileName, @Part MultipartBody.Part audio, @Part("name") RequestBody name);

        @GET("/test/voice/{id}")
        Observable<Response<FeedbackItem>> getAudioDetail(@Path("id") String objectKey);

        @GET("/test/text/{id}")
        Observable<Response<FeedbackItem>> getTextDetail(@Path("id") String objectKey);
    }

    public void getFeedbackItems(final Context context, SearchCriteria search) {
        searchClient.getFeedbackItems(search)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<SearchResponse>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "getFeedbackItems onComplete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Response<SearchResponse> listResponse) {
                        Intent intent = new Intent();
                        intent.setAction(FeedbackItemListActivity.FEEDBACK_LIST_RETRIEVED_ACTION);
                        if (listResponse.body() != null) {
                            intent.putParcelableArrayListExtra(FeedbackItemListActivity.LIST_KEY,
                                    new ArrayList<Parcelable>(listResponse.body().hits.hits));
                        }
                        context.sendBroadcast(intent);
                    }
                });
    }

    public void postTextFeedback(final Context context, final TextFeedback feedback) {
        String timestamp = String.valueOf(new Date().getTime()) + ".txt";
        mClient.sendTextFeedback(timestamp, feedback)
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
                        Log.d(TAG, "postTextFeedback onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Response response) {
                        Log.d(TAG, "postTextFeedback onNext");
                    }
                });
    }

    public void postAudioFeedback(final Context context, final AudioFeedback feedback) {
        String timestamp = String.valueOf(new Date().getTime()) + ".mp3";
        mClient.sendAudioFeedback(timestamp, feedback.audio, feedback.name)
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
                       Log.d(TAG, "Error posting audio: " + e.getMessage());
                   }

                   @Override
                   public void onNext(Response<ApiResponse> apiResponseResponse) {

                   }
                });
    }

    public void postImageFeedback(final Context context, final ImageFeedback feedback) {
        String timestamp = String.valueOf(new Date().getTime()) + ".jpg";
        mClient.sendImageFeedback(timestamp, feedback.image, feedback.name)
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
                        Log.d(TAG, "Error posting image: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Response<ApiResponse> apiResponseResponse) {

                    }
                });
    }
}
