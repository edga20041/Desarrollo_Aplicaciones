package com.example.desarrollo_aplicaciones.helpers;
import android.util.Log;
import android.widget.Toast;

import com.example.desarrollo_aplicaciones.activity.authActivity.RegisterActivity;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class Validations {
    private final String TAG;

    public Validations(String tag) {
        this.TAG = tag;
    }

    public boolean validateFields(RegisterActivity activity, String nombre, String apellido, String dni, String phone, String email, String password, String confirmPassword) {
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || phone.isEmpty() ||
                email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(activity, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidName(nombre)) {
            Toast.makeText(activity, "Nombre inválido. Debe empezar por mayúscula y no tener números.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidSurname(apellido)) {
            Toast.makeText(activity, "Apellido inválido. Debe empezar por mayúscula y no tener números.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidDni(dni)) {
            Toast.makeText(activity, "DNI inválido. Debe tener entre 5 y 8 dígitos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPhoneNumber(phone, "AR")) {
            Toast.makeText(activity, "Teléfono inválido para Argentina.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(activity, "Correo electrónico inválido.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(activity, "Contraseña inválida. Debe tener una mayúscula, un número, un caracter especial y mínimo 8 caracteres.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(activity, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidName(String name) {
        return name.matches("^[A-Z][a-zA-Z ]*$");
    }

    private boolean isValidSurname(String surname) {
        return surname.matches("^[A-Z][a-zA-Z ]*$");
    }

    private boolean isValidDni(String dni) {
        return dni.matches("^\\d{5,8}$");
    }

    private boolean isValidPhoneNumber(String phoneNumber, String region) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phoneNumber, region);
            return phoneUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            Log.e(TAG, "Fomato de numero de telefono inválido.");
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%&*_+?\\-=]).{8,}$");
    }
}
