package com.codelunch.app.settings.adapter;

import android.content.Context;
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
import com.codelunch.app.settings.storage.NutrisliceStorage;

import org.json.JSONException;
import org.json.JSONObject;

public class NutrisliceCategoryAdapter extends NutrisliceAdapter<NutrisliceCategoryAdapter.ViewHolder> {
    private final Context context;
    private final ItemTouchHelper moveHelper;
    private final String schoolName;
    private final String menuName;


    public NutrisliceCategoryAdapter(Context context, String schoolName, String menuName, ItemTouchHelper moveHelper) {
        this.context = context;
        this.moveHelper = moveHelper;
        this.schoolName = schoolName;
        this.menuName = menuName;
    }

    @NonNull
    @Override
    public NutrisliceCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customize_school_row_item, parent, false);

        return new NutrisliceCategoryAdapter.ViewHolder(context, schoolName, menuName, moveHelper, v);
    }

    @Override
    public void onBindViewHolder(@NonNull NutrisliceCategoryAdapter.ViewHolder holder, int position) {
        try {
            JSONObject currentObject = NutrisliceStorage.getCategoryData(context, schoolName, menuName).getJSONObject(position);
            String name = currentObject.getString("name");
            holder.setCategory(name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return NutrisliceStorage.getCategoryData(context, schoolName, menuName).length();
    }

    @Override
    public void delete(int pos) {
        NutrisliceStorage.deleteCategory(context, schoolName, menuName, pos);
        notifyItemRemoved(pos);
    }

    public Context getContext() {
        return context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private String schoolName;
        private final ItemTouchHelper moveHelper;
        private final Switch textView;
        private final TextView textViewSub;
        private final ImageView dragImage;
        private final String menuName;
        private String categoryName;

        public ViewHolder(Context context, String schoolName, String menuName, ItemTouchHelper moveHelper, @NonNull View itemView) {
            super(itemView);

            this.context = context;
            this.schoolName = schoolName;
            this.menuName = menuName;
            this.moveHelper = moveHelper;
            this.textView = itemView.findViewById(R.id.text_row_item);
            this.textViewSub = itemView.findViewById(R.id.text_row_subtext);
            this.dragImage = itemView.findViewById(R.id.text_row_image_drag);

            textView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setEnabled(isChecked);
                    setEnabledText(isChecked);
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

        private void setEnabledText(boolean isChecked) {
            if (isChecked) {
                textViewSub.setText(R.string.enabled);
            } else {
                textViewSub.setText(R.string.disabled);
            }
        }

        public void setCategory(String categoryName) {
            this.categoryName = categoryName;

            textView.setChecked(NutrisliceStorage.isCategoryEnabled(context, schoolName, menuName, categoryName));
            setText(categoryName);

            // Make Subtext
            setEnabledText(NutrisliceStorage.isCategoryEnabled(context, schoolName, menuName, categoryName));
        }

        public void setText(String text) {
            textView.setText(text);
        }

        public void setSubText(String text) {
            textViewSub.setText(text);
        }

        public void setEnabled(boolean enabled) {
            NutrisliceStorage.setCategoryEnabled(context, schoolName, menuName, categoryName, enabled);
        }
    }
}
