package com.example.desarrollo_aplicaciones.Dagger;
import com.example.desarrollo_aplicaciones.LogReg.LoginActivity;
import com.example.desarrollo_aplicaciones.LogReg.RecoverPasswordActivity;
import com.example.desarrollo_aplicaciones.LogReg.RegisterActivity;
import com.example.desarrollo_aplicaciones.LogReg.VerifyCodeActivity;
import com.example.desarrollo_aplicaciones.MainActivity;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(LoginActivity loginActivity);
    void inject(RegisterActivity registerActivity);
    void inject(RecoverPasswordActivity recoverPasswordActivity);
    void inject(VerifyCodeActivity verifyCodeActivity);
    void inject(MainActivity mainActivity);
}
