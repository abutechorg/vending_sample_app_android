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

import com.mastercard.labs.unattended.services.dto.Approval;
import com.mastercard.labs.unattended.services.dto.Card;
import com.mastercard.labs.unattended.services.dto.FinalisedApproval;
import com.mastercard.labs.unattended.services.dto.Machine;
import com.mastercard.labs.unattended.services.request.ApprovalRequest;
import com.mastercard.labs.unattended.services.request.CreateSessionRequest;
import com.mastercard.labs.unattended.services.request.FinaliseApprovalRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PartnerServiceDefinition {
    @GET("api/machines")
    Call<List<Machine>> listNearbyMachines(@Query("latitude") double latitude, @Query("longitude") double longitude);

    @POST("api/fundReservations")
    Call<Approval> createApproval(@Body ApprovalRequest approvalRequest);

    @PUT("api/fundReservations/{id}")
    Call<FinalisedApproval> finaliseApproval(@Path("id") String id, @Body FinaliseApprovalRequest finaliseApprovalRequest);

    @GET("api/cards")
    Call<List<Card>> listCards();

    @POST("api/logout")
    Call<ResponseBody> logout();

    @POST("api/session")
    Call<ResponseBody> createSession(@Body CreateSessionRequest createSessionRequest);
}
