package com.example.shoppingmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingmanagement.Product;
import com.example.shoppingmanagement.ProductAdapter;
import com.example.shoppingmanagement.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.DividerItemDecoration;
import android.content.Context;

public class MainPage extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FloatingActionButton addProductButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://shopping-list-task-f7de1-default-rtdb.europe-west1.firebasedatabase.app");

        TextView welcomeText = view.findViewById(R.id.welcomeText);
        if (getContext() != null) {
            String username = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("username", null);
            
            if (username != null) {
                welcomeText.setText("Hello " + username + "!");
            } else {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    welcomeText.setText("Hello " + currentUser.getEmail() + "!");
                } else {
                    welcomeText.setText("Hello Guest!");
                }
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);
        
        addProductButton = view.findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(v -> showAddProductDialog());

        loadProducts();
        
        return view;
    }

    private void loadProducts() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        String userId = currentUser.getUid();
        
        DatabaseReference userProductsRef = database.getReference("shopping_lists").child(userId);
        
        userProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> newProducts = new ArrayList<>();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    try {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            newProducts.add(product);
                        }
                    } catch (Exception e) {
                    }
                }
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        productList.clear();
                        productList.addAll(newProducts);
                        productAdapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);

        int[] quantities = {0, 0, 0};

        TextView plusCucumber = dialogView.findViewById(R.id.plusCucumberButton);
        TextView minusCucumber = dialogView.findViewById(R.id.minusCucumberButton);
        TextView cucumberQuantity = dialogView.findViewById(R.id.cucumberQuantity);

        TextView plusTomato = dialogView.findViewById(R.id.plusTomatoButton);
        TextView minusTomato = dialogView.findViewById(R.id.minusTomatoButton);
        TextView tomatoQuantity = dialogView.findViewById(R.id.tomatoQuantity);

        TextView plusPepper = dialogView.findViewById(R.id.plusPepperButton);
        TextView minusPepper = dialogView.findViewById(R.id.minusPepperButton);
        TextView pepperQuantity = dialogView.findViewById(R.id.pepperQuantity);

        plusCucumber.setOnClickListener(v -> {
            quantities[0]++;
            cucumberQuantity.setText(String.valueOf(quantities[0]));
        });
        minusCucumber.setOnClickListener(v -> {
            if (quantities[0] > 0) {
                quantities[0]--;
                cucumberQuantity.setText(String.valueOf(quantities[0]));
            }
        });

        plusTomato.setOnClickListener(v -> {
            quantities[1]++;
            tomatoQuantity.setText(String.valueOf(quantities[1]));
        });
        minusTomato.setOnClickListener(v -> {
            if (quantities[1] > 0) {
                quantities[1]--;
                tomatoQuantity.setText(String.valueOf(quantities[1]));
            }
        });

        plusPepper.setOnClickListener(v -> {
            quantities[2]++;
            pepperQuantity.setText(String.valueOf(quantities[2]));
        });
        minusPepper.setOnClickListener(v -> {
            if (quantities[2] > 0) {
                quantities[2]--;
                pepperQuantity.setText(String.valueOf(quantities[2]));
            }
        });

        EditText customProductName = dialogView.findViewById(R.id.customProductName);
        TextView plusCustom = dialogView.findViewById(R.id.plusCustomButton);
        TextView minusCustom = dialogView.findViewById(R.id.minusCustomButton);
        TextView customQuantity = dialogView.findViewById(R.id.customQuantity);
        int[] customQuantityValue = {0};

        plusCustom.setOnClickListener(v -> {
            customQuantityValue[0]++;
            customQuantity.setText(String.valueOf(customQuantityValue[0]));
        });

        minusCustom.setOnClickListener(v -> {
            if (customQuantityValue[0] > 0) {
                customQuantityValue[0]--;
                customQuantity.setText(String.valueOf(customQuantityValue[0]));
            }
        });

        AlertDialog dialog = builder.setView(dialogView).create();

        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button addButton = dialogView.findViewById(R.id.addButton);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        addButton.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            boolean anyProductAdded = false;

            if (quantities[0] > 0) {
                addProductToDatabase("Cucumber", quantities[0], user);
                anyProductAdded = true;
            }
            if (quantities[1] > 0) {
                addProductToDatabase("Tomato", quantities[1], user);
                anyProductAdded = true;
            }
            if (quantities[2] > 0) {
                addProductToDatabase("Bell Pepper", quantities[2], user);
                anyProductAdded = true;
            }

            String customName = customProductName.getText().toString().trim();
            if (!customName.isEmpty() && customQuantityValue[0] > 0) {
                addProductToDatabase(customName, customQuantityValue[0], user);
                anyProductAdded = true;
            }

            if (anyProductAdded) {
                Toast.makeText(getContext(), "Products added successfully", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void addProductToDatabase(String productName, int quantity, FirebaseUser user) {
        if (user == null) {
            return;
        }
        
        DatabaseReference productsRef = database.getReference("shopping_lists")
            .child(user.getUid());
        
        productsRef.orderByChild("name").equalTo(productName).get()
            .addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product existingProduct = snapshot.getValue(Product.class);
                        if (existingProduct != null) {
                            int newQuantity = existingProduct.getQuantity() + quantity;
                            snapshot.getRef().child("quantity").setValue(newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                    loadProducts();
                                })
                                .addOnFailureListener(e -> {
                                    if (getContext() != null) {
                                        Toast.makeText(getContext(), "Failed to update product", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            return;
                        }
                    }
                } else {
                    String productId = productsRef.push().getKey();
                    if (productId != null) {
                        Product newProduct = new Product(productName, quantity);
                        newProduct.setId(productId);
                        
                        productsRef.child(productId).setValue(newProduct)
                            .addOnSuccessListener(aVoid -> {
                                loadProducts();
                            })
                            .addOnFailureListener(e -> {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }
            })
            .addOnFailureListener(e -> {
            });
    }
} 
