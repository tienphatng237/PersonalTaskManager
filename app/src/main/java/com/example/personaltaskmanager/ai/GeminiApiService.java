package com.example.personaltaskmanager.ai;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Gemini API Service
 * -----------------------
 * Service để gọi Google Gemini API với các tính năng AI:
 * - Complete: Tự động hoàn thiện text
 * - Improve: Cải thiện văn bản
 * - Summarize: Tóm tắt nội dung
 * - Expand: Mở rộng ý tưởng
 */
public class GeminiApiService {

    private static final String TAG = "GeminiApiService";
    // Google AI Studio API base URL
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    // Model name - using gemini-2.5-flash (latest stable model)
    // Reference: https://docs.cloud.google.com/vertex-ai/generative-ai/docs/start/quickstart
    // Alternative models: gemini-2.5-pro, gemini-1.5-flash, gemini-1.5-pro
    private static final String MODEL_NAME = "gemini-2.5-flash";
    
    // ============================================================
    // API KEY - Thay thế bằng API key của bạn từ Google AI Studio
    // Lấy tại: https://aistudio.google.com/app/api-keys
    // ============================================================
    private static final String GEMINI_API_KEY = "YOUR_API_KEY_HERE";
    
    private static GeminiApiService instance;
    private GeminiApiInterface apiInterface;

    private GeminiApiService(Context context) {
        // API key is hardcoded in GEMINI_API_KEY constant above
        
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Content-Type", "application/json");
                        return chain.proceed(requestBuilder.build());
                    }
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiInterface = retrofit.create(GeminiApiInterface.class);
    }

    public static synchronized GeminiApiService getInstance(Context context) {
        if (instance == null) {
            instance = new GeminiApiService(context);
        }
        return instance;
    }

    /**
     * Get API key from hardcoded constant
     */
    private String getApiKey() {
        if (GEMINI_API_KEY == null || GEMINI_API_KEY.isEmpty() || GEMINI_API_KEY.equals("YOUR_API_KEY_HERE")) {
            Log.e(TAG, "API key chưa được cấu hình! Vui lòng thay thế GEMINI_API_KEY trong code.");
            return "";
        }
        return GEMINI_API_KEY;
    }

    /**
     * Complete text - tự động hoàn thiện văn bản
     */
    public void completeText(String prompt, GeminiCallback callback) {
        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            callback.onError("API key chưa được cấu hình trong code. Vui lòng cập nhật GEMINI_API_KEY.");
            return;
        }

        String fullPrompt = prompt + "\n\nTiếp tục viết:";
        generateContent(fullPrompt, apiKey, callback);
    }

    /**
     * Improve text - cải thiện văn bản
     */
    public void improveText(String text, GeminiCallback callback) {
        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            callback.onError("API key chưa được cấu hình trong code. Vui lòng cập nhật GEMINI_API_KEY.");
            return;
        }

        String prompt = "Cải thiện và làm cho văn bản sau đây trở nên rõ ràng, chuyên nghiệp và dễ hiểu hơn:\n\n" + text;
        generateContent(prompt, apiKey, callback);
    }

    /**
     * Summarize text - tóm tắt nội dung
     */
    public void summarizeText(String text, GeminiCallback callback) {
        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            callback.onError("API key chưa được cấu hình trong code. Vui lòng cập nhật GEMINI_API_KEY.");
            return;
        }

        String prompt = "Tóm tắt ngắn gọn nội dung sau đây:\n\n" + text;
        generateContent(prompt, apiKey, callback);
    }

    /**
     * Expand text - mở rộng ý tưởng
     */
    public void expandText(String text, GeminiCallback callback) {
        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            callback.onError("API key chưa được cấu hình trong code. Vui lòng cập nhật GEMINI_API_KEY.");
            return;
        }

        String prompt = "Mở rộng và phát triển ý tưởng sau đây một cách chi tiết:\n\n" + text;
        generateContent(prompt, apiKey, callback);
    }

    /**
     * Generate content using Gemini API
     */
    private void generateContent(String prompt, String apiKey, GeminiCallback callback) {
        GeminiRequest request = new GeminiRequest();
        request.contents = new GeminiRequest.Content[]{
                new GeminiRequest.Content(
                        new GeminiRequest.Part[]{
                                new GeminiRequest.Part(prompt)
                        }
                )
        };

        // Try multiple models in order (based on official documentation)
        // Reference: https://docs.cloud.google.com/vertex-ai/generative-ai/docs/start/quickstart
        String[] modelsToTry = {"gemini-2.5-flash", "gemini-1.5-flash", "gemini-1.5-pro", "gemini-pro"};
        tryGenerateContent(modelsToTry, 0, apiKey, request, callback);
    }
    
    /**
     * Try generating content with different models until one works
     */
    private void tryGenerateContent(String[] models, int index, String apiKey, 
                                   GeminiRequest request, GeminiCallback callback) {
        if (index >= models.length) {
            callback.onError("Không tìm thấy model phù hợp. Vui lòng kiểm tra API key.");
            return;
        }
        
        String currentModel = models[index];
        Log.d(TAG, "Trying model: " + currentModel);
        
        retrofit2.Call<GeminiResponse> call = apiInterface.generateContent(
                currentModel,
                apiKey,
                request
        );

        call.enqueue(new retrofit2.Callback<GeminiResponse>() {
            @Override
            public void onResponse(retrofit2.Call<GeminiResponse> call, retrofit2.Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GeminiResponse body = response.body();
                    if (body.candidates != null && body.candidates.length > 0) {
                        GeminiResponse.Candidate candidate = body.candidates[0];
                        if (candidate.content != null && candidate.content.parts != null 
                                && candidate.content.parts.length > 0) {
                            String result = candidate.content.parts[0].text;
                            Log.d(TAG, "Success with model: " + currentModel);
                            callback.onSuccess(result);
                            return;
                        }
                    }
                }
                
                // If 404, try next model
                if (response.code() == 404) {
                    Log.w(TAG, "Model " + currentModel + " not found (404), trying next model...");
                    tryGenerateContent(models, index + 1, apiKey, request, callback);
                    return;
                }
                
                // Handle other error responses
                String errorMsg = "Không thể tạo nội dung. Vui lòng thử lại.";
                if (response.errorBody() != null) {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "API Error: " + errorBody);
                        if (errorBody.contains("error")) {
                            errorMsg = "Lỗi API: " + errorBody;
                        } else {
                            errorMsg = errorBody;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                } else {
                    Log.e(TAG, "Response code: " + response.code());
                    errorMsg = "Lỗi từ server (Code: " + response.code() + ")";
                }
                callback.onError(errorMsg);
            }

            @Override
            public void onFailure(retrofit2.Call<GeminiResponse> call, Throwable t) {
                Log.e(TAG, "API call failed for model " + currentModel, t);
                // Try next model on failure
                tryGenerateContent(models, index + 1, apiKey, request, callback);
            }
        });
    }

    /**
     * API Interface
     * Using Google AI Studio REST API endpoint format
     * Reference: https://aistudio.google.com/app/api-keys
     * Reference: https://docs.cloud.google.com/vertex-ai/generative-ai/docs/start/quickstart
     * Using v1 API (as per official documentation)
     */
    private interface GeminiApiInterface {
        @POST("v1/models/{model}:generateContent")
        retrofit2.Call<GeminiResponse> generateContent(
                @retrofit2.http.Path("model") String model,
                @retrofit2.http.Query("key") String apiKey,
                @Body GeminiRequest request
        );
    }

    /**
     * Request model
     */
    static class GeminiRequest {
        Content[] contents;

        static class Content {
            Part[] parts;

            Content(Part[] parts) {
                this.parts = parts;
            }
        }

        static class Part {
            String text;

            Part(String text) {
                this.text = text;
            }
        }
    }

    /**
     * Response model
     */
    static class GeminiResponse {
        Candidate[] candidates;

        static class Candidate {
            Content content;
        }

        static class Content {
            Part[] parts;
        }

        static class Part {
            String text;
        }
    }

    /**
     * Callback interface
     */
    public interface GeminiCallback {
        void onSuccess(String result);
        void onError(String error);
    }
}

