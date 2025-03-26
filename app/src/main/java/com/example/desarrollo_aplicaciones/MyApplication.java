package com.example.desarrollo_aplicaciones;

import android.app.Application;

import com.example.desarrollo_aplicaciones.Dagger.AppComponent;
import com.example.desarrollo_aplicaciones.Dagger.DaggerAppComponent;

public class MyApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializamos el componente Dagger
        appComponent = DaggerAppComponent.create();  // Dagger genera la implementación de AppComponent
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
