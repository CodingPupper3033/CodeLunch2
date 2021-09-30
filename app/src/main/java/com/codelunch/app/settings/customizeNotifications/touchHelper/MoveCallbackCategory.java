package com.codelunch.app.settings.customizeNotifications.touchHelper;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.codelunch.app.settings.storage.NutrisliceStorage;

public class MoveCallbackCategory extends ItemTouchHelper.SimpleCallback {

    private final Activity activity;
    private final String schoolName;
    private String menuName;

    public MoveCallbackCategory(Activity activity, String schoolName, String menuName) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0);
        this.activity = activity;
        this.schoolName = schoolName;
        this.menuName = menuName;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        NutrisliceStorage.moveCategory(activity.getApplicationContext(), schoolName, menuName, fromPosition, toPosition);
        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
