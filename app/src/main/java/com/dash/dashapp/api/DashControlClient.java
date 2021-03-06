package com.dash.dashapp.api;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.dash.dashapp.api.data.BudgetApiBudgetAnswer;
import com.dash.dashapp.api.data.BudgetApiBudgetHistoryAnswer;
import com.dash.dashapp.api.data.BudgetApiProposalAnswer;
import com.dash.dashapp.api.data.ChartRecord;
import com.dash.dashapp.api.data.DashBlogNews;
import com.dash.dashapp.api.data.DashControlExchange;
import com.dash.dashapp.api.data.DashControlMarketsAnswer;
import com.dash.dashapp.api.data.DashControlPricesAnswer;
import com.dash.dashapp.api.data.DashProposal;
import com.dash.dashapp.api.data.InsightResponse;
import com.dash.dashapp.api.service.DashBlogService;
import com.dash.dashapp.api.service.DashCentralService;
import com.dash.dashapp.api.service.DashControlService;
import com.dash.dashapp.api.service.DashInsightService;
import com.dash.dashapp.models.BudgetProposal;
import com.dash.dashapp.models.BudgetSummary;
import com.dash.dashapp.models.Exchange;
import com.dash.dashapp.models.Market;
import com.dash.dashapp.models.PortfolioEntry;
import com.dash.dashapp.utils.URLs;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashControlClient {

    private String TAG = DashControlClient.class.getSimpleName();

    private final DashBlogService dashBlogService;
    private final DashCentralService dashCentralService;
    private final DashControlService dashControlService;
    private final DashInsightService dashInsightService;

    private static final DashControlClient INSTANCE = new DashControlClient();

    private DashControlClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss Z")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.DASH_BLOG_API)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        dashBlogService = retrofit.create(DashBlogService.class);

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.DASH_CENTRAL_API)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        dashCentralService = retrofit.create(DashCentralService.class);

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_URL_DASH_CONTROL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        dashControlService = retrofit.create(DashControlService.class);

        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.DASH_INSIGHT_API)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        dashInsightService = retrofit.create(DashInsightService.class);
    }

    public static DashControlClient getInstance() {
        return INSTANCE;
    }

    public Call<List<DashBlogNews>> getBlogNews(int page) {
        return dashBlogService.blogNews(page);
    }

    public Call getBudget(final Callback<Pair<BudgetSummary, List<BudgetProposal>>> callback) {
        Call<BudgetApiBudgetAnswer> budgetCall = dashCentralService.budget();
        budgetCall.enqueue(new retrofit2.Callback<BudgetApiBudgetAnswer>() {
            @Override
            public void onResponse(@NonNull Call<BudgetApiBudgetAnswer> call,
                                   @NonNull Response<BudgetApiBudgetAnswer> response) {
                if (response.isSuccessful()) {
                    BudgetApiBudgetAnswer budgetApiBudgetAnswer = Objects.requireNonNull(response.body());
                    BudgetSummary budgetSummary = budgetApiBudgetAnswer.dashBudget.convert();
                    List<DashProposal> dashProposalList = budgetApiBudgetAnswer.proposals;
                    List<BudgetProposal> proposalList = new ArrayList<>();
                    for (DashProposal dashProposal : dashProposalList) {
                        proposalList.add(dashProposal.convert(false));
                    }
                    callback.onResponse(new Pair<>(budgetSummary, proposalList));
                } else {
                    callback.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BudgetApiBudgetAnswer> call,
                                  @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
        return budgetCall;
    }

    public Call getBudgetHistory(final Callback<List<BudgetProposal>> callback) {
        Call<BudgetApiBudgetHistoryAnswer> budgetHistoryCall = dashCentralService.budgetHistory();
        budgetHistoryCall.enqueue(new retrofit2.Callback<BudgetApiBudgetHistoryAnswer>() {
            @Override
            public void onResponse(@NonNull Call<BudgetApiBudgetHistoryAnswer> call,
                                   @NonNull Response<BudgetApiBudgetHistoryAnswer> response) {
                if (response.isSuccessful()) {
                    BudgetApiBudgetHistoryAnswer budgetApiBudgetHistoryAnswer = Objects.requireNonNull(response.body());
                    List<DashProposal> dashProposalList = budgetApiBudgetHistoryAnswer.proposals;
                    List<BudgetProposal> proposalList = new ArrayList<>();
                    for (DashProposal dashProposal : dashProposalList) {
                        proposalList.add(dashProposal.convert(true));
                    }
                    callback.onResponse(proposalList);
                } else {
                    callback.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BudgetApiBudgetHistoryAnswer> call,
                                  @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
        return budgetHistoryCall;
    }

    public void postProposalComment(String apiKey, String proposalHash, String commentId, String replyContent,
                            final Callback<String> callback) {
        Call<JsonObject> postProposalCommentCall = dashCentralService.postProposalComment(apiKey, replyContent, proposalHash, commentId);
        postProposalCommentCall.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call,
                                   @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(null);
                } else {
                    callback.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call,
                                  @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void getPrices(final Callback<List<Exchange>> callback) {
        Call<DashControlPricesAnswer> pricesCall = dashControlService.prices();
        pricesCall.enqueue(new retrofit2.Callback<DashControlPricesAnswer>() {
            @Override
            public void onResponse(@NonNull Call<DashControlPricesAnswer> call,
                                   @NonNull Response<DashControlPricesAnswer> response) {
                if (response.isSuccessful()) {
                    DashControlPricesAnswer body = Objects.requireNonNull(response.body());
                    List<Exchange> result = new ArrayList<>();
                    for (DashControlExchange dcExchange : body.getIntlExchanges()) {
                        result.add(dcExchange.convert());
                    }
                    callback.onResponse(result);
                } else {
                    callback.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashControlPricesAnswer> call,
                                  @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void getDefaultMarket(final Callback<DashControlMarketsAnswer.DefaultMarket> callback) {
        Call<DashControlMarketsAnswer> marketsCall = dashControlService.markets();
        marketsCall.enqueue(new retrofit2.Callback<DashControlMarketsAnswer>() {
            @Override
            public void onResponse(@NonNull Call<DashControlMarketsAnswer> call,
                                   @NonNull Response<DashControlMarketsAnswer> response) {
                if (response.isSuccessful()) {
                    DashControlMarketsAnswer body = Objects.requireNonNull(response.body());
                    callback.onResponse(body.defaultMarket);
                } else {
                    callback.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashControlMarketsAnswer> call,
                                  @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void getMarkets(final Callback<List<Exchange>> callback) {
        Call<DashControlMarketsAnswer> marketsCall = dashControlService.markets();
        marketsCall.enqueue(new retrofit2.Callback<DashControlMarketsAnswer>() {
            @Override
            public void onResponse(@NonNull Call<DashControlMarketsAnswer> call,
                                   @NonNull Response<DashControlMarketsAnswer> response) {
                if (response.isSuccessful()) {
                    DashControlMarketsAnswer body = Objects.requireNonNull(response.body());
                    DashControlMarketsAnswer.DefaultMarket defaultMarket = body.defaultMarket;
                    List<Exchange> result = new ArrayList<>();
                    Map<String, Set<String>> byExchangeMap = body.getByExchange();
                    for (Map.Entry<String, Set<String>> exchangeEntry : byExchangeMap.entrySet()) {
                        Exchange exchange = new Exchange();
                        exchange.name = exchangeEntry.getKey();
                        exchange.markets = new RealmList<>();
                        boolean isDefaultExchange = false;
                        for (String marketName : exchangeEntry.getValue()) {
                            Market market = new Market();
                            market.name = marketName;
                            market.isDefault = exchange.name.equals(defaultMarket.exchange)
                                    && market.name.equals(defaultMarket.market);
                            if (market.isDefault) {
                                isDefaultExchange = true;
                                exchange.markets.add(0, market);
                            } else {
                                exchange.markets.add(market);
                            }
                        }
                        if (isDefaultExchange) {
                            result.add(0, exchange);
                        } else {
                            result.add(exchange);
                        }
                    }
                    callback.onResponse(result);
                } else {
                    callback.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashControlMarketsAnswer> call,
                                  @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public Call<List<ChartRecord>> getChartData(boolean noLimit, String exchange, String market, long startDateMs, long endDateMs) {
        return dashControlService.chartData(noLimit, exchange, market, startDateMs / 1000, endDateMs / 1000);
    }

    public Call<BudgetApiProposalAnswer> getProposalDetails(String proposalHash) {
        return dashCentralService.proposalDetails(proposalHash);
    }

    public Call<List<InsightResponse>> getUtxos(List<PortfolioEntry> portfolioEntries) {
        StringBuilder addrsBuilder = new StringBuilder();
        for (PortfolioEntry entry : portfolioEntries) {
            if (addrsBuilder.length() > 0) {
                addrsBuilder.append(",");
            }
            addrsBuilder.append(entry.pubKey);
        }
        String addrs = addrsBuilder.toString();
        return dashInsightService.utxos(addrs);
    }

    public interface Callback<T> {

        void onResponse(T response);

        void onFailure(Throwable t);
    }
}
