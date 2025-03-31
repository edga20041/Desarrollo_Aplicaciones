// filepath: c:\Users\USUARIO\Desarrollo_Aplicaciones\app\src\main\java\com\example\desarrollo_aplicaciones\HomeActivity.java
package com.example.desarrollo_aplicaciones;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // Ejemplo: Botón para cerrar sesión
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Eliminar token de SharedPreferences
            SharedPreferences preferences = getSharedPreferences("auth_prefs", MODE_PRIVATE);
            preferences.edit().remove("auth_token").apply();
            
            // Volver a MainActivity
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}