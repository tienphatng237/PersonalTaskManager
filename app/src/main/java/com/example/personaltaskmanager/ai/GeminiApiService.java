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
 * Gemini API Service - Đã tối ưu Prompt và Xử lý loại bỏ nội dung thừa
 */
public class GeminiApiService {

    private static final String TAG = "GeminiApiService";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    
    // Model và API Key theo yêu cầu
    private static final String MODEL_NAME = "gemini-2.5-flash-lite";
    private static final String GEMINI_API_KEY = "YOUR API KEY";
    
    private static GeminiApiService instance;
    private GeminiApiInterface apiInterface;

    private GeminiApiService(Context context) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Content-Type", "application/json");
                    return chain.proceed(requestBuilder.build());
                })
                .connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(7, TimeUnit.SECONDS)
                .writeTimeout(7, TimeUnit.SECONDS)
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

    public void completeText(String prompt, GeminiCallback callback) {
        String fullPrompt = "Nhiệm vụ: Hãy hoàn thiện và sửa lỗi chính tả, thêm dấu tiếng Việt cho câu sau đây. " +
                "Yêu cầu: CHỈ trả về duy nhất nội dung đã được sửa, tuyệt đối không lặp lại câu gốc, không thêm lời dẫn hay giải thích.\n\n" +
                "Dữ liệu: \"" + prompt + "\"";
        generateContent(fullPrompt, prompt, callback);
    }

    /**
     * Cải thiện: Làm cho câu từ hay hơn, chi tiết và chuyên nghiệp hơn (gợi ý thêm thông tin thực tế).
     */
    public void improveText(String text, GeminiCallback callback) {
        String prompt = "Nhiệm vụ: Cải thiện nội dung sau để chuyên nghiệp và chi tiết hơn. " +
                "Nếu nội dung về kỹ thuật, hãy gợi ý thêm công cụ hoặc thuộc tính cụ thể. " +
                "Yêu cầu: CHỈ trả về kết quả cuối cùng, không giải thích, không lặp lại câu gốc.\n\n" +
                "Dữ liệu: \"" + text + "\"";
        generateContent(prompt, text, callback);
    }

    /**
     * Tóm tắt: Rút gọn nội dung, tập trung vào trọng tâm chính.
     */
    public void summarizeText(String text, GeminiCallback callback) {
        String prompt = "Nhiệm vụ: Tóm tắt đoạn văn sau cực kỳ ngắn gọn, tập trung vào ý chính nhất. " +
                "Yêu cầu: CHỈ trả về kết quả tóm tắt.\n\n" +
                "Dữ liệu: \"" + text + "\"";
        generateContent(prompt, text, callback);
    }

    /**
     * Mở rộng: Gợi ý thêm các việc cần làm, chuẩn bị hoặc vật dụng liên quan.
     */
    public void expandText(String text, GeminiCallback callback) {
        String prompt = "Nhiệm vụ: Mở rộng nội dung sau bằng cách gợi ý thêm các bước thực hiện hoặc vật dụng cần thiết liên quan. " +
                "Yêu cầu: Trình bày ngắn gọn, CHỈ trả về phần mở rộng thêm.\n\n" +
                "Dữ liệu: \"" + text + "\"";
        generateContent(prompt, text, callback);
    }

    private void generateContent(String prompt, String originalInput, GeminiCallback callback) {
        GeminiRequest request = new GeminiRequest();
        request.contents = new GeminiRequest.Content[]{
                new GeminiRequest.Content(
                        new GeminiRequest.Part[]{
                                new GeminiRequest.Part(prompt)
                        }
                )
        };

        String[] modelsToTry = {MODEL_NAME, "gemini-1.5-flash", "gemini-pro"};
        tryGenerateContent(modelsToTry, 0, request, originalInput, callback);
    }
    
    private void tryGenerateContent(String[] models, int index, GeminiRequest request, String originalInput, GeminiCallback callback) {
        if (index >= models.length) {
            callback.onError("Không tìm thấy model AI phù hợp hoặc API Key có vấn đề.");
            return;
        }
        
        String currentModel = models[index];
        retrofit2.Call<GeminiResponse> call = apiInterface.generateContent(currentModel, GEMINI_API_KEY, request);

        call.enqueue(new retrofit2.Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, retrofit2.Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GeminiResponse body = response.body();
                    if (body.candidates != null && body.candidates.length > 0) {
                        GeminiResponse.Candidate candidate = body.candidates[0];
                        if (candidate.content != null && candidate.content.parts != null && candidate.content.parts.length > 0) {
                            String result = candidate.content.parts[0].text;
                            
                            if (result != null) {
                                // 1. Xóa dấu ngoặc kép
                                result = result.replace("\"", "").trim();
                                
                                // 2. Loại bỏ câu gốc nếu AI lỡ lặp lại ở đầu
                                if (result.toLowerCase().startsWith(originalInput.toLowerCase())) {
                                    result = result.substring(originalInput.length()).trim();
                                }
                                
                                // 3. Loại bỏ các prefix phổ biến mà AI hay tự thêm vào
                                result = result.replaceAll("(?i)^(Kết quả:|Hoàn thiện:|Cải thiện:|Tóm tắt:|Mở rộng:|Nội dung:)", "").trim();
                            }
                            
                            // Nếu sau khi lọc mà rỗng (do AI lặp lại y hệt câu gốc), dùng lại kết quả gốc để xử lý
                            if (result == null || result.isEmpty()) {
                                result = candidate.content.parts[0].text.replace("\"", "").trim();
                            }
                            
                            callback.onSuccess(result);
                            return;
                        }
                    }
                }
                
                if (response.code() == 404) {
                    tryGenerateContent(models, index + 1, request, originalInput, callback);
                } else {
                    callback.onError("Lỗi API (Code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                tryGenerateContent(models, index + 1, request, originalInput, callback);
            }
        });
    }

    private interface GeminiApiInterface {
        @POST("v1/models/{model}:generateContent")
        Call<GeminiResponse> generateContent(
                @retrofit2.http.Path("model") String model,
                @Query("key") String apiKey,
                @Body GeminiRequest request
        );
    }

    static class GeminiRequest {
        Content[] contents;
        static class Content {
            Part[] parts;
            Content(Part[] parts) { this.parts = parts; }
        }
        static class Part {
            String text;
            Part(String text) { this.text = text; }
        }
    }

    static class GeminiResponse {
        Candidate[] candidates;
        static class Candidate { Content content; }
        static class Content { Part[] parts; }
        static class Part { String text; }
    }

    public interface GeminiCallback {
        void onSuccess(String result);
        void onError(String error);
    }
}
