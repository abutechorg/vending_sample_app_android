/*
 * Copyright 2016 MasterCard International.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * Neither the name of the MasterCard International Incorporated nor the names of its
 * contributors may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package com.mastercard.labs.unattended.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import bolts.TaskCompletionSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
