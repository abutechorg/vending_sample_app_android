package com.mastercard.labs.unattended.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import bolts.TaskCompletionSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by douglas on 10/8/16.
 */
public class ServiceCallback<T> implements Callback<T> {
    private static final String TAG = ServiceCallback.class.getSimpleName();
    private final Gson gson;

    private final TaskCompletionSource<T> taskCompletionSource;

    public ServiceCallback(TaskCompletionSource<T> taskCompletionSource, Gson gson) {
        this.taskCompletionSource = taskCompletionSource;
        this.gson = gson;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        T result = response.body();
        if(response.isSuccessful()){
            taskCompletionSource.setResult(result);
        }
        else{
            try {
                ServiceException serviceException = gson.fromJson(response.errorBody().string(), ServiceException.class);
                taskCompletionSource.setError(serviceException);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                taskCompletionSource.setError(new Exception("Failed to read error object from response", e));
            } catch (IllegalStateException ex){
                Log.e(TAG, "onResponse: ", ex);
            }
            catch (JsonSyntaxException e){
                Log.e(TAG, e.getMessage(), e);
                taskCompletionSource.setError(new ServiceException(response.code(), response.message()));
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        taskCompletionSource.setError(new Exception(t));
    }
}
