package com.codelunch.app.settings.customizeNotifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codelunch.app.R;
import com.codelunch.app.settings.adapter.NutrisliceCategoryAdapter;
import com.codelunch.app.settings.customizeNotifications.touchHelper.MoveCallbackCategory;

public class CustomizeNotificationsCategoryActivity extends AppCompatActivity {


    private String schoolName;
    private String menuName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_notifications_category);

        Intent intent = getIntent();
        this.schoolName = intent.getStringExtra("school");
        this.menuName = intent.getStringExtra("menu");

        // Show Menu Name
        TextView nameView = findViewById(R.id.customizeNotificationNameText);
        nameView.setText(menuName + ':');

        RecyclerView recyclerView = findViewById(R.id.schools_recycler_view);

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
    }
}