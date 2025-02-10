package org.aaron.foodscanner.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aaron.Product;
import org.aaron.ProductAdapter;
import org.aaron.foodscanner.R;
import org.aaron.foodscanner.databinding.FragmentDashboardBinding;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ListView listViewHistorial;
    private TextView textNoProducts;
    private ProductAdapter adapter;
    private ArrayList<Product> historialList;
    private DatabaseReference historialRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewHistorial = root.findViewById(R.id.listViewHistorial);  // Aquí se hace la referencia correcta
        textNoProducts = root.findViewById(R.id.textNoProducts);

        // Inicialitzar Firebase Database
        historialRef = FirebaseDatabase.getInstance("https://foodscanner-32fdf-default-rtdb.firebaseio.com/")
                .getReference("historial");

        // Configurar l'adaptador per a la llista
        historialList = new ArrayList<>();
        adapter = new ProductAdapter(requireContext(), historialList);
        listViewHistorial.setAdapter(adapter);

        // Aquí agregas el ValueEventListener para cargar los datos desde Firebase
        carregarHistorial();

        return root;
    }

    private void carregarHistorial() {
        historialRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historialList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Product product = data.getValue(Product.class);
                        if (product != null) {
                            // Añadimos el producto a la lista
                            historialList.add(product);
                        }
                    }
                    // Si tenemos productos, mostramos la lista
                    textNoProducts.setVisibility(View.GONE);
                    listViewHistorial.setVisibility(View.VISIBLE);
                } else {
                    // Si no hay productos en la base de datos, mostramos un mensaje
                    textNoProducts.setVisibility(View.VISIBLE);
                    listViewHistorial.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();  // Notificamos al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Aquí puedes gestionar el error en caso de que la lectura de Firebase falle
                Log.e("Firebase", "Error de lectura de Firebase", error.toException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

