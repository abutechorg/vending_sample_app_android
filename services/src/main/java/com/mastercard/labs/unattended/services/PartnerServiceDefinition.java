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

/**
 * Created by douglas on 8/8/16.
 */
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
