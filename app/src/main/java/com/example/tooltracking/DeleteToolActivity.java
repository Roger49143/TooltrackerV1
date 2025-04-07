package com.example.tooltracking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class DeleteToolActivity extends AppCompatActivity {

    private Spinner toolSpinner;
    private Button deleteButton;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> toolNames;
    private ArrayList<String> toolKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_tool);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolSpinner = findViewById(R.id.toolSpinner);
        deleteButton = findViewById(R.id.deleteButton);

        sharedPreferences = getSharedPreferences("RegisteredTools", MODE_PRIVATE);
        toolNames = new ArrayList<>();
        toolKeys = new ArrayList<>();

        loadToolsFromPreferences();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, toolNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toolSpinner.setAdapter(adapter);

        deleteButton.setOnClickListener(v -> {
            int selectedIndex = toolSpinner.getSelectedItemPosition();
            if (selectedIndex == 0) {
                Toast.makeText(this, "Please select a tool to delete.", Toast.LENGTH_SHORT).show();
                return;
            }

            String toolName = toolNames.get(selectedIndex); // For display in confirmation
            String keyToDelete = toolKeys.get(selectedIndex - 1);

            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete \"" + toolName + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(keyToDelete);
                        editor.apply();

                        Toast.makeText(this, "Tool deleted!", Toast.LENGTH_SHORT).show();

                        // Refresh tool list
                        toolNames.clear();
                        toolKeys.clear();
                        loadToolsFromPreferences();
                        adapter.notifyDataSetChanged();
                        toolSpinner.setSelection(0);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        }

        private void loadToolsFromPreferences () {
            toolNames.clear();
            toolKeys.clear();

            // Add prompt as the first item
            toolNames.add("Please select a tool");

            for (String key : sharedPreferences.getAll().keySet()) {
                String toolData = sharedPreferences.getString(key, "");
                if (!toolData.isEmpty()) {
                    String[] parts = toolData.split("\\|");
                    if (parts.length >= 3) {
                        String brand = parts[0];
                        String toolName = parts[1];
                        String model = parts[2];

                        String displayName = "Brand: " + brand + " | Name: " + toolName + " | Model: " + model;
                        toolNames.add(displayName);
                        toolKeys.add(key);
                    }
                }
            }
        }


        @Override
        public boolean onSupportNavigateUp () {
            finish();
            return true;
        }
    }


