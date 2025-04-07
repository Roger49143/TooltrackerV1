package com.example.tooltracking;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterToolActivity extends AppCompatActivity {

    private EditText brandEditText, toolNameEditText, modelNumberEditText;
    private Button registerButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tool);

      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        brandEditText = findViewById(R.id.brandEditText);
        toolNameEditText = findViewById(R.id.toolNameEditText);
        modelNumberEditText = findViewById(R.id.modelNumberEditText);
        registerButton = findViewById(R.id.registerButton);

        prefs = getSharedPreferences("RegisteredTools", MODE_PRIVATE);

        registerButton.setOnClickListener(v -> {
            String brand = brandEditText.getText().toString().trim();
            String tool = toolNameEditText.getText().toString().trim();
            String model = modelNumberEditText.getText().toString().trim();

            if (brand.isEmpty() || tool.isEmpty() || model.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                String toolKey = tool + "_" + model;
                String mode = "ENABLED"; // Default mode
                String toolData = brand + "|" + tool + "|" + model + "|" + mode;


                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(toolKey, toolData);
                editor.apply();

                Toast.makeText(this, "Tool registered!", Toast.LENGTH_SHORT).show();
                finish(); // closes RegisterToolActivity and returns to MainActivity

                brandEditText.setText("");
                toolNameEditText.setText("");
                modelNumberEditText.setText("");
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // closes the current activity and returns to previous screen
        return true;
    }

}
