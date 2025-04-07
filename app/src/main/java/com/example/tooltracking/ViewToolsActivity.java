package com.example.tooltracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class ViewToolsActivity extends AppCompatActivity {

    private ListView toolListView;
    private ArrayList<String> toolsList;
    private ArrayList<String> fullToolDataList; // Stores full data for click use

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tools);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolListView = findViewById(R.id.toolListView);
        toolsList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("RegisteredTools", MODE_PRIVATE);

        loadTools();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toolsList);
        toolListView.setAdapter(adapter);

        toolListView.setOnItemClickListener((parent, view, position, id) -> {
            String full = fullToolDataList.get(position);
            try {
                String[] parts = full.split("\\|");
                String brand = parts[0];
                String toolName = parts[1];
                String modelNumber = parts[2];

                Intent intent = new Intent(ViewToolsActivity.this, ToolDetailActivity.class);
                intent.putExtra("brand", brand);
                intent.putExtra("toolName", toolName);
                intent.putExtra("modelNumber", modelNumber);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to open tool details.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadTools() {
        toolsList.clear();
        fullToolDataList = new ArrayList<>();
        for (String key : sharedPreferences.getAll().keySet()) {
            String toolData = sharedPreferences.getString(key, "");
            if (!toolData.isEmpty()) {
                String[] parts = toolData.split("\\|");
                if (parts.length >= 3) {
                    String brand = parts[0];
                    String toolName = parts[1];
                    String modelNumber = parts[2];

                    toolsList.add(toolName); // Only show the name in the list
                    fullToolDataList.add(toolData); // Save full data for later use
                }
            }
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
