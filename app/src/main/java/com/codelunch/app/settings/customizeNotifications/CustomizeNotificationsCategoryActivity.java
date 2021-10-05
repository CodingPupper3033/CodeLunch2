package com.codelunch.app.settings.customizeNotifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codelunch.app.R;
import com.codelunch.app.api.finder.NutrisliceFinder;
import com.codelunch.app.settings.adapter.NutrisliceCategoryAdapter;
import com.codelunch.app.settings.customizeNotifications.touchHelper.MoveCallbackCategory;

public class CustomizeNotificationsCategoryActivity extends AppCompatActivity {
    private String schoolName;
    private String menuName;
    private NutrisliceFinder nutrisliceFinder;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_notifications_category);

        Intent intent = getIntent();
        this.schoolName = intent.getStringExtra("school");
        this.menuName = intent.getStringExtra("menu");

        // Finder
        nutrisliceFinder = new NutrisliceFinder(getApplicationContext());

        // Progress Bar
        progressBar = findViewById(R.id.progressBarCustomizeCategories);

        // Show Menu Name
        TextView nameView = findViewById(R.id.customizeNotificationNameText);
        nameView.setText(menuName + ':');


        recyclerView = findViewById(R.id.schools_recycler_view);

        // Layout
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        // Divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                llm.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Move
        ItemTouchHelper itemTouchHelperMove = new ItemTouchHelper(new MoveCallbackCategory(this, schoolName, menuName));
        itemTouchHelperMove.attachToRecyclerView(recyclerView);

        // Adapter
        NutrisliceCategoryAdapter adapter = new NutrisliceCategoryAdapter(this, schoolName, menuName, itemTouchHelperMove);
        recyclerView.setAdapter(adapter);

        // Refresh Just in case
        refreshList();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customize_notifications_reload_category, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Refresh list
        refreshList();
        return true;
    }

    public void refreshList() {
        progressBar.setVisibility(View.VISIBLE);
        nutrisliceFinder.makeCategoryRequest(schoolName, menuName, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                recyclerView.getAdapter().notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}