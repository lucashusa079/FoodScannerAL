package org.aaron.foodscanner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.ScanContract;
import com.squareup.picasso.Picasso;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.aaron.OpenFoodFactsApi;
import org.aaron.Product;
import org.aaron.ProductResponse;
import org.aaron.foodscanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    public void iniciarEscaneig(View v) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Escaneja un codi de barres");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setCameraId(0);  // Usa la càmera posterior

        barcodeLauncher.launch(options);
    }

        private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String barcode = result.getContents();
                    buscarProducte(barcode);
                }
            }
    );
    private void buscarProducte(String barcode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenFoodFactsApi api = retrofit.create(OpenFoodFactsApi.class);

        Call<ProductResponse> call = api.getProduct(barcode);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body().getProduct();
                    mostrarProducte(product);
                } else {
                    Toast.makeText(MainActivity.this, "Producte no trobat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de connexió", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void mostrarProducte(Product product) {
        if (product == null) {
            Log.e("MainActivity", "El producto es null");
            return;
        }

        MaterialCardView cardProducte = findViewById(R.id.cardProducte);
        ImageView imageProducte = findViewById(R.id.imageProducte);
        TextView nomProducte = findViewById(R.id.nomProducte);
        TextView marcaProducte = findViewById(R.id.marcaProducte);

        if (product.getProductName() != null) {
            nomProducte.setText(product.getProductName());
        } else {
            nomProducte.setText("Nombre no disponible");
        }

        if (product.getBrand() != null) {
            marcaProducte.setText("Marca: " + product.getBrand());
        } else {
            marcaProducte.setText("Marca no disponible");
        }

        if (product.getImageUrl() != null) {
            Picasso.get().load(product.getImageUrl()).into(imageProducte);
        } else {
            imageProducte.setImageResource(R.drawable.ic_home_black_24dp); // Imagen por defecto
        }

        cardProducte.setVisibility(View.VISIBLE);
        guardarProducteHistorial(product);
    }

    private void guardarProducteHistorial(Product product) {
        DatabaseReference historialRef = FirebaseDatabase.getInstance("https://foodscanner-32fdf-default-rtdb.firebaseio.com/")
                .getReference("historial");
        DatabaseReference productRef = historialRef.push(); // Crea un ID único
        productRef.setValue(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Producto guardado correctamente!");
            } else {
                Log.e("Firebase", "Error al guardar el producto", task.getException());
            }
        });
    }








}