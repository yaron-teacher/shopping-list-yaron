package com.example.shoppinglist;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class ProductActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etQuantity;
    private Button btnCreate;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        btnCreate = findViewById(R.id.btnCreate);

        if (getIntent().getBooleanExtra("EDIT", false))
            load();
        else {
            // new item
            btnCreate.setOnClickListener(view -> {
                add();
            });
        }

    }

    private void load() {
        id = getIntent().getStringExtra("ID");

        FirebaseFirestore.getInstance()
                .collection(DB.PRODUCTS)
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Product product = documentSnapshot.toObject(Product.class);
                    etName.setText(product.getName());
                    etQuantity.setText("" + product.getQuantity());
                })
                .addOnFailureListener(exc -> {
                    Toast.makeText(this, "oops...", Toast.LENGTH_LONG).show();
                    Log.e("ShoppingList", "Error", exc);
                });

        btnCreate.setText("Update");

        btnCreate.setOnClickListener(v -> {
            update();
        });

    }

    private Product validate() {
        boolean succ = true;
        if (etName.getText().toString().isEmpty()) {
            etName.setError("Please enter value");
            succ = false;
        }
        if (etQuantity.getText().toString().isEmpty()) {
            etQuantity.setError("Please enter value");
            succ = false;
        }
        if (!succ)
            return null;
        String name = etName.getText().toString();
        int quantity = Integer.parseInt(etQuantity.getText().toString());

        return new Product(name, quantity);
    }

    private void update() {
        Product product = validate();
        if (product != null) {
            FirebaseFirestore.getInstance()
                    .collection(DB.PRODUCTS)
                    .document(id)
                    .set(product)
                    .addOnSuccessListener(t -> {
                        finish();
                    })
                    .addOnFailureListener(exc -> {
                        Toast.makeText(this, "oops...", Toast.LENGTH_LONG).show();
                        Log.e("ShoppingList", "Error", exc);
                    });
        }
    }

    private void add() {
        Product product = validate();
        if (product != null) {
            FirebaseFirestore.getInstance().collection(DB.PRODUCTS).
                    add(product)
                    .addOnSuccessListener(res -> {
                        finish();
                    })
                    .addOnFailureListener(exc -> {
                        Toast.makeText(this, "oops...", Toast.LENGTH_LONG).show();
                        Log.e("ShoppingList", "Error", exc);
                    });
        }
    }
}