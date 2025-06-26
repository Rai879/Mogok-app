package com.example.mogok.api;

import com.example.mogok.models.TempTransactionItem;
import com.example.mogok.models.Transaction;
import com.example.mogok.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiConfig {

    String BASE_URL = "http://10.0.2.2:8000/api/";

    // Auth
    @POST("register")
    Call<AuthResponse> registerUser(@Body Map<String, String> body);

    @POST("login")
    Call<AuthResponse> loginUser(@Body Map<String, String> body);

    @POST("logout")
    Call<ResponseBody> logoutUser(@Header("Authorization") String token);

    // Parts
    @GET("parts/search")
    Call<List<com.example.mogok.models.Part>> searchParts(@Query("query") String query);

    @GET("parts/by-barcode/{barcode}")
    Call<com.example.mogok.models.Part> getPartByBarcode(@Path("barcode") String barcode);

    // Temp Transactions (Cart)
    @GET("transactions/temp")
    Call<TempTransactionsResponse> getTempTransactions();

    @POST("transactions/add-to-cart")
    Call<AddToCartResponse> addToCart(@Body Map<String, Object> body);

    @PATCH("transactions/update-cart-quantity/{tempTransactionId}")
    Call<TempTransactionItem> updateCartQuantity(@Path("tempTransactionId") int tempTransactionId, @Body Map<String, Integer> body);

    @DELETE("transactions/remove-from-cart/{tempTransactionId}")
    Call<ResponseBody> removeFromCart(@Path("tempTransactionId") int tempTransactionId);

    // Process Transaction
    @POST("transactions/process")
    Call<ProcessTransactionResponse> processTransaction(@Body Map<String, Double> body);

    // Transaction History
    @GET("transactions/history")
    Call<TransactionHistoryResponse> getTransactionHistory(@Header("Authorization") String token, @Query("page") int page);

    @GET("transactions/{transactionId}")
    Call<Transaction> getTransactionDetails(@Header("Authorization") String token, @Path("transactionId") int transactionId);

    // Response models (nested classes or separate files)
    class AuthResponse {
        public String message;
        public String access_token;
        public String token_type;
        public User user;
    }

    class AddToCartResponse {
        public String message;
        public TempTransactionItem cart_item;
    }

    class TempTransactionsResponse {
        public List<TempTransactionItem> items;
        public double total_amount;
    }

    class ProcessTransactionResponse {
        public String message;
        public double change_due;
    }

    class TransactionHistoryResponse {
        public List<Transaction> data; // Laravel's paginate returns 'data'
        public int current_page;
        public int last_page;
        // Add other pagination fields as needed
    }
}