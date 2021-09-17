package com.codelunch.app.settings.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.codelunch.app.R;
import com.codelunch.app.settings.customizeNotifications.CustomizeNotificationsMenuActivity;
import com.codelunch.app.settings.storage.NutrisliceStorage;

import org.json.JSONException;
import org.json.JSONObject;

public class NutrisliceMenuAdapter extends RecyclerView.Adapter<NutrisliceMenuAdapter.ViewHolder> {
    private final Context context;
    private final ItemTouchHelper moveHelper;
    String schoolName;

    public NutrisliceMenuAdapter(Context context, String schoolName, ItemTouchHelper moveHelper) {
        this.context = context;
        this.moveHelper = moveHelper;
        this.schoolName = schoolName;
    }

    @NonNull
    @Override
    public NutrisliceMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customize_school_row_item, parent, false);

        return new NutrisliceMenuAdapter.ViewHolder(context, schoolName, moveHelper, v);
    }

    @Override
    public void onBindViewHolder(@NonNull NutrisliceMenuAdapter.ViewHolder holder, int position) {
        try {
            JSONObject currentObject = NutrisliceStorage.getMenuData(context, schoolName).getJSONObject(position);
            String name = currentObject.getString("name");
            holder.setMenu(name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return NutrisliceStorage.getMenuData(context, schoolName).length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private String schoolName;
        private final ItemTouchHelper moveHelper;
        private final Switch textView;
        private final TextView textViewSub;
        private final ImageView dragImage;
        private String menuName;

        public ViewHolder(Context context, String schoolName, ItemTouchHelper moveHelper, @NonNull View itemView) {
            super(itemView);

            this.context = context;
            this.schoolName = schoolName;
            this.moveHelper = moveHelper;
            this.textView = itemView.findViewById(R.id.text_row_item);
            this.textViewSub = itemView.findViewById(R.id.text_row_subtext);
            this.dragImage = itemView.findViewById(R.id.text_row_image_drag);

            textView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setEnabled(isChecked);
                }
            });

            textViewSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open Menu Changer
                    Intent intent = new Intent(context, CustomizeNotificationsMenuActivity.class);
                    intent.putExtra("school",schoolName);
                    context.startActivity(intent);
                }
            });

            ViewHolder holder = this;
            dragImage.setOnTouchListener(new View.OnTouchListener() { // Move Image
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    moveHelper.startDrag(holder);
                    return false;
                }
            });
        }

        public void setMenu(String menuName) {
            this.menuName = menuName;
            textView.setChecked(NutrisliceStorage.isMenuEnabled(context, schoolName, menuName));
            setText(menuName);

            // Make Subtext
            int amount = NutrisliceStorage.getCategoryData(context, schoolName, menuName).length();
            setSubText(amount + " " + context.getResources().getString(R.string.categories_to_customize));
        }

        public void setText(String text) {
            textView.setText(text);
        }

        public void setSubText(String text) {
            textViewSub.setText(text);
        }

        public void setEnabled(boolean enabled) {
            NutrisliceStorage.setMenuEnabled(context, schoolName, menuName, enabled);
        }
    }
}
