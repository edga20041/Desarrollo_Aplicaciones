package com.example.desarrollo_aplicaciones.api.model;

import com.example.desarrollo_aplicaciones.entity.Entrega;
import com.example.desarrollo_aplicaciones.entity.User;
import com.example.desarrollo_aplicaciones.entity.Ruta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import okhttp3.ResponseBody;

public interface ApiService {
    @POST("/auth/register")
    Call<ResponseBody> register(@Body RegisterRequest registerRequest);
    @POST("/auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);
    @GET("/auth/user/me")
    Call<UserResponse> getUserInfo(@Header("Authorization") String authorization);
    @GET("/auth/users/{userId}")
    Call<User> getUserById(@Path("userId") int userId);
    @POST("/auth/recover")
    Call<Void> recoverPassword(@Body RecoverPasswordRequest request);

    @POST("/auth/verify")
    Call<AuthResponse> verify(@Body VerifyRequest request);
    @POST("/auth/resend-code")
    Call<ResponseBody> resendCode(@Body ResendCodeRequest request);
    @GET("/repartidores/historial")
    Call<List<Entrega>> getHistorialEntregas(@Header("Authorization") String authorization);


    @GET("/rutas/pendientes")
    Call<List<Ruta>> getRutasPendientes(@Header("Authorization") String authorization);


    @POST("/rutas/aceptar-ruta/{rutaId}")
    Call<ResponseBody> aceptarRuta(
            @Header("Authorization") String authorization,
            @Path("rutaId") Long rutaId
    );

    @POST("/rutas/{rutaId}/rechazar")
    Call<ResponseBody> rechazarRuta(
            @Header("Authorization") String authorization,
            @Path("rutaId") Long rutaId
    );
}