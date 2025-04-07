package com.example.tooltracking;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ToolDetailActivity extends AppCompatActivity {

    private TextView toolNameTextView, toolLocationTextView;
    private Button simulateInterferenceButton, applyModeButton;
    private Spinner modeSpinner;
    private String toolName;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_detail);


        // ✅ Setup toolbar + back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // 🔧 Initialize Views
        toolNameTextView = findViewById(R.id.toolNameTextView);
        toolLocationTextView = findViewById(R.id.toolLocationTextView);
        simulateInterferenceButton = findViewById(R.id.simulateInterferenceButton);
        modeSpinner = findViewById(R.id.modeSpinner);
        applyModeButton = findViewById(R.id.applyModeButton);

        // 📡 Receive tool name from intent
        Intent intent = getIntent();
        toolName = intent.getStringExtra("toolName");
        String brand = intent.getStringExtra("brand");
        String modelNumber = intent.getStringExtra("modelNumber");

// Optionally display all in a single label or individual TextViews
        toolNameTextView.setText("Tool: " + toolName + "\nBrand: " + brand + "\nModel: " + modelNumber);

        toolNameTextView.setText("Tool: " + toolName);

        // 📍 Generate and show fake location
        String mockLocation = generateMockLocation();
        toolLocationTextView.setText("Location: " + mockLocation);

        // 🚨 Simulate Interference
        simulateInterferenceButton.setOnClickListener(v -> {
            triggerAlarm();
        });

        // ⚙️ Tool Mode Spinner setup
        String[] modes = {"Enable", "Disable", "Stolen", "Service"};
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modes);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);

        // 🚀 Apply Mode Logic
        applyModeButton.setOnClickListener(v -> {
            String selectedMode = modeSpinner.getSelectedItem().toString();

            switch (selectedMode) {
                case "Stolen":
                    Toast.makeText(this, "ALERT: Tool reported stolen!", Toast.LENGTH_LONG).show();
                    break;
                case "Disable":
                    Toast.makeText(this, "Tool is now disabled.", Toast.LENGTH_SHORT).show();
                    break;
                case "Enable":
                    Toast.makeText(this, "Tool is now enabled.", Toast.LENGTH_SHORT).show();
                    break;
                case "Service":
                    Toast.makeText(this, "Tool is in service mode.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Unknown mode selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private String generateMockLocation() {
        Random rand = new Random();
        double lat = -90 + (90 - (-90)) * rand.nextDouble();
        double lon = -180 + (180 - (-180)) * rand.nextDouble();
        return "Lat: " + String.format("%.4f", lat) + ", Lon: " + String.format("%.4f", lon);
    }

    private void triggerAlarm() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
