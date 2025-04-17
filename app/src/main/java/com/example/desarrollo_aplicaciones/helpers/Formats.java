package com.example.desarrollo_aplicaciones.helpers;

import android.widget.EditText;

public class Formats {

    private static String capitalize(String word) {
        if (word.isEmpty()) return "";
        return Character.toUpperCase(word.charAt(0)) +
                word.substring(1).toLowerCase();
    }
    public static String capitalizeInput(EditText nameInput) {
        String name = nameInput.getText().toString().trim();

        if (name.trim().isEmpty()) {
            return "";
        }

        String[] words = name.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result
                        .append(capitalize(word))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String getTextInput(EditText input){
        return input.getText().toString().trim();
    }
}
