package org.aaron;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import org.aaron.foodscanner.R;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Context context;
    private List<Product> products;

    public ProductAdapter(Context context, List<Product> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }

        Product product = getItem(position);

        TextView productName = convertView.findViewById(R.id.product_name);
        TextView productBrand = convertView.findViewById(R.id.brands);
        TextView productBarcode = convertView.findViewById(R.id.code);
        ImageView productImage = convertView.findViewById(R.id.image_url);

        if (product != null) {
            productName.setText(product.getProductName());
            productBrand.setText("Marca: " + product.getBrand());
            productBarcode.setText("Codi: " + product.getCode());

            // Usa Picasso para cargar la imagen desde la URL
            Picasso.get().load(product.getImageUrl()).into(productImage);
        }

        return convertView;
    }
}
