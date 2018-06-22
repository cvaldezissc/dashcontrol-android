package com.dash.dashapp.api.service;

import com.dash.dashapp.api.data.BudgetApiBudgetAnswer;
import com.dash.dashapp.api.data.BudgetApiProposalAnswer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DashCentralService {

    @GET("budget")
    Call<BudgetApiBudgetAnswer> proposals();

    @GET("proposal")
    Call<BudgetApiProposalAnswer> proposalDetails(
            @Query("hash") String proposalHash);
}