package com.example.desarrollo_aplicaciones.di;

import android.content.Context;
import com.example.desarrollo_aplicaciones.repository.auth.TokenRepository;
import dagger.hilt.android.qualifiers.ApplicationContext;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://localhost:8080/"; // Reemplaza

    private final Context applicationContext;

    @Inject
    public RetrofitClient(@ApplicationContext Context context) {
        this.applicationContext = context;
    }

    public Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    String token = null;

                    TokenRepository tokenRepository = new TokenRepository(applicationContext);
                    token = tokenRepository.getToken();

                    if (token != null && !token.isEmpty()) {
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Bearer " + token);
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }

                    return chain.proceed(original);
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public <S> S createService(Class<S> serviceClass) {
        return getRetrofitInstance().create(serviceClass);
    }
}