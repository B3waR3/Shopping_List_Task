package com.example.shoppingmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.shoppingmanagement.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends Fragment {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_page, container, false);
        
        mAuth = FirebaseAuth.getInstance();
        
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        registerLink = view.findViewById(R.id.registerLink);

        loginButton.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> 
            Navigation.findNavController(view).navigate(R.id.action_loginPage_to_registerPage));

        return view;
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Navigation.findNavController(getView())
                        .navigate(R.id.action_loginPage_to_mainPage);
                } else {
                    Toast.makeText(getContext(), "Login failed: " + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
} 