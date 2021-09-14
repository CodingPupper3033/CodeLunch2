package com.codelunch.app.settings.customizeNotifications;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codelunch.app.R;
import com.codelunch.app.searchHandlers.SearchOrganizationHandler;
import com.codelunch.app.settings.adapter.NutrisliceSchoolAdapter;
import com.codelunch.app.settings.customizeNotifications.touchHelper.MoveCallback;
import com.codelunch.app.settings.customizeNotifications.touchHelper.SwipeToDeleteCallback;

public class CustomizeNotificationsSchoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_notifications_school);


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
        ItemTouchHelper itemTouchHelperMove = new ItemTouchHelper(new MoveCallback(this));
        itemTouchHelperMove.attachToRecyclerView(recyclerView);

        // Adapter
        NutrisliceSchoolAdapter adapter = new NutrisliceSchoolAdapter(this, itemTouchHelperMove);
        recyclerView.setAdapter(adapter);

        // Swipe to delete
        ItemTouchHelper itemTouchHelperSwipe = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelperSwipe.attachToRecyclerView(recyclerView);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customize_notifications_menu, menu);

        // Search View
        MenuItem search = menu.findItem(R.id.app_bar_add);
        RecyclerView recyclerView = findViewById(R.id.listViewSearchResults);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        RecyclerView recyclerViewList = findViewById(R.id.schools_recycler_view);

        SearchOrganizationHandler searchOrganizationHandler = new SearchOrganizationHandler(this, recyclerViewList, search, R.id.searchResultsProgressBar, R.id.listViewSearchResults);

        return true;
    }

}