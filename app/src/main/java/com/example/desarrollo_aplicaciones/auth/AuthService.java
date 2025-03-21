package com.example.desarrollo_aplicaciones.auth;

import com.example.desarrollo_aplicaciones.DI.User;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;

public class AuthService {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public AuthService() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public Task<AuthResult> register(String email, String password, String nombre, String apellido, String dni, String phone) {
        return auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user.getUid(), nombre, apellido, dni, email, phone);
                        }
                    }
                });
    }

    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }


    public void saveUserData(String userId, String nombre, String apellido, String dni, String email, String phone) {
        User user = new User(nombre, apellido, dni, email, phone);
        db.collection("users").document(userId).set(user);
    }


    public void sendEmailVerification() {
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().sendEmailVerification();
        }
    }

    public void resetPassword(String email) {
        auth.sendPasswordResetEmail(email);
    }
}
