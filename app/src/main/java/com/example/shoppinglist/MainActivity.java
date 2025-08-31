package com.example.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lvProducts;
    ArrayList<Product> products;
    ArrayList<String> ids;
    ArrayAdapter adapter;

    FloatingActionButton btnCreate;
    private ActivityResultLauncher<Intent> updateLauncher;
    private ActivityResultLauncher<Intent> createLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvProducts = findViewById(R.id.lvProducts);
        btnCreate =  findViewById(R.id.btnCreate);

        loadList();

        updateLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> { backFromUpdate(); }
                );
        createLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> { backFromCreate(); }
                );
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductActivity.class);
            createLauncher.launch(intent);
        });

    }

    private void backFromCreate() {
        Toast.makeText(this, "product created successfully", Toast.LENGTH_SHORT).show();
        loadList();
    }

    private void backFromUpdate() {
        Toast.makeText(this, "update successfully", Toast.LENGTH_SHORT).show();
        loadList();
    }

    private void loadList() {
        FirebaseFirestore.getInstance().collection(DB.PRODUCTS)
                .get()
                .addOnSuccessListener(snapshots -> {
                    products = new ArrayList<>();
                    ids = new ArrayList<>();
                    List<DocumentSnapshot> list = snapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        Product p = d.toObject(Product.class);
                        products.add(p);
                        ids.add(d.getId());
                    }
                    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
                    lvProducts.setAdapter(adapter);
                    lvProducts.setOnItemClickListener((p, v, pos,  id) -> { onItemClick(pos); });
                })
                .addOnFailureListener(exc -> {
                    Toast.makeText(this, "Failed loading data...", Toast.LENGTH_LONG).show();
                    Log.e("ShoppingList", "Error", exc);
                });
    }

    private void onItemClick(int pos)
    {
        String id = ids.get(pos);
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra("EDIT", true);
        updateLauncher.launch(intent);
    }
}