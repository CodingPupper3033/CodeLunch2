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
import com.codelunch.app.settings.adapter.NutrisliceMenuAdapter;
import com.codelunch.app.settings.customizeNotifications.touchHelper.MoveCallbackMenu;

public class CustomizeNotificationsMenuActivity extends AppCompatActivity {
    String schoolName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_notifications_menu);

        Intent intent = getIntent();
        this.schoolName = intent.getStringExtra("school");

        // Show School Name
        TextView nameView = findViewById(R.id.customizeNotificationNameText);
        nameView.setText(schoolName + ':');

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
        ItemTouchHelper itemTouchHelperMove = new ItemTouchHelper(new MoveCallbackMenu(this, schoolName));
        itemTouchHelperMove.attachToRecyclerView(recyclerView);

        // Adapter
        NutrisliceMenuAdapter adapter = new NutrisliceMenuAdapter(this, schoolName, itemTouchHelperMove);
        recyclerView.setAdapter(adapter);
    }
}