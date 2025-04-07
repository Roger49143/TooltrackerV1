package com.example.tooltracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.Build;
import android.os.VibrationEffect;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import android.view.animation.AnimationUtils;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tooltracking.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  //  private ListView toolListView;
  //  private Button addToolToListButton;
  //  private Spinner toolSelectorSpinner;
   // private ArrayList<String> registeredToolNames;

    private ArrayList<String> toolsList;
  //  private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("DEBUG", "MainActivity launched");

        // Toolbar setup
        sharedPreferences = getSharedPreferences("ToolTrackerPrefs", MODE_PRIVATE);
        toolsList = new ArrayList<>();
        loadToolsFromPreferences();
  //      adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toolsList);
      //  toolListView = findViewById(R.id.toolListView);
       // toolListView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Drawer + NavigationView
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();  // ✅ THIS shows the hamburger icon

    ;

// Handle menu item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            drawerLayout.postDelayed(() -> {
                if (id == R.id.nav_view_tools) {
                    startActivity(new Intent(MainActivity.this, ViewToolsActivity.class));
                } else if (id == R.id.nav_register_tool) {
                    startActivity(new Intent(MainActivity.this, RegisterToolActivity.class));
                } else if (id == R.id.nav_delete_tool) {
                    startActivity(new Intent(MainActivity.this, DeleteToolActivity.class));
                }
            }, 250);

            return true;
        });


        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);

// Image resource array
        int[] images = {
                R.drawable.bosch,
                R.drawable.dewalt,
                R.drawable.makita,
                R.drawable.milwaukee
        };

// Add each image to the ViewFlipper
        for (int image : images) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(image);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(6000);

        viewFlipper.setInAnimation(this, android.R.anim.fade_in);
        viewFlipper.setOutAnimation(this, android.R.anim.fade_out);

        viewFlipper.startFlipping();






        // Handle tool clicks
      //  toolListView.setOnItemClickListener((parent, view, position, id) -> {
            // Trigger alarm when a tool is clicked (for demonstration purposes)
         //   triggerAlarm();

            // Proceed to tool details
         //   Intent intent = new Intent(MainActivity.this, ToolDetailActivity.class);
         //   intent.putExtra("toolName", toolsList.get(position));
        //    startActivity(intent);
      //  });
    }
    private ArrayList<String> loadRegisteredToolNames() {
        SharedPreferences prefs = getSharedPreferences("RegisteredTools", MODE_PRIVATE);
        ArrayList<String> names = new ArrayList<>();
        for (String key : prefs.getAll().keySet()) {
            String data = prefs.getString(key, null);
            if (data != null) {
                String[] parts = data.split("\\|");
                if (parts.length == 3) {
                    names.add(parts[1] + " (" + parts[2] + ")");
                }
            }
        }
        return names;
    }

    private void loadToolsFromPreferences() {
        int size = sharedPreferences.getInt("toolsSize", 0);
        for (int i = 0; i < size; i++) {
            toolsList.add(sharedPreferences.getString("tool_" + i, ""));
        }
    }

    private void saveToolsToPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("toolsSize", toolsList.size());
        for (int i = 0; i < toolsList.size(); i++) {
            editor.putString("tool_" + i, toolsList.get(i));
        }
        editor.apply();
    }

    // The alarm trigger method
    private void triggerAlarm() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound);
            mediaPlayer.start();
        } catch (Exception e) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(500);
                }
            }
        }
    }



    @Override
    protected void onDestroy() {
        // Release MediaPlayer when the activity is destroyed to avoid memory leaks
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }


    private void showDeleteToolDialog() {
        String[] toolsArray = toolsList.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Delete a Tool")
                .setItems(toolsArray, (dialog, which) -> {
                    String toolToDelete = toolsList.get(which);

                    new AlertDialog.Builder(this)
                            .setTitle("Confirm Delete")
                            .setMessage("Are you sure you want to delete \"" + toolToDelete + "\"?")
                            .setPositiveButton("Delete", (confirmDialog, whichButton) -> {
                                // 1. Remove from visible list
                                toolsList.remove(toolToDelete);

                                // 2. Remove from ToolTrackerPrefs
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.putInt("toolsSize", toolsList.size());
                                for (int i = 0; i < toolsList.size(); i++) {
                                    editor.putString("tool_" + i, toolsList.get(i));
                                }
                                editor.apply();

                                // 3. Remove from RegisteredTools
                                SharedPreferences regPrefs = getSharedPreferences("RegisteredTools", MODE_PRIVATE);
                                SharedPreferences.Editor regEditor = regPrefs.edit();

                                // Extract ToolName and ModelNumber from: "ToolName (ModelNumber)"
                                String cleanToolName = toolToDelete.substring(0, toolToDelete.lastIndexOf(" (")).trim();
                                String modelNumber = toolToDelete.substring(toolToDelete.lastIndexOf("(") + 1, toolToDelete.lastIndexOf(")")).trim();

                                for (String key : regPrefs.getAll().keySet()) {
                                    String data = regPrefs.getString(key, null);
                                    if (data != null) {
                                        String[] parts = data.split("\\|");
                                        if (parts.length == 3) {
                                            String name = parts[1].trim();
                                            String model = parts[2].trim();
                                            if (name.equals(cleanToolName) && model.equals(modelNumber)) {
                                                regEditor.remove(key);
                                            }
                                        }
                                    }
                                }
                                regEditor.apply();

         //                       adapter.notifyDataSetChanged();
                                Toast.makeText(this, toolToDelete + " deleted", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



  //  @Override
 //   protected void onResume() {
 //       super.onResume();

 //       ArrayList<String> updatedList = loadRegisteredToolNames();

 //       for (String tool : updatedList) {
 //           if (!toolsList.contains(tool)) {
 //               toolsList.add(tool);
 //           }
 //       }

  //      adapter.notifyDataSetChanged();
  //      saveToolsToPreferences();
 //   }



}
