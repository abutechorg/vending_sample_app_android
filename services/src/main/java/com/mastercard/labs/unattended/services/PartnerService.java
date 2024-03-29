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

import android.content.Context;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mastercard.labs.unattended.services.dto.Approval;
import com.mastercard.labs.unattended.services.dto.FinalisedApproval;
import com.mastercard.labs.unattended.services.dto.Machine;
import com.mastercard.labs.unattended.services.request.ApprovalRequest;
import com.mastercard.labs.unattended.services.request.CreateSessionRequest;
import com.mastercard.labs.unattended.services.request.FinaliseApprovalRequest;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartnerService {
    private static final String TAG = PartnerService.class.getSimpleName();
    private final PartnerServiceDefinition partnerServiceDefinition;
    private final Gson gson;
    private final PersistentCookieJar cookieJar;
    private boolean loggedIn;

    private class ServiceErrorDeserialiser implements JsonDeserializer<ServiceException> {
        public ServiceException deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject errors = json.getAsJsonObject().getAsJsonObject("error");
            return new ServiceException(errors.get("code").getAsString(), errors.get("message").getAsString());
        }
    }

    public PartnerService(Context context, String baseUrl) {
        Log.d(TAG, "baseUrl: " + baseUrl);
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        partnerServiceDefinition = retrofit.create(PartnerServiceDefinition.class);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ServiceException.class, new ServiceErrorDeserialiser());
        gson = gsonBuilder.create();
    }


    public Task<List<Machine>> listNearbyMachines(final double latitude, final double longitude) {
        Log.d(TAG, "listNearbyMachines: lat:" + latitude + " long:" + longitude);
        final TaskCompletionSource<List<Machine>> tcs = new TaskCompletionSource<>();
        partnerServiceDefinition.listNearbyMachines(latitude, longitude).enqueue(new ServiceCallback(tcs, gson));

        return tcs.getTask();
    }

    public Task<Approval> createApproval(int amount, String payload) {
        final TaskCompletionSource<Approval> tcs = new TaskCompletionSource<>();
        ApprovalRequest approvalRequest = new ApprovalRequest(amount, payload);
        partnerServiceDefinition.createApproval(approvalRequest).enqueue(new ServiceCallback(tcs, gson));

        return tcs.getTask();
    }

    public Task<FinalisedApproval> finaliseApproval(String approvalId, String state, String payload) {
        final TaskCompletionSource<FinalisedApproval> tcs = new TaskCompletionSource<>();
        FinaliseApprovalRequest finaliseApprovalRequest = new FinaliseApprovalRequest(state, payload);
        partnerServiceDefinition.finaliseApproval(approvalId, finaliseApprovalRequest).enqueue(new ServiceCallback(tcs, gson));

        return tcs.getTask();
    }


    public boolean isLoggedIn() {
        return loggedIn;
    }

    public Task<ResponseBody> createSession(String accessToken, String refreshToken) {
        final TaskCompletionSource<ResponseBody> tcs = new TaskCompletionSource<>();
        partnerServiceDefinition.createSession(new CreateSessionRequest(accessToken, refreshToken)).enqueue(new ServiceCallback(tcs, gson));

        return tcs.getTask();
    }

    public Task<ResponseBody> logout() {
        final TaskCompletionSource<ResponseBody> tcs = new TaskCompletionSource<>();
        partnerServiceDefinition.logout().enqueue(new ServiceCallback(tcs, gson));
        tcs.getTask().continueWith(new Continuation<ResponseBody, ResponseBody>() {
            @Override
            public ResponseBody then(Task<ResponseBody> task) throws Exception {
                loggedIn = false;
                cookieJar.clearSession();

                return task.getResult();
            }
        });

        return tcs.getTask();
    }
}

