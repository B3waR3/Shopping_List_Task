package com.example.shoppingmanagement;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;

    public ProductAdapter(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        
        String icon = getProductIcon(product.getName());
        holder.productIcon.setText(icon);
        
        String displayText = product.getName() + ": " + product.getQuantity();
        holder.productName.setText(displayText);
        
        holder.deleteButton.setText("\uD83D\uDDD1");
        holder.deleteButton.setOnClickListener(v -> deleteProduct(holder.getAdapterPosition(), product));
    }

    private String getProductIcon(String productName) {
        switch (productName.toLowerCase()) {
            case "cucumber":
                return "\uD83E\uDD52"; // 🥒
            case "tomato":
                return "\uD83C\uDF45"; // 🍅
            case "bell pepper":
                return "\uD83E\uDED1"; // 🫑
            default:
                return "Custom";
        }
    }

    private void deleteProduct(int position, Product product) {
        if (product.getId() == null || product.getId().isEmpty()) {
            Log.e("ProductAdapter", "Cannot delete product: Invalid ID");
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("ProductAdapter", "Cannot delete product: User not logged in");
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://shopping-list-task-f7de1-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference productsRef = database.getReference("shopping_lists")
            .child(currentUser.getUid())
            .child(product.getId());

        productsRef.removeValue()
            .addOnSuccessListener(aVoid -> {
            })
            .addOnFailureListener(e -> {
                Log.e("ProductAdapter", "Error deleting product", e);
            });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products.clear();
        if (newProducts != null) {
            this.products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final TextView productName;
        final TextView productIcon;
        final TextView deleteButton;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productIcon = itemView.findViewById(R.id.productIcon);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 