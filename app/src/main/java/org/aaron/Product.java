package org.aaron;

public class Product {
    private String product_name;  // Nombre del producto
    private String brands;        // Marca del producto
    private String image_url;     // URL de la imagen
    private String code;       // Código de barras

    public Product() {
        // Constructor vacío necesario para Firebase
    }

    // Getters
    public String getProductName() {
        return product_name;
    }

    public String getBrand() {
        return brands;
    }

    public String getImageUrl() {
        return image_url;
    }

    public String getCode() {
        return code;
    }

    // Setters
    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public void setBrand(String brands) {
        this.brands = brands;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public void setCode(String code) {
        this.code = code;
    }
}


