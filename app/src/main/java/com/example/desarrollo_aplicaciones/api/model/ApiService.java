package com.example.desarrollo_aplicaciones.api.model;

import com.example.desarrollo_aplicaciones.entity.Entrega;
import com.example.desarrollo_aplicaciones.entity.Estado;
import com.example.desarrollo_aplicaciones.entity.User;
import com.example.desarrollo_aplicaciones.entity.Ruta;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
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

    @POST("/auth/verify")
    Call<AuthResponse> verify(@Body VerifyRequest request);
    @POST("/auth/resend-code")
    Call<ResponseBody> resendCode(@Body ResendCodeRequest request);
    @GET("entregas/pendientes")
    Call<List<Entrega>> getEntregasPendientes(@Header("Authorization") String token);
    @GET("/entregas/{entrega_id}")
    Call<Entrega> getEntregaById(@Path("entrega_id") Long id, @Header("Authorization") String token);

    @GET("/estados/{estado_id}")
    Call<Estado> getEstado(@Path("estado_id") Long id, @Header("Authorization") String token);

    @GET("/estados/{id}")
    Call<Estado> obtenerEstadoPorId(@Path("id") Long estadoId, @Header("Authorization") String token); // ¡Añade el Header aquí!


    @GET("/entregas/historial")
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

    @GET("/rutas/ruta-asignada")
    Call<RutaAsignadaResponse> getRutaAsignada(@Header("Authorization") String authorization);

    @POST("/rutas/finalizar-ruta/{rutaAsignadaId}")
    Call<ResponseBody> finalizarRuta(
            @Header("Authorization") String authorization,
            @Path("rutaAsignadaId") Long rutaAsignadaId
    );
    @PATCH("/entregas/cambiar_estado")
    Call<CambiarEstadoEntregaResponse> cambiarEstadoEntrega(
            @Body CambiarEstadoEntregaRequest request,
            @Header("Authorization") String token
    );
    @GET("/rutas/{ruta_id}")
    Call<Ruta> getRutaById(@Path("ruta_id") Long id, @Header("Authorization") String token);

    @POST("/auth/recover")
    Call<Void> recoverPassword(@Body RecoverPasswordRequest request);

    @POST("/auth/validate-recovery-code")
    Call<Map<String, Object>> validateRecoveryCode(@Body Map<String, String> body);

    @POST("/auth/reset-password")
    Call<Map<String, String>> resetPassword(@Body PasswordResetRequest request);

    @POST("/auth/resend-code")
    Call<Map<String, String>> resendVerificationCode(@Body Map<String, String> body);

    @POST("/auth/resend-recovery-code")
    Call<Map<String, String>> resendRecoveryCodePassword(@Body Map<String, String> body);
}