package com.codelunch.app.settings.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class NutrisliceSchoolAdapter extends NutrisliceAdapter<NutrisliceSchoolAdapter.ViewHolder> {
    Context context;
    ItemTouchHelper moveHelper;


    public NutrisliceSchoolAdapter(Context context, ItemTouchHelper moveHelper) {
        this.context = context;
        this.moveHelper = moveHelper;
    }
    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customize_school_row_item, parent, false);

        return new ViewHolder(context, moveHelper, v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject currentObject = NutrisliceStorage.getData(context).getJSONObject(position);
            String name = currentObject.getString("name");
            holder.setSchool(name);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return NutrisliceStorage.getData(context).length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Switch textView;
        private final TextView textViewSub;
        private final ImageView dragImage;
        private String schoolName;
        private final Context context;
        ItemTouchHelper moveHelper;

        public ViewHolder(Context context, ItemTouchHelper moveHelper, @NonNull View itemView) {
            super(itemView);
            this.context = context;
            this.moveHelper = moveHelper;
            textView = itemView.findViewById(R.id.text_row_item);
            textViewSub = itemView.findViewById(R.id.text_row_subtext);
            dragImage = itemView.findViewById(R.id.text_row_image_drag);

            textView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setEnabled(isChecked);
                }
            });

            // TODO
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
            dragImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    moveHelper.startDrag(holder);
                    return false;
                }
            });
        }

        public void setSchool(String schoolName) {
            this.schoolName = schoolName;
            textView.setChecked(NutrisliceStorage.isSchoolEnabled(context, schoolName));
            setText(schoolName);

            // Make Subtext
            Log.d("TAG", "setSchool: " + schoolName + "  " + NutrisliceStorage.getMenuData(context, schoolName));
            int amount = NutrisliceStorage.getMenuData(context, schoolName).length();
            setSubText(amount + " " + context.getResources().getString(R.string.menus_to_customize));
        }

        public void setText(String text) {
            textView.setText(text);
        }

        public void setSubText(String text) {
            textViewSub.setText(text);
        }

        public void setEnabled(boolean enabled) {
            NutrisliceStorage.setSchoolEnabled(context, schoolName, enabled);
        }

    }

    public void delete(int pos) {
        NutrisliceStorage.deleteSchool(context, pos);
        notifyItemRemoved(pos);
    }
}
