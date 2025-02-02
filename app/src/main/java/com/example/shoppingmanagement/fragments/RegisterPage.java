package com.example.shoppingmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.shoppingmanagement.R;
import com.example.shoppingmanagement.user.userData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import android.util.Log;

public class RegisterPage extends Fragment {
    private static final String TAG = "RegisterPage";
    private EditText emailInput, passwordInput, usernameInput;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_page, container, false);
        
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        usernameInput = view.findViewById(R.id.usernameInput);
        registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        registerButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    userData newUser = new userData(user.getUid(), email, username);

                    if (getContext() != null) {
                        getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                            .edit()
                            .putString("username", username)
                            .apply();
                    }
                    
                    database.getReference("users")
                        .child(user.getUid())
                        .setValue(newUser)
                        .addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                Navigation.findNavController(getView())
                                    .navigate(R.id.action_registerPage_to_mainPage);
                            } else {
                                Log.e(TAG, "Database error: ", dbTask.getException());
                                Toast.makeText(getContext(), 
                                    "Error saving user data: " + dbTask.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            }
                            registerButton.setEnabled(true);
                        });
                } else {
                    Log.e(TAG, "Authentication error: ", task.getException());
                    Toast.makeText(getContext(), 
                        "Registration failed: " + task.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                }
            });
    }
} 