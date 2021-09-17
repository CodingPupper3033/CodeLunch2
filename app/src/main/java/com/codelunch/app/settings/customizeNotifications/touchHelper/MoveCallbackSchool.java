package com.codelunch.app.settings.customizeNotifications.touchHelper;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.codelunch.app.settings.storage.NutrisliceStorage;

public class MoveCallbackSchool extends ItemTouchHelper.SimpleCallback {

    private final Activity activity;

    public MoveCallbackSchool(Activity activity) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0);
        this.activity = activity;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        NutrisliceStorage.moveSchool(activity.getApplicationContext(), fromPosition, toPosition);
        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
