package com.example.shoppingmanagement;

public class Product {
    private String id;
    private String name;
    private int quantity;

    public Product() {
        this.id = "";
        this.name = "";
        this.quantity = 0;
    }

    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() { return id != null ? id : ""; }
    public void setId(String id) { this.id = id != null ? id : ""; }
    
    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name != null ? name : ""; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                '}';
    }
} 